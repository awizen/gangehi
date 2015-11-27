package com.awizen.gangehi.util;

import org.apache.commons.lang.StringUtils;

import com.awizen.gangehi.model.User;

public class UserDecorator {

	public static String getNameForPageHeader(User user) {
		StringBuilder sb = new StringBuilder();

		buildFirstLastName(user, sb);

		boolean namePresent = StringUtils.isNotBlank(sb.toString());

		if (namePresent) {
			sb.append(" (");
		}
		sb.append(user.getUserId());
		if (namePresent) {
			sb.append(")");
		}
		return sb.toString();
	}

	public static String getNameForEmail(User user) {
		StringBuilder sb = new StringBuilder();

		buildFirstLastName(user, sb);

		return StringUtils.isNotBlank(sb.toString()) ? sb.toString() : user.getUserId();
	}

	private static void buildFirstLastName(User user, StringBuilder sb) {
		if (StringUtils.isNotBlank(user.getFirstName())) {
			sb.append(user.getFirstName()).append(" ");
		}
		if (StringUtils.isNotBlank(user.getLastName())) {
			sb.append(user.getLastName());
		}
	}

}
