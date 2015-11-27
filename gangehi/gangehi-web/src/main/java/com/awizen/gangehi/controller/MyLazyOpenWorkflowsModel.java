package com.awizen.gangehi.controller;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.awizen.gangehi.dtos.WorkflowSummaryDto;
import com.awizen.gangehi.service.ScheduleService;

@SuppressWarnings("serial")
public class MyLazyOpenWorkflowsModel extends LazyDataModel<WorkflowSummaryDto> {

	private ScheduleService scheduleService;
	private String userName;

	public MyLazyOpenWorkflowsModel(ScheduleService scheduleService, String userName) {
		this.scheduleService = scheduleService;
		this.userName = userName;
	}

	@Override
	public List<WorkflowSummaryDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		List<WorkflowSummaryDto> tasks = scheduleService.getOpenWorkflows(first, pageSize, userName);
		// TODO: fix to the correct RowCount or
		// or get newest primefaces from ELITE to support unknown row count
		setRowCount(10000);
		return tasks;
	}

}
