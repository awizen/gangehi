package com.awizen.gangehi.service;

import javax.ejb.Singleton;
import javax.persistence.TypedQuery;

import com.awizen.gangehi.model.UserRole;

@Singleton
public class UserRoleDAO extends AbstractDAO<Long, UserRole> {

	public UserRole findUserRoleByRole(String role) {
		TypedQuery<UserRole> q = em.createNamedQuery(UserRole.FIND_BY_ROLE, UserRole.class);
		q.setParameter ("role", role);
		return q.getSingleResult();
	}

}
