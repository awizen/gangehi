package com.awizen.gangehi.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.awizen.gangehi.listener.AuditEntityListener;

@Data
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("serial")
@Entity
@EntityListeners(value = { AuditEntityListener.class })
@NamedQueries({
	@NamedQuery(name = ApprovalHistory.FIND_BY_TAKS_ID, query = "SELECT a FROM ApprovalHistory a WHERE a.taskId = :taskId"),
	@NamedQuery(name = ApprovalHistory.FIND_BY_PROCESS_INSTANCE_ID, query = "SELECT a FROM ApprovalHistory a WHERE a.processInstanceId = :processInstanceId ORDER BY a.changedOn DESC") })
@Table(indexes = @Index(columnList = "processInstanceId"))
public class ApprovalHistory extends AuditableAbstractEntity {

	public static final String FIND_BY_PROCESS_INSTANCE_ID = "ApprovalHistory.findByProcessInstanceId";
	public static final String FIND_BY_TAKS_ID = "ApprovalHistory.findByTaksId";

	// @NotNull
	private Long taskId;

	// @NotNull
	private Long processInstanceId;

	// @NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date taskCreatedOn;

	// @NotNull
	private String action;

	private String comment;

}
