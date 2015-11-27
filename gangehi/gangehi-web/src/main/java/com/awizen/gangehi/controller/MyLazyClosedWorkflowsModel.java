package com.awizen.gangehi.controller;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.awizen.gangehi.dtos.ClosedWorkflowSummaryDto;
import com.awizen.gangehi.service.ScheduleService;

@SuppressWarnings("serial")
public class MyLazyClosedWorkflowsModel extends LazyDataModel<ClosedWorkflowSummaryDto> {

	private ScheduleService scheduleService;
	private String userName;

	public MyLazyClosedWorkflowsModel(ScheduleService scheduleService, String userName) {
		this.scheduleService = scheduleService;
		this.userName = userName;
	}

	@Override
	public List<ClosedWorkflowSummaryDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		List<ClosedWorkflowSummaryDto> tasks = scheduleService.getClosedWorkflows(first, pageSize, userName);
		// TODO: fix to the correct RowCount or
		// or get newest primefaces from ELITE to support unknown row count
		setRowCount(10000);
		return tasks;
	}

}
