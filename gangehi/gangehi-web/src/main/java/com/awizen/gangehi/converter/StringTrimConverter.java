package com.awizen.gangehi.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

@SuppressWarnings("serial")
@FacesConverter(forClass = String.class)
public class StringTrimConverter implements Serializable, javax.faces.convert.Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent cmp, String value) {

		if (value != null && cmp instanceof HtmlInputText) {
			// trim the entered value in a HtmlInputText before doing
			// validation/updating the model
			return value.trim();
		}

		return value;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent cmp, Object value) {

		if (value != null) {
			// return the value as is for presentation
			return value.toString();
		}
		return null;
	}
}
