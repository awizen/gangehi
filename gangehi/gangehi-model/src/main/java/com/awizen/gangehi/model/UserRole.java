package com.awizen.gangehi.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.hibernate.validator.constraints.NotEmpty;

import com.awizen.gangehi.listener.AuditEntityListener;


@Data
@EqualsAndHashCode(callSuper=true)
@SuppressWarnings("serial")
@Entity
@NamedQuery(name = UserRole.FIND_BY_ROLE, query="SELECT ur FROM UserRole ur WHERE ur.role = :role")
@EntityListeners(value = { AuditEntityListener.class })
public class UserRole extends AuditableAbstractEntity{

	public static final String FIND_BY_ROLE = "UserRole.findByRole";

	public static final String ADMIN = "admin";
	public static final String USER = "user";

	@ManyToMany(fetch=FetchType.LAZY, mappedBy="userRoles")
	private List<User> users;

	@NotNull
	@NotEmpty
	@Column(unique=true)
	private String role;

}
