package com.awizen.gangehi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.task.impl.model.UserImpl;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.query.QueryFilter;

import com.awizen.gangehi.enums.ApprovalStepState;
import com.awizen.gangehi.enums.WorkflowState;
import com.awizen.gangehi.model.ApprovalHistory;
import com.awizen.gangehi.model.ApprovalStep;
import com.awizen.gangehi.model.FileEntity;
import com.awizen.gangehi.model.SimpleApproval;
import com.awizen.gangehi.util.StartupBean;

@Singleton
public class SimpleWFService {

	public static final String NEXT_APPROVER = "nextApprover";
	public static final String APPROVAL_DENIER = "approvalDenier";

	private static final String SYSTEM_USER_ADMINISTRATOR = "Administrator";
	private static final String USER_ACTION = "userAction";
	private static final String USER_ACTION_SAVE = "save";
	private static final String USER_ACTION_DELETE = "delete";
	private static final String USER_ACTION_REJECT = "reject";
	private static final String USER_ACTION_ABORT = "abort";
	private static final String USER_ACTION_APPROVE = "approve";
	private static final String USER_ACTION_AUTHORISE = "authorise";

	@Inject
	private SimpleApprovalDAO simpleApprovalDAO;

	@Inject
	private ApprovalStepDAO approvalStepDAO;

	@Inject
	private BaseDAO baseDAO;

	@Inject
	private ProcessService processService;

	@Inject
	private UserTaskService userTaskService;

	@Inject
	private RuntimeDataService runtimeDataService;

	@Inject
	private Event<ApprovalHistory> approvalHistoryEvent;

	@Inject
	private ApprovalHistoryDAO approvalHistoryDAO;

	@Inject
	private IdentityProvider identityProvider;

	public void saveSimpleApproval(SimpleApproval simpleApproval, ApprovalHistory approvalHistory, List<ApprovalStep> approverList, List<FileEntity> fileList) {

		refreschApprovalPlan(simpleApproval, approverList);

		if (simpleApproval.getProcessInstanceId() == null) {
			Long processInstanceId = startProcess();
			simpleApproval.setProcessInstanceId(processInstanceId);
			simpleApproval.setWorkflowState(WorkflowState.NEW);
			simpleApprovalDAO.persist(simpleApproval);
		} else {
			simpleApprovalDAO.merge(simpleApproval);
		}

		updatePersistentFileList(simpleApproval, fileList);

		TaskSummary taskSummary = getTask(simpleApproval);
		fireApprovalHistory(approvalHistory, taskSummary, simpleApproval, USER_ACTION_SAVE);

	}

	private ApprovalStep determineNextApprovalStep(SimpleApproval simpleApproval) {
		ApprovalStep nextApprovalStep = null;
		Set<ApprovalStep> approvalSteps = simpleApproval.getApprovalSteps();
		int lowestRank = Integer.MAX_VALUE;
		for (ApprovalStep approvalStep : approvalSteps) {

			if (approvalStep.getApprovalState().equals(ApprovalStepState.SCHEDULED)) {
				approvalStep.setApprovalState(ApprovalStepState.APPROVED);
				approvalStepDAO.merge(approvalStep);
			}

			if (approvalStep.getApprovalState().equals(ApprovalStepState.OPEN)) {
				int stepRank = approvalStep.getRank();
				if (lowestRank > stepRank) {
					lowestRank = stepRank;
					nextApprovalStep = approvalStep;
				}
			}
		}
		if (nextApprovalStep != null) {
			nextApprovalStep.setApprovalState(ApprovalStepState.SCHEDULED);
			approvalStepDAO.merge(nextApprovalStep);
		}
		return nextApprovalStep;
	}

	private void resetApprovalSteps(SimpleApproval simpleApproval) {
		Set<ApprovalStep> approvalSteps = simpleApproval.getApprovalSteps();
		for (ApprovalStep approvalStep : approvalSteps) {
			if (!ApprovalStepState.OPEN.equals(approvalStep.getApprovalState())) {
				approvalStep.setApprovalState(ApprovalStepState.OPEN);
				approvalStepDAO.merge(approvalStep);
			}
		}
	}

	public Long startProcess() {

		String currentUser = identityProvider.getName();
		long processInstanceId = processService.startProcess(StartupBean.PROCESSES_DEPLOYMENT_ID, "com.awizen.gangehi.simple_approval");
		List<Long> tasksByProcessInstanceId = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
		Long taskId = tasksByProcessInstanceId.get(0);
		List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();
		potentialOwners.add(new UserImpl(currentUser));
		userTaskService.nominate(taskId, SYSTEM_USER_ADMINISTRATOR, potentialOwners);
		userTaskService.start(taskId, currentUser);

		return processInstanceId;

	}

	public SimpleApproval getSimpleApproval(long processInstanceId) {
		return simpleApprovalDAO.findSimpleApproval(processInstanceId);
	}

	public Task getTask(Long taskId) {
		return userTaskService.getTask(taskId);
	}

	public void delete(SimpleApproval simpleApproval, ApprovalHistory approvalHistory) {
		TaskSummary taskSummary = getTask(simpleApproval);

		Map<String, Object> results = new HashMap<String, Object>();
		results.put(USER_ACTION, USER_ACTION_DELETE);
		userTaskService.complete(taskSummary.getId(), identityProvider.getName(), results);

		simpleApproval.setWorkflowState(WorkflowState.DELETED);
		simpleApprovalDAO.merge(simpleApproval);
		fireApprovalHistory(approvalHistory, taskSummary, simpleApproval, USER_ACTION_DELETE);
	}

	private void fireApprovalHistory(ApprovalHistory approvalHistory, TaskSummary taskSummary, SimpleApproval simpleApproval, String action) {
		approvalHistory.setTaskId(taskSummary.getId());
		approvalHistory.setProcessInstanceId(taskSummary.getProcessInstanceId());
		approvalHistory.setAction(action);

		approvalHistoryEvent.fire(approvalHistory);
	}

	public void approve(SimpleApproval simpleApproval, ApprovalHistory approvalHistory) {

		TaskSummary taskSummary = getTask(simpleApproval);

		if (taskSummary.getStatus().equals(Status.Reserved)) {
			userTaskService.start(taskSummary.getId(), identityProvider.getName());
		}

		Map<String, Object> results = new HashMap<String, Object>();
		ApprovalStep nextApprovalStep = determineNextApprovalStep(simpleApproval);
		results.put(USER_ACTION, USER_ACTION_APPROVE);

		if (nextApprovalStep != null) {
			results.put(NEXT_APPROVER, nextApprovalStep.getApprover().toLowerCase(Locale.ROOT));
			results.put(MySendTaskHandler.NOTIFICATION_TYPE, MySendTaskHandler.NOTIFY_NEXT_APPROVER);
			userTaskService.complete(taskSummary.getId(), identityProvider.getName(), results);
			simpleApproval.setWorkflowState(WorkflowState.IN_PROGRESS);
			simpleApprovalDAO.merge(simpleApproval);
			fireApprovalHistory(approvalHistory, taskSummary, simpleApproval, USER_ACTION_APPROVE);
		} else {
			results.put(NEXT_APPROVER, null);
			results.put(MySendTaskHandler.NOTIFICATION_TYPE, MySendTaskHandler.AUTHORISED_NOTIFICATION);
			results.put(MySendTaskHandler.PROCESS_INSTANCE_ID, simpleApproval.getProcessInstanceId());
			userTaskService.complete(taskSummary.getId(), identityProvider.getName(), results);
			simpleApproval.setWorkflowState(WorkflowState.AUTHORIZED);
			simpleApprovalDAO.merge(simpleApproval);
			fireApprovalHistory(approvalHistory, taskSummary, simpleApproval, USER_ACTION_AUTHORISE);
		}
	}

	private TaskSummary getTask(SimpleApproval simpleApproval) {
		List<Status> statusList = new ArrayList<Status>();
		statusList.add(Status.InProgress);
		statusList.add(Status.Reserved);
		List<TaskSummary> summaryList = runtimeDataService.getTasksByStatusByProcessInstanceId(simpleApproval.getProcessInstanceId(), statusList, null);
		TaskSummary taskSummary = summaryList.get(0);

		return taskSummary;
	}

	public void reject(SimpleApproval simpleApproval, ApprovalHistory approvalHistory) {
		TaskSummary taskSummary = getTask(simpleApproval);

		String currentUser = identityProvider.getName();
		if (taskSummary.getStatus().equals(Status.Reserved)) {
			userTaskService.start(taskSummary.getId(), currentUser);
		}

		Map<String, Object> results = new HashMap<String, Object>();
		results.put(USER_ACTION, USER_ACTION_REJECT);
		// give back to the initiator
		results.put(NEXT_APPROVER, simpleApproval.getCreatedBy());
		results.put(MySendTaskHandler.NOTIFICATION_TYPE, MySendTaskHandler.NOTIFY_ORIGINATOR);
		results.put(APPROVAL_DENIER, currentUser);
		userTaskService.complete(taskSummary.getId(), currentUser, results);

		simpleApproval.setWorkflowState(WorkflowState.REJECTED);
		simpleApprovalDAO.merge(simpleApproval);

		resetApprovalSteps(simpleApproval);

		fireApprovalHistory(approvalHistory, taskSummary, simpleApproval, USER_ACTION_REJECT);
	}

	public void abort(SimpleApproval simpleApproval, ApprovalHistory approvalHistory) {
		TaskSummary taskSummary = getTask(simpleApproval);

		if (taskSummary.getStatus().equals(Status.Reserved)) {
			userTaskService.start(taskSummary.getId(), identityProvider.getName());
		}

		Map<String, Object> results = new HashMap<String, Object>();
		results.put(USER_ACTION, USER_ACTION_ABORT);
		results.put(MySendTaskHandler.NOTIFICATION_TYPE, MySendTaskHandler.ABORTED_NOTIFICATION);
		results.put(MySendTaskHandler.PROCESS_INSTANCE_ID, simpleApproval.getProcessInstanceId());
		userTaskService.complete(taskSummary.getId(), identityProvider.getName(), results);

		simpleApproval.setWorkflowState(WorkflowState.ABORTED);
		simpleApprovalDAO.merge(simpleApproval);

		fireApprovalHistory(approvalHistory, taskSummary, simpleApproval, USER_ACTION_ABORT);
	}

	public List<ApprovalHistory> getApprovalHistory(SimpleApproval simpleApproval) {
		return approvalHistoryDAO.findApprovalHistory(simpleApproval.getProcessInstanceId());
	}

	private void refreschApprovalPlan(SimpleApproval simpleApproval, List<ApprovalStep> approverList) {
		Set<ApprovalStep> approvalSteps = new HashSet<>();
		for (ApprovalStep approvalStep : approverList) {
			if (approvalStep.getId() == null || approvalStep.getId() < 0) {
				approvalStep.setId(null);
				approvalStep = approvalStepDAO.persist(approvalStep);
			}
			approvalSteps.add(approvalStep);
		}
		simpleApproval.setApprovalSteps(approvalSteps);
	}

	private void updatePersistentFileList(SimpleApproval simpleApproval, List<FileEntity> currentFileList) {

		Set<Long> persistentFilesToRetain = new HashSet<Long>();
		boolean anyChanges = false;

		for (FileEntity fileEntity : currentFileList) {
			if (fileEntity.getId() != null) {
				if (fileEntity.getId() > 0) {
					persistentFilesToRetain.add(fileEntity.getId());
				} else {
					// remove fake id
					fileEntity.setId(null);
				}
			}
		}

		// a loop to delete removed files
		Set<FileEntity> persistentFiles = simpleApproval.getFiles();
		Iterator<FileEntity> persistentFilesIterator = persistentFiles.iterator();
		while (persistentFilesIterator.hasNext()) {
			FileEntity fileEntity = persistentFilesIterator.next();
			if (!persistentFilesToRetain.contains(fileEntity.getId())) {
				persistentFilesIterator.remove();
				anyChanges = true;
			}
		}

		// a loop to insert new files
		for (FileEntity fileEntity : currentFileList) {
			if (fileEntity.getId() == null) {
				baseDAO.persist(fileEntity);
				simpleApproval.getFiles().add(fileEntity);
				anyChanges = true;
			}
		}

		if (anyChanges) {
			baseDAO.merge(simpleApproval);
		}
	}

	public Long findTaksId(Long processInstanceId) {
		List<Status> status = null;
		QueryFilter filter = null;
		List<TaskSummary> list = runtimeDataService.getTasksByStatusByProcessInstanceId(processInstanceId, status, filter);

		for (TaskSummary taskSummary : list) {
			String currentUser = identityProvider.getName();
			if (currentUser.equals(taskSummary.getActualOwnerId()) && Status.Reserved.equals(taskSummary.getStatus())) {
				return taskSummary.getId();
			}
		}

		return null;
	}

	public boolean isAuthorizedToView(String currentUser, SimpleApproval simpleApproval) {
		if (simpleApproval != null) {
			Boolean userInvolved = approvalStepDAO.isUserInvolved(simpleApproval.getProcessInstanceId(), currentUser);
			boolean createdByCurrentUser = simpleApproval.getCreatedBy().equalsIgnoreCase(currentUser);
			return userInvolved || createdByCurrentUser;
		} else {
			return false;
		}
	}

}
