package com.awizen.gangehi.service;

import java.util.List;

import javax.ejb.Singleton;
import javax.persistence.TypedQuery;

import com.awizen.gangehi.model.ApprovalHistory;

@Singleton
public class ApprovalHistoryDAO extends AbstractDAO<Long, ApprovalHistory> {

	public List<ApprovalHistory> findApprovalHistory(Long processInstanceId) {

		TypedQuery<ApprovalHistory> q = em.createNamedQuery(ApprovalHistory.FIND_BY_PROCESS_INSTANCE_ID, ApprovalHistory.class);
		q.setParameter ("processInstanceId", processInstanceId);
		return q.getResultList();
	}

}
