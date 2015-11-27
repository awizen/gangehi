package com.awizen.gangehi.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import javax.faces.application.ProjectStage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;

@ManagedBean
@RequestScoped
public class ErrorHandler {

	private static final String JAVAX_SERVLET_ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
	private static final String JAVAX_SERVLET_ERROR_EXCEPTION = "javax.servlet.error.exception";
	private static final String JAVAX_SERVLET_ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
	private static final String JAVAX_SERVLET_ERROR_MESSAGE = "javax.servlet.error.message";
	private static final String JAVAX_SERVLET_ERROR_STATUS_CODE = "javax.servlet.error.status_code";

	private String getStringValue(String javaxServletErrorParam) {
		return String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(javaxServletErrorParam));
	}

	public boolean isDevelopment() {
		return FacesContext.getCurrentInstance().isProjectStage(ProjectStage.Development);
	}

	public String getStatusCode() {
		return getStringValue(JAVAX_SERVLET_ERROR_STATUS_CODE);
	}

	public String getMessage() {
		return getStringValue(JAVAX_SERVLET_ERROR_MESSAGE);
	}

	public String getExceptionType() {
		return getStringValue(JAVAX_SERVLET_ERROR_EXCEPTION_TYPE);
	}

	public String getException() {
		return getStringValue(JAVAX_SERVLET_ERROR_EXCEPTION);
	}

	public String getRequestURI() {
		return getStringValue(JAVAX_SERVLET_ERROR_REQUEST_URI);
	}

	public String getStackTrace() {
		Throwable ex = (Throwable) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(JAVAX_SERVLET_ERROR_EXCEPTION);
		StringWriter sw = new StringWriter();
		fillStackTrace(ex, new PrintWriter(sw));
		return sw.toString();
	}

	private static void fillStackTrace(Throwable t, PrintWriter w) {
		if (t == null) {
			return;
		}
		t.printStackTrace(w);
		if (t instanceof ServletException) {
			Throwable cause = ((ServletException) t).getRootCause();
			if (cause != null) {
				w.println("Root cause:");
				fillStackTrace(cause, w);
			}
		} else if (t instanceof SQLException) {
			Throwable cause = ((SQLException) t).getNextException();
			if (cause != null) {
				w.println("Next exception:");
				fillStackTrace(cause, w);
			}
		} else {
			Throwable cause = t.getCause();
			if (cause != null) {
				w.println("Cause:");
				fillStackTrace(cause, w);
			}
		}
	}

}