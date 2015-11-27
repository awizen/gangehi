package com.awizen.gangehi.controller;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.orderlist.OrderList;

import com.awizen.gangehi.model.FileEntity;

@FacesConverter("fileListConverter")
public class FileListConverter implements Converter{


	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		if (component instanceof OrderList) {
			@SuppressWarnings("unchecked")
			List<FileEntity> stepList = (List<FileEntity>) ((OrderList) component).getValue();

			for (FileEntity step : stepList) {
				String convertedListValue = getAsString(context, component, step);
				if (value == null ? convertedListValue == null : value.equals(convertedListValue)) {
					return step;
				}
			}
		}

		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {

		if (value == null) {
			return null;
		}

		if (value instanceof FileEntity) {
			FileEntity as = (FileEntity) value;
			return as.getId().toString();

		}

		return value.toString();
	}

}
