package com.awizen.gangehi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Index;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.hibernate.validator.constraints.NotEmpty;

import com.awizen.gangehi.listener.AuditEntityListener;


@Data
@EqualsAndHashCode(callSuper=true)
@SuppressWarnings("serial")
@Entity
@EntityListeners(value = { AuditEntityListener.class })
@NamedQuery(name = Property.FIND_BY_KEY, query="SELECT p FROM Property p WHERE p.key = :key")
@Table(indexes = @Index(columnList = "keyy"))
public class Property extends AuditableAbstractEntity{

	public static final String FIND_BY_KEY = "Property.findByKey";

	@NotNull
	@NotEmpty
	@Column(unique=true, name="keyy")
	private String key;

	@NotNull
	@NotEmpty
	private String value;

	@NotNull
	@NotEmpty
	private String description;

}
