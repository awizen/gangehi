package com.awizen.gangehi.controller;

import lombok.Data;

@Data
public class SimpleApprovalDialogState {

	private boolean startButtonRendered = false;
	private boolean deleteButtonRendered = false;
	private boolean rejectButtonRendered = false;
	private boolean abortButtonRendered = false;
	private boolean approveButtonRendered = false;
	private boolean workflowStateStyleRendered = false;
	private boolean commentRendered = false;
	private boolean historyRendered = true;
	private boolean readOnly = true;
	private String workflowStateStyle;

}
