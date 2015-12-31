package com.awizen.gangehi.controller;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.Task;
import org.kie.internal.identity.IdentityProvider;
import org.primefaces.context.RequestContext;

import com.awizen.gangehi.enums.ApprovalStepState;
import com.awizen.gangehi.model.ApprovalHistory;
import com.awizen.gangehi.model.ApprovalStep;
import com.awizen.gangehi.model.FileEntity;
import com.awizen.gangehi.model.SimpleApproval;
import com.awizen.gangehi.model.User;
import com.awizen.gangehi.service.SimpleWFService;
import com.awizen.gangehi.service.UserDAO;
import com.awizen.gangehi.service.UserService;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Named
@ViewScoped
public class SimpleWFController implements Serializable {

	// TODO make it configurable
	private static final int DEFAULT_DUE_PERIOD = 2;

	@Inject
	private Logger log;

	@Inject
	private SimpleWFService simpleWFService;

	@Inject
	private UserService userService;

	@Inject
	private UserDAO userDAO;

	@Inject
	private ApproverListView approverListView;

	@Inject
	private FileListView fileListView;

	@Inject
	private transient IdentityProvider identityProvider;

	private SimpleApproval simpleApproval;

	private ApprovalHistory currentApprovalHistory = new ApprovalHistory();

	private List<ApprovalHistory> approvalHistory;

	private SimpleApprovalDialogState dialogState;

	@Setter
	@Getter
	private Long processInstanceId;

	@Setter
	@Getter
	private Long taskId;

	private Task task;

	@Setter
	@Getter
	private String templateName;

	@Getter
	private List<String> unknownApprovers = new ArrayList<>();

	@Produces
	@Named
	public ApprovalHistory getCurrentApprovalHistory() {
		return currentApprovalHistory;
	}

	@Produces
	@Named
	public SimpleApproval getSimpleApproval() {
		return simpleApproval;
	}

	@Produces
	@Named
	public SimpleApprovalDialogState getDialogState() {
		return dialogState;
	}

	@PostConstruct
	private void initialize() {
		simpleApproval = new SimpleApproval();
		simpleApproval.setFiles(new ArrayList<FileEntity>());

		dialogState = new SimpleApprovalDialogState();
	}

	@Produces
	@Named
	public List<ApprovalHistory> getApprovalHistory() {
		return approvalHistory;
	}

	public void initializeSimpleApproval() {

		boolean potentialOwner = false;
		String currentUser = identityProvider.getName();

		// initialize the controller only once per view life-cycle
		if (!FacesContext.getCurrentInstance().isPostback()) {

			if (processInstanceId != null) {

				simpleApproval = simpleWFService.getSimpleApproval(processInstanceId);

				if (simpleWFService.isAuthorizedToView(currentUser, simpleApproval)) {
					initForExistingProcess(potentialOwner, currentUser);
				} else {
					// show an empty approval page if the user is not authorized to view
					simpleApproval = new SimpleApproval();
					simpleApproval.setFiles(new ArrayList<FileEntity>());
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "You are not authorised to view this data!", "The comment can't be empty."));
					log.warning("Unauthorised access to the processInstanceId: '" + processInstanceId + "' by the user '" + currentUser + "'");
				}


			} else {
				initForNewProcess();
			}
			approvalHistory = simpleWFService.getApprovalHistory(simpleApproval);
			if (approvalHistory.isEmpty()) {
				dialogState.setHistoryRendered(false);
			}
		}
		setWorkflowStateStyle();
	}

	private void initForNewProcess() {
		dialogState.setStartButtonRendered(true);
		// TODO consider UTC and time zone from the browser
		LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
		localDateTime.plusDays(DEFAULT_DUE_PERIOD);
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		Date dueDate = Date.from(instant);
		simpleApproval.setDueDate(dueDate);
		dialogState.setReadOnly(false);
		dialogState.setCommentRendered(false);
		dialogState.setHistoryRendered(false);
	}

	private void initForExistingProcess(boolean potentialOwner, String currentUser) {
		if (taskId == null) {
			taskId = simpleWFService.findTaksId(processInstanceId);
		} else {
			task = simpleWFService.getTask(taskId);
			PeopleAssignments peopleAssignments = task.getPeopleAssignments();
			List<OrganizationalEntity> potentialOwners = peopleAssignments.getPotentialOwners();
			for (OrganizationalEntity po : potentialOwners) {
				potentialOwner = po.getId().equalsIgnoreCase(currentUser);
			}
		}

		List<ApprovalStep> approvalSteps = simpleApproval.getApprovalSteps();
		for (ApprovalStep approvalStep : approvalSteps) {
			if (approvalStep.getApprovalState().equals(ApprovalStepState.OPEN)) {
				approverListView.getApproverList().add(approvalStep);
			}
		}

		List<FileEntity> files = simpleApproval.getFiles();
		for (FileEntity fileEntity : files) {
			fileListView.getFileList().add(fileEntity);
		}

		if (potentialOwner && taskId != null) {
			if (task.getName().equals("create approval item")) {
				dialogState.setDeleteButtonRendered(true);
				dialogState.setStartButtonRendered(true);
				dialogState.setReadOnly(false);
				dialogState.setCommentRendered(false);
			}

			if (task.getName().equals("edit approval item")) {
				dialogState.setApproveButtonRendered(true);
				dialogState.setAbortButtonRendered(true);
				dialogState.setRejectButtonRendered(false);
				dialogState.setReadOnly(false);
				dialogState.setCommentRendered(true);
			}

			if (task.getName().equals("next approve")) {
				dialogState.setRejectButtonRendered(true);
				dialogState.setApproveButtonRendered(true);
				dialogState.setCommentRendered(true);
			}
		}
	}

	private void setWorkflowStateStyle() {

		if (simpleApproval.getWorkflowState() != null) {
			dialogState.setWorkflowStateStyleRendered(true);
			switch (simpleApproval.getWorkflowState()) {
			case AUTHORIZED:
				dialogState.setWorkflowStateStyle("gg-workflowStateStyleGreen");
				break;

			case REJECTED:
			case ABORTED:
				dialogState.setWorkflowStateStyle("gg-workflowStateStyleRed");
				break;

			default:
				break;
			}
		} else {
			dialogState.setWorkflowStateStyleRendered(false);
		}
	}

	public String startApproval() {

		// validate approval steps on start
		if (approverListView.getApproverList().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please define the approver list.", "The approver list can't be empty."));
			return null;
		}


		// are there unregistered user in the list?
		checkForUnknownApprover();
		if (! unknownApprovers.isEmpty()) {
			RequestContext.getCurrentInstance().addCallbackParam("unregisteredApprovers", true);
			return null;
		}

		try {
			simpleWFService.saveSimpleApproval(simpleApproval, currentApprovalHistory, approverListView.getApproverList(), fileListView.getFileList());
			log.info("startApproval: " + simpleApproval.getSubject());

			currentApprovalHistory.setId(null);
			simpleWFService.approve(simpleApproval, currentApprovalHistory);

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Approval Unsuccessful");
			FacesContext.getCurrentInstance().addMessage(null, m);
			return null;
		}

		return "schedule";
	}

	private void checkForUnknownApprover() {

		unknownApprovers.removeAll(unknownApprovers);

		List<ApprovalStep> approverList = approverListView.getApproverList();
		for (ApprovalStep approvalStep : approverList) {
			User user = userDAO.findUserByUserId(approvalStep.getApprover());
			if (user == null || !user.isEmailVerified()) {
				unknownApprovers.add(approvalStep.getApprover());
			}
		}
	}

	public String delete() {
		log.info("delete: " + simpleApproval.getSubject());
		simpleWFService.delete(simpleApproval, currentApprovalHistory);
		return "schedule";
	}

	public String save() {
		log.info("save: " + simpleApproval.getSubject());
		simpleWFService.saveSimpleApproval(simpleApproval, currentApprovalHistory, approverListView.getApproverList(), fileListView.getFileList());
		return "schedule";
	}

	public String saveAsTemplate() {
		log.info("save as template: " + simpleApproval.getSubject());
		simpleWFService.saveSimpleApproval(simpleApproval, currentApprovalHistory, approverListView.getApproverList(), fileListView.getFileList());
		return "schedule";
	}

	public String startApproverListVerification() {
		log.info("Start Approver List Verification: " + simpleApproval.getSubject());

		for (String unknownApprover : unknownApprovers) {

			User user = userDAO.findUserByUserId(unknownApprover);
			if (user == null) {
				user = new User();
				user.setUserId(unknownApprover);
				user.setPasswd(UUID.randomUUID().toString());

				userService.register(user);
			}
			userService.sendInvitationEmail(user, identityProvider.getName());

		}

		simpleWFService.saveSimpleApproval(simpleApproval, currentApprovalHistory, approverListView.getApproverList(), fileListView.getFileList());

		return "schedule";
	}

	public String reject() {

		// validate comment
		if (StringUtils.isBlank(currentApprovalHistory.getComment())) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please comment your rejection.", "The comment can't be empty."));
			return null;
		}

		log.info("reject: " + simpleApproval.getSubject());
		simpleWFService.reject(simpleApproval, currentApprovalHistory);
		return "schedule";
	}

	public String abort() {

		// validate comment
		if (StringUtils.isBlank(currentApprovalHistory.getComment())) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please comment your abortion.", "The comment can't be empty."));
			return null;
		}

		log.info("abort: " + simpleApproval.getSubject());
		simpleWFService.abort(simpleApproval, currentApprovalHistory);
		return "schedule";
	}

	public String approve() {
		log.info("approve: " + simpleApproval.getSubject());
		simpleWFService.approve(simpleApproval, currentApprovalHistory);
		return "schedule";
	}

	private String getRootErrorMessage(Exception e) {
		String errorMessage = "Operation failed. See server log for more information";
		if (e == null) {
			return errorMessage;
		}

		Throwable t = e;
		while (t != null) {
			errorMessage = t.getLocalizedMessage();
			t = t.getCause();
		}
		return errorMessage;
	}

}
