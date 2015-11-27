package com.awizen.gangehi.service;

import java.lang.reflect.ParameterizedType;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.awizen.gangehi.model.AbstractEntity;

public abstract class AbstractDAO <K, E extends AbstractEntity> {

	protected Class<E> entityClass;

	@Inject
	protected EntityManager em;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[1];
	}

	public E persist(E entity) {
		em.persist(entity);
		return entity;
	}

	public E merge(final E entity) {
		return em.merge(entity);
	}

	public void remove(final E entity) {
		em.remove(em.merge(entity));
	}

	public E findById(final K id) {
		return em.find(entityClass, id);
	}

}
