package com.awizen.gangehi.service;

import java.util.List;

import javax.ejb.Singleton;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.awizen.gangehi.model.SimpleApproval;

@Singleton
public class SimpleApprovalDAO extends AbstractDAO<Long, SimpleApproval> {

	public SimpleApproval findSimpleApproval(long processInstanceId) {
		try {
			TypedQuery<SimpleApproval> q = em.createNamedQuery(SimpleApproval.FIND_BY_PROCESS_INSTANCE_ID, SimpleApproval.class);
			q.setParameter ("processInstanceId", processInstanceId);
			return q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<SimpleApproval> findOpenApprovalsProcessInstanceId() {
		try {
			TypedQuery<SimpleApproval> q = em.createNamedQuery(SimpleApproval.FIND_OPEN_APPROVALS, SimpleApproval.class);
			return q.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

}
