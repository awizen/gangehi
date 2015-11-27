package com.awizen.gangehi.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.awizen.gangehi.listener.AuditEntityListener;

@Data
@EqualsAndHashCode(callSuper=true)
@SuppressWarnings("serial")
@Entity
@EntityListeners(value = { AuditEntityListener.class })
public class FileEntity extends AuditableAbstractEntity{

	@NotNull
	private String fileName;

	@NotNull
	@Lob
	private byte[] content;

	private String contentType;
}
