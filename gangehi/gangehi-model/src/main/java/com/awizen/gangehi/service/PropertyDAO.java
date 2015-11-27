package com.awizen.gangehi.service;

import javax.ejb.Singleton;
import javax.persistence.TypedQuery;

import com.awizen.gangehi.model.Property;
import com.awizen.gangehi.model.User;

@Singleton
public class PropertyDAO extends AbstractDAO<Long, User> {

	public Property findPropertyByKey(String key) {
		TypedQuery<Property> q = em.createNamedQuery(Property.FIND_BY_KEY, Property.class);
		q.setParameter ("key", key);
		return q.getSingleResult();
	}

}
