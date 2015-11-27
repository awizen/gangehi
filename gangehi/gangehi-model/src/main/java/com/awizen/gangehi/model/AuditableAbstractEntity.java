package com.awizen.gangehi.model;

import java.util.Date;

import javax.persistence.EntityListeners;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.hibernate.validator.constraints.NotEmpty;

import com.awizen.gangehi.listener.AuditEntityListener;

@SuppressWarnings("serial")
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper=true)
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(value = { AuditEntityListener.class })
public abstract class AuditableAbstractEntity extends AbstractEntity {

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date changedOn;

	@NotNull
	@NotEmpty
	private String createdBy;

	@NotNull
	@NotEmpty
	private String changedBy;

}
