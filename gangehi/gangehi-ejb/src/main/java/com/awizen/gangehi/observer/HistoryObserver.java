package com.awizen.gangehi.observer;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.awizen.gangehi.model.ApprovalHistory;
import com.awizen.gangehi.service.ApprovalHistoryDAO;

@Singleton
public class HistoryObserver {

	@Inject
	private ApprovalHistoryDAO approvalHistoryDAO;

	public void listenToHisory(@Observes ApprovalHistory history) {

		approvalHistoryDAO.persist(history);
	}

}
