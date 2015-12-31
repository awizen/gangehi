package com.awizen.gangehi.model;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import com.awizen.gangehi.enums.ApprovalStepState;
import com.awizen.gangehi.enums.ApprovalStepStateConverter;
import com.awizen.gangehi.listener.AuditEntityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@SuppressWarnings("serial")
@Entity
@NamedQueries({
	@NamedQuery(name = ApprovalStep.IS_USER_INVOLVED, query = "SELECT COUNT(step) FROM SimpleApproval sapp, ApprovalStep step "
			+ "WHERE step MEMBER OF sapp.approvalSteps AND sapp.processInstanceId = :processInstanceId AND LOWER(step.approver) = LOWER(:userId)")
})
@EntityListeners(value = { AuditEntityListener.class })
public class ApprovalStep extends AuditableAbstractEntity{

	public static final String IS_USER_INVOLVED = "ApprovalStep.isUserInvolved";

	@NotNull
	private String approver;

	@NotNull
	@Convert(converter = ApprovalStepStateConverter.class)
	private ApprovalStepState approvalState;

	@NotNull
	private int rank;

}
