package com.awizen.gangehi.service;

import java.util.List;
import java.util.Set;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;

import com.awizen.gangehi.dtos.ClosedWorkflowSummaryDto;
import com.awizen.gangehi.dtos.WorkflowSummaryDto;
import com.awizen.gangehi.model.SimpleApproval;
import com.awizen.gangehi.model.User;




@Singleton
public class DTOService{

	@Inject
	protected EntityManager em;

	public User findUserByUserId(String userId) {
		TypedQuery<User> q = em.createNamedQuery(User.FIND_BY_USER_ID, User.class);
		q.setParameter ("userId", userId);
		return q.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<WorkflowSummaryDto> listOpenWorkflows(Set<Long> processInstanceIds) {

		String ids = StringUtils.join(processInstanceIds, ',');

		// TODO make it named query
		// remove @SuppressWarnings("unchecked")

		String sqlString = "SELECT"
				+ "    t.id taskId,"
				+ "    t.processInstanceId,"
				+ "    sap.subject,"
				+ "    sap.createdBy initiator,"
				+ "    t.actualOwner_id,"
				+ "    sap.createdOn,"
				+ "    sap.dueDate "
				+ "FROM"
				+ "    jbpm.Task t,"
				+ "    gangehi.SimpleApproval sap "
				+ "WHERE"
				+ "    t.processInstanceId IN ("
				+ ids
				+ ")"
				+ "    AND t.processInstanceId = sap.processInstanceId"
				+ "    AND NOT t.status = 'Completed';";

		Query query = em.createNativeQuery(sqlString, SimpleApproval.WORKFLOW_SUMMARY_DTO_MAPPING);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ClosedWorkflowSummaryDto> listClosedWorkflows(Set<Long> processInstanceIds) {

		String ids = StringUtils.join(processInstanceIds, ',');

		// TODO make it named query
		// remove @SuppressWarnings("unchecked")

		String sqlString = "SELECT"
				+ "    sap.processInstanceId,"
				+ "    sap.subject,"
				+ "    sap.createdBy initiator,"
				+ "    sap.createdOn,"
				+ "    sap.dueDate,"
				+ "    pi.end_date endDate "
				+ "FROM"
				+ "    gangehi.SimpleApproval sap,"
				+ "    jbpm.ProcessInstanceLog pi "
				+ "WHERE"
				+ "    sap.processInstanceId IN ("
				+ ids
				+ ")"
				+ "    AND sap.processInstanceId = pi.processInstanceId;";

		Query query = em.createNativeQuery(sqlString, SimpleApproval.CLOSED_WORKFLOW_SUMMARY_DTO_MAPPING);

		return query.getResultList();
	}

}
