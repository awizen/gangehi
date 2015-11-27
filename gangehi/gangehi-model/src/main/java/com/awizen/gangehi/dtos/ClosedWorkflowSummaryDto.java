package com.awizen.gangehi.dtos;

import java.util.Date;

import lombok.Data;

@Data
public class ClosedWorkflowSummaryDto {

	public ClosedWorkflowSummaryDto(Long processInstanceId, String subject, String initiator, Date createdOn, Date dueDate, Date endDate) {
		super();
		this.processInstanceId = processInstanceId;
		this.subject = subject;
		this.initiator = initiator;
		this.createdOn = createdOn;
		this.dueDate = dueDate;
		this.endDate = endDate;
	}

	private Long processInstanceId;
	private String subject;
	private String initiator;
	private Date createdOn;
	private Date dueDate;
	private Date endDate;
}
