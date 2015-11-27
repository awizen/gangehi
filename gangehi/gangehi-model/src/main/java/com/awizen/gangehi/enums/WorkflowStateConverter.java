package com.awizen.gangehi.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class WorkflowStateConverter implements AttributeConverter<WorkflowState, String>{

	private static final String DB_VALUE_NEW = "N";
	private static final String DB_VALUE_IN_PROGRESS = "IP";
	private static final String DB_VALUE_DELETED = "D";
	private static final String DB_VALUE_REJECTED = "R";
	private static final String DB_VALUE_AUTHORIZED = "AT";
	private static final String DB_VALUE_ABORTED = "AB";

	@Override
	public String convertToDatabaseColumn(WorkflowState attribute) {
		switch (attribute) {
		case NEW:
			return DB_VALUE_NEW;

		case IN_PROGRESS:
			return DB_VALUE_IN_PROGRESS;

		case DELETED:
			return DB_VALUE_DELETED;

		case REJECTED:
			return DB_VALUE_REJECTED;

		case AUTHORIZED:
			return DB_VALUE_AUTHORIZED;

		case ABORTED:
			return DB_VALUE_ABORTED;

		default:
			throw new IllegalArgumentException("Unknown WorkflowState: " + attribute);
		}
	}

	@Override
	public WorkflowState convertToEntityAttribute(String dbData) {
		switch (dbData) {
		case DB_VALUE_NEW:
			return WorkflowState.NEW;

		case DB_VALUE_IN_PROGRESS:
			return WorkflowState.IN_PROGRESS;

		case DB_VALUE_DELETED:
			return WorkflowState.DELETED;

		case DB_VALUE_REJECTED:
			return WorkflowState.REJECTED;

		case DB_VALUE_AUTHORIZED:
			return WorkflowState.AUTHORIZED;

		case DB_VALUE_ABORTED:
			return WorkflowState.ABORTED;

		default:
			throw new IllegalArgumentException("Unknown dbData for WorkflowState: " + dbData);
		}
	}

}
