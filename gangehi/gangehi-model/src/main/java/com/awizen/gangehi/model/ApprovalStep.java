package com.awizen.gangehi.model;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.awizen.gangehi.enums.ApprovalStepState;
import com.awizen.gangehi.enums.ApprovalStepStateConverter;
import com.awizen.gangehi.listener.AuditEntityListener;

@Data
@EqualsAndHashCode(callSuper=true)
@SuppressWarnings("serial")
@Entity
@EntityListeners(value = { AuditEntityListener.class })
public class ApprovalStep extends AuditableAbstractEntity{

	@NotNull
	private String approver;

	@NotNull
	@Convert(converter = ApprovalStepStateConverter.class)
	private ApprovalStepState approvalState;

	@NotNull
	private int rank;

}
