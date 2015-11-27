package com.awizen.gangehi.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ApprovalStepStateConverter implements AttributeConverter<ApprovalStepState, String>{

	private static final String DB_VALUE_OPEN = "O";
	private static final String DB_VALUE_SCHEDULED = "S";
	private static final String DB_VALUE_RETURNED = "RT";
	private static final String DB_VALUE_REJECTED = "RJ";
	private static final String DB_VALUE_APPROVED = "AP";
	private static final String DB_VALUE_AUTHORIZED = "AT";

	@Override
	public String convertToDatabaseColumn(ApprovalStepState attribute) {
		switch (attribute) {
		case OPEN:
			return DB_VALUE_OPEN;

		case SCHEDULED:
			return DB_VALUE_SCHEDULED;

		case RETURNED:
			return DB_VALUE_RETURNED;

		case REJECTED:
			return DB_VALUE_REJECTED;

		case APPROVED:
			return DB_VALUE_APPROVED;

		case AUTHORIZED:
			return DB_VALUE_AUTHORIZED;

		default:
			throw new IllegalArgumentException("Unknown ApprovalStepState: " + attribute);
		}
	}

	@Override
	public ApprovalStepState convertToEntityAttribute(String dbData) {
		switch (dbData) {
		case DB_VALUE_OPEN:
			return ApprovalStepState.OPEN;

		case DB_VALUE_SCHEDULED:
			return ApprovalStepState.SCHEDULED;

		case DB_VALUE_RETURNED:
			return ApprovalStepState.RETURNED;

		case DB_VALUE_REJECTED:
			return ApprovalStepState.REJECTED;

		case DB_VALUE_APPROVED:
			return ApprovalStepState.APPROVED;

		case DB_VALUE_AUTHORIZED:
			return ApprovalStepState.AUTHORIZED;

		default:
			throw new IllegalArgumentException("Unknown dbData for ApprovalStepState: " + dbData);
		}
	}

}
