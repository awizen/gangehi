package com.awizen.gangehi.service;

import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
public class PropertyService {

	@Inject
	private PropertyDAO propertyDAO;

	public String getString(String key) {
		return propertyDAO.findPropertyByKey(key).getValue();
	}

	public Boolean getBoolean(String key) {
		return Boolean.valueOf(propertyDAO.findPropertyByKey(key).getValue());
	}

	public long getLong(String key, long defaultValue) {
		try {
			return Long.parseLong(propertyDAO.findPropertyByKey(key).getValue());
		} catch (Exception e) {
			return defaultValue;
		}
	}

}
