package com.awizen.gangehi.service;

import javax.ejb.Singleton;
import javax.persistence.TypedQuery;

import com.awizen.gangehi.model.ApprovalStep;

@Singleton
public class ApprovalStepDAO extends AbstractDAO<Long, ApprovalStep> {

	public Boolean isUserInvolved(long processInstanceId, String userId) {
		TypedQuery<Long> q = em.createNamedQuery(ApprovalStep.IS_USER_INVOLVED, Long.class);
		q.setParameter ("processInstanceId", processInstanceId);
		q.setParameter ("userId", userId);
		Long involvedUserCount = q.getSingleResult();
		return involvedUserCount > 0;
	}

}