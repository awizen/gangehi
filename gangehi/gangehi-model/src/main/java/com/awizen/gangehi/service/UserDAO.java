package com.awizen.gangehi.service;

import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.awizen.gangehi.model.User;

@Singleton
public class UserDAO extends AbstractDAO<Long, User> {

	@Inject
	private Logger log;

	public User findUserByUserId(String userId) {
		try {
			TypedQuery<User> q = em.createNamedQuery(User.FIND_BY_USER_ID, User.class);
			q.setParameter ("userId", userId);
			return q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public User findUserByRandomUUID(String randomUUID) {
		try {
			TypedQuery<User> q = em.createNamedQuery(User.FIND_BY_RANDOM_UUID, User.class);
			q.setParameter ("randomUUID", randomUUID);
			return q.getSingleResult();
		} catch (Exception e) {
			log.warning(e.getMessage() + "; randomUUID: " + randomUUID);
			return null;
		}

	}

}
