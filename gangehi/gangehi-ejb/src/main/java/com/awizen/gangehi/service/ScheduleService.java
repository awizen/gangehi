package com.awizen.gangehi.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Singleton;
import javax.inject.Inject;

import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryContext;
import org.kie.internal.query.QueryFilter;

import com.awizen.gangehi.dtos.ClosedWorkflowSummaryDto;
import com.awizen.gangehi.dtos.WorkflowSummaryDto;

@Singleton
public class ScheduleService {


	@Inject
	private RuntimeDataService runtimeDataService;

	@Inject
	private DTOService dtoService;

	public List<WorkflowSummaryDto> getTasks(int first, int pageSize, String user) {
		Set<Long> processInstanceIds = new HashSet<Long>();

		// FIXME sorting!
		QueryFilter filter = new QueryFilter(first, pageSize);
		for (TaskSummary taskSummary : runtimeDataService.getTasksOwned(user, filter)) {
			processInstanceIds.add(taskSummary.getProcessInstanceId());
		}

		if (!processInstanceIds.isEmpty()) {
			List<WorkflowSummaryDto> listOpenWorkflows = dtoService.listOpenWorkflows(processInstanceIds);
			return listOpenWorkflows;
		}
		return null;
	}

	public List<WorkflowSummaryDto> getOpenWorkflows(int first, int pageSize, String user) {
		List<Integer> states = new ArrayList<Integer>();
		states.add(ProcessInstance.STATE_ACTIVE);
		states.add(ProcessInstance.STATE_PENDING);
		states.add(ProcessInstance.STATE_SUSPENDED);

		Set<Long> processInstanceIds = new HashSet<Long>();
		for (ProcessInstanceDesc processInstanceDesc : getProcessInstances(first, pageSize, states, user)) {
			processInstanceIds.add(processInstanceDesc.getId());
		}

		if (!processInstanceIds.isEmpty()) {
			return dtoService.listOpenWorkflows(processInstanceIds);
		}
		return null;
	}

	public List<ClosedWorkflowSummaryDto> getClosedWorkflows(int first, int pageSize, String initiator) {
		List<Integer> states = new ArrayList<Integer>();
		states.add(ProcessInstance.STATE_ABORTED);
		states.add(ProcessInstance.STATE_COMPLETED);

		Set<Long> processInstanceIds = new HashSet<Long>();
		for (ProcessInstanceDesc processInstanceDesc : getProcessInstances(first, pageSize, states, initiator)) {
			processInstanceIds.add(processInstanceDesc.getId());
		}

		if (!processInstanceIds.isEmpty()) {
			return dtoService.listClosedWorkflows(processInstanceIds);
		}
		return null;
	}

	private Collection<ProcessInstanceDesc> getProcessInstances(int first, int pageSize, List<Integer> states, String initiator) {
		QueryContext queryContext = new QueryContext(first, pageSize);
		Collection<ProcessInstanceDesc> processInstances = runtimeDataService.getProcessInstances(states, initiator, queryContext);
		return processInstances;
	}
}
