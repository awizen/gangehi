package com.awizen.gangehi.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.awizen.gangehi.enums.ApprovalStepState;
import com.awizen.gangehi.model.ApprovalStep;
import com.awizen.gangehi.model.SimpleApproval;

public class MySendTaskHandler implements WorkItemHandler {

	public static final String NOTIFICATION_TYPE = "notificationType";
	public static final String NOTIFY_NEXT_APPROVER = "notifyNextApprover";
	public static final String NOTIFY_ORIGINATOR = "notifyOriginator";
	public static final String ABORTED_NOTIFICATION = "abortedNotification";
	public static final String AUTHORISED_NOTIFICATION = "authorisedNotification";
	public static final String PROCESS_INSTANCE_ID = "processInstanceId";

	@Inject
	private PropertyService propertyService;

	@Inject
	private SimpleApprovalDAO simpleApprovalDAO;

	@Inject
	private EmailService emailService;

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		long processInstanceId = workItem.getProcessInstanceId();
		String nextApprover = (String) workItem.getParameter(SimpleWFService.NEXT_APPROVER);
		String notificationType = (String) workItem.getParameter(NOTIFICATION_TYPE);
		String approvalDenier = (String) workItem.getParameter(SimpleWFService.APPROVAL_DENIER);

		SimpleApproval approval = simpleApprovalDAO.findSimpleApproval(processInstanceId);

		String originator = approval.getCreatedBy();
		String replyTo = getReplyTo(notificationType, originator, approvalDenier);
		String subjectPrefix = getSubjectPrefix(notificationType);
		String applicationUrl = propertyService.getString("mail.application.url");
		String emailSubject = subjectPrefix + approval.getSubject();

		Map<String, String> data = new HashMap<String, String>();

		String taskUrl = getTaskUrl(applicationUrl, processInstanceId);

		data.put("taskUrl", taskUrl);
		data.put("taskSubject", approval.getSubject());
		data.put("rejector", approvalDenier);
		data.put("originator", originator);

		Set<String> toSet = getToSet(nextApprover, notificationType, approval, originator);
		String templateFileName = getTemplate(notificationType);

		emailService.sendMessage(replyTo, emailSubject, data, toSet, templateFileName);

		manager.completeWorkItem(workItem.getId(), null);
	}

	private String getTaskUrl(String applicationUrl, long processInstanceId) {
		return applicationUrl + "secure/simpleWf.xhtml?processInstanceId=" + processInstanceId;
	}

	private String getReplyTo(String notificationType, String originator, String approvalDenier) {

		switch (notificationType) {
		case NOTIFY_NEXT_APPROVER:
		case ABORTED_NOTIFICATION:
		case AUTHORISED_NOTIFICATION:
			return originator;

		case NOTIFY_ORIGINATOR:
			return approvalDenier;

		default:
			throw new IllegalArgumentException("Unknown notification type!");
		}
	}

	private Set<String> getToSet(String nextApprover, String notificationType, SimpleApproval approval, String originator) {

		Set<String> toSet = new HashSet<String>();

		switch (notificationType) {
		case NOTIFY_NEXT_APPROVER:
		case NOTIFY_ORIGINATOR:
			toSet.add(nextApprover);
			break;

		case AUTHORISED_NOTIFICATION:
			toSet.add(originator);
			// no break after this case!

		case ABORTED_NOTIFICATION:
			List<ApprovalStep> approvalSteps = approval.getApprovalSteps();
			for (ApprovalStep approvalStep : approvalSteps) {
				// TODO: restrict toSet only to users who really were involved
				// in the approval process: try to use approval history
				if (!ApprovalStepState.OPEN.equals(approvalStep.getApprovalState())) {
					toSet.add(approvalStep.getApprover());
				}
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown notification type!");
		}

		return toSet;
	}

	private String getSubjectPrefix(String notificationType) {

		switch (notificationType) {
		case NOTIFY_NEXT_APPROVER:
			return "Gangehi Approval: ";

		case NOTIFY_ORIGINATOR:
			return "Gangehi Approval rejected: ";

		case ABORTED_NOTIFICATION:
			return "Gangehi Approval aborted: ";

		case AUTHORISED_NOTIFICATION:
			return "Gangehi Approval authorised: ";

		default:
			throw new IllegalArgumentException("Unknown notification type!");
		}
	}

	private String getTemplate(String notificationType) {

		switch (notificationType) {
		case NOTIFY_NEXT_APPROVER:
			return "mail-next-approver.ftl";

		case NOTIFY_ORIGINATOR:
			return "mail-notify-originator.ftl";

		case ABORTED_NOTIFICATION:
			return "mail-aborted-notification.ftl";

		case AUTHORISED_NOTIFICATION:
			return "mail-authorised-notification.ftl";

		default:
			throw new IllegalArgumentException("Unknown notification type!");
		}
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

	}

}
