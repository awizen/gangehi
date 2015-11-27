package com.awizen.gangehi.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.hibernate.validator.constraints.NotEmpty;

import com.awizen.gangehi.listener.AuditEntityListener;

@Data
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("serial")
@Entity
@EntityListeners(value = { AuditEntityListener.class })
@NamedQueries({ @NamedQuery(name = User.FIND_BY_USER_ID, query = "SELECT u FROM User u WHERE u.userId = :userId"),
	@NamedQuery(name = User.FIND_BY_RANDOM_UUID, query = "SELECT u FROM User u WHERE u.randomUUID = :randomUUID") })
public class User extends AuditableAbstractEntity {

	public static final String FIND_BY_USER_ID = "User.findByUserId";
	public static final String FIND_BY_RANDOM_UUID = "User.findByRandomUUID";

	@NotNull
	@NotEmpty
	@Column(unique = true)
	private String userId;

	@NotNull
	@NotEmpty
	private String passwd;

	private String firstName;

	private String lastName;

	private String telephoneNr;

	private boolean emailVerified;

	@Column(unique = true)
	private String randomUUID;

	@ManyToMany(fetch = FetchType.LAZY)
	private List<UserRole> userRoles;

}
