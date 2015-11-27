package com.awizen.gangehi.dtos;

import java.util.Date;

import lombok.Data;

@Data
public class WorkflowSummaryDto {

	public WorkflowSummaryDto(Long taskId, Long processInstanceId, String subject, String initiator, String actualOwner, Date createdOn, Date dueDate) {
		super();
		this.taskId = taskId;
		this.processInstanceId = processInstanceId;
		this.subject = subject;
		this.initiator = initiator;
		this.actualOwner = actualOwner;
		this.createdOn = createdOn;
		this.dueDate = dueDate;
	}

	private Long taskId;
	private Long processInstanceId;
	private String subject;
	private String initiator;
	private String actualOwner;
	private Date createdOn;
	private Date dueDate;
}
