package com.awizen.gangehi.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;

import com.awizen.gangehi.enums.ApprovalStepState;
import com.awizen.gangehi.model.ApprovalStep;

import lombok.Data;

@SuppressWarnings("serial")
@Data
@Named
@ViewScoped
public class ApproverListView implements Serializable{

	private List<ApprovalStep> approverList = new ArrayList<ApprovalStep>();

	private String nextApprover;

	private ApprovalStep selected;

	private Long fakeStepId = -1l;

	public void onSelect(SelectEvent event) {
		selected = (ApprovalStep) event.getObject();
	}


	public void onReorder(AjaxBehaviorEvent event) {
		int rank = 1;
		for (ApprovalStep approvalStep : approverList) {
			approvalStep.setRank(rank++);
		}
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "List Reordered", null));
	}

	public void addApprover(){
		int rank = approverList.size() + 1;

		ApprovalStep step = new ApprovalStep();
		step.setId(fakeStepId--);
		step.setApprover(nextApprover);
		step.setApprovalState(ApprovalStepState.OPEN);
		step.setRank(rank);
		approverList.add(step);
		nextApprover = null;
	}

	public void deleteApprover(){
		approverList.remove(selected);
	}
}
