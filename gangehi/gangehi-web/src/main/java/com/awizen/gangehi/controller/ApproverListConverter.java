package com.awizen.gangehi.controller;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.orderlist.OrderList;

import com.awizen.gangehi.model.ApprovalStep;

@FacesConverter("approverListConverter")
public class ApproverListConverter implements Converter{


	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		if (component instanceof OrderList) {
			@SuppressWarnings("unchecked")
			List<ApprovalStep> stepList = (List<ApprovalStep>) ((OrderList) component).getValue();

			for (ApprovalStep step : stepList) {
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

		if (value instanceof ApprovalStep) {
			ApprovalStep as = (ApprovalStep) value;
			return as.getId().toString();

		}

		return value.toString();
	}

}
