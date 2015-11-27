package com.awizen.gangehi.rest;

import java.util.Collection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.awizen.gangehi.dtos.WorkflowSummaryDto;
import com.awizen.gangehi.service.ScheduleService;

@Path("/schedule")
@RequestScoped
public class ScheduleRESTService {


	@Inject
	private ScheduleService scheduleService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<WorkflowSummaryDto> listForLogedInUser() {
		String user = "userA";
		int first = 1;
		int pageSize = 5;
		return scheduleService.getTasks(first , pageSize , user);
	}

	@GET
	@Path("/{user}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<WorkflowSummaryDto> lookupMemberById(@PathParam("user") String user) {
		int first = 1;
		int pageSize = 5;
		return scheduleService.getTasks(first, pageSize, user);
	}

}
