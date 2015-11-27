package com.awizen.gangehi.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import lombok.Getter;

import org.kie.internal.identity.IdentityProvider;
import org.primefaces.model.LazyDataModel;

import com.awizen.gangehi.dtos.ClosedWorkflowSummaryDto;
import com.awizen.gangehi.dtos.WorkflowSummaryDto;
import com.awizen.gangehi.service.ScheduleService;

@Model
public class ScheduleController {

	@Inject
	private ScheduleService scheduleService;

	@Inject
	private IdentityProvider identityProvider;

	@Getter
	private LazyDataModel<WorkflowSummaryDto> myLazyTasksModel;

	@Getter
	private LazyDataModel<WorkflowSummaryDto> myLazyOpenWorkflowsModel;

	@Getter
	private LazyDataModel<ClosedWorkflowSummaryDto> myLazyClosedWorkflowsModel;


	@PostConstruct
	public void init() {
		myLazyTasksModel = new MyLazyTasksModel(scheduleService, identityProvider.getName());
		myLazyOpenWorkflowsModel = new MyLazyOpenWorkflowsModel(scheduleService, identityProvider.getName());
		myLazyClosedWorkflowsModel = new MyLazyClosedWorkflowsModel(scheduleService, identityProvider.getName());
	}

}
