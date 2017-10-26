package com.awizen.gangehi.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.awizen.gangehi.dtos.ClosedWorkflowSummaryDto;
import com.awizen.gangehi.dtos.WorkflowSummaryDto;
import com.awizen.gangehi.enums.WorkflowState;
import com.awizen.gangehi.enums.WorkflowStateConverter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SqlResultSetMappings( {
	@SqlResultSetMapping(name= SimpleApproval.WORKFLOW_SUMMARY_DTO_MAPPING,
			classes = {
					@ConstructorResult(targetClass = WorkflowSummaryDto.class,
							columns = {
									@ColumnResult(name="taskId", type = Long.class),
									@ColumnResult(name="processInstanceId", type = Long.class),
									@ColumnResult(name="subject", type = String.class),
									@ColumnResult(name="initiator", type = String.class),
									@ColumnResult(name="actualOwner_id", type = String.class),
									@ColumnResult(name="createdOn", type = Date.class),
									@ColumnResult(name="dueDate", type = Date.class),
					}
							)}
			),

	@SqlResultSetMapping(name= SimpleApproval.CLOSED_WORKFLOW_SUMMARY_DTO_MAPPING,
	classes = {
			@ConstructorResult(targetClass = ClosedWorkflowSummaryDto.class,
					columns = {
							@ColumnResult(name="processInstanceId", type = Long.class),
							@ColumnResult(name="subject", type = String.class),
							@ColumnResult(name="initiator", type = String.class),
							@ColumnResult(name="createdOn", type = Date.class),
							@ColumnResult(name="dueDate", type = Date.class),
							@ColumnResult(name="endDate", type = Date.class)
			}
					)}
			)
})

@Data
@EqualsAndHashCode(callSuper=true)
@SuppressWarnings("serial")
@Entity
@NamedQueries({
	@NamedQuery(name = SimpleApproval.FIND_BY_PROCESS_INSTANCE_ID, query="SELECT a FROM SimpleApproval a WHERE a.processInstanceId = :processInstanceId"),
	@NamedQuery(name = SimpleApproval.FIND_OPEN_APPROVALS, query = "SELECT a FROM SimpleApproval a WHERE a.workflowState in ('IP', 'R')") })

@Table(indexes = @Index(columnList = "processInstanceId"))
public class SimpleApproval extends AuditableAbstractEntity{

	public static final String FIND_BY_PROCESS_INSTANCE_ID = "SimpleApproval.findByProcessInstanceId";
	public static final String FIND_OPEN_APPROVALS = "SimpleApproval.findOpenApprovals";
	public static final String WORKFLOW_SUMMARY_DTO_MAPPING = "WorkflowSummaryDtoMapping";
	public static final String CLOSED_WORKFLOW_SUMMARY_DTO_MAPPING = "ClosedWorkflowSummaryDtoMapping";

	@NotNull
	private Date dueDate;

	@NotNull
	@NotEmpty
	private String subject;

	@NotNull
	@Column(unique=true)
	private Long processInstanceId;

	@NotNull
	@NotEmpty
	private String approvalText;

	@OneToMany(fetch=FetchType.EAGER, orphanRemoval=true, cascade=CascadeType.ALL)
	@OrderBy("rank ASC")
	private Set<ApprovalStep> approvalSteps;

	@OneToMany(fetch=FetchType.EAGER, orphanRemoval=true, cascade=CascadeType.ALL)
	@OrderBy("fileName ASC")
	private Set<FileEntity> files;

	@NotNull
	@Convert(converter = WorkflowStateConverter.class)
	private WorkflowState workflowState;

}
