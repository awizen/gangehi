package com.awizen.gangehi.controller;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import com.awizen.gangehi.service.UserService;

@SuppressWarnings("serial")
@Named
@ViewScoped
public class EmailVerificationController implements Serializable{

	@Inject
	private Logger log;

	@Inject
	private UserService userService;

	@Setter
	@Getter
	private String verificationId;

	@Setter
	@Getter
	private String email;

	@Getter
	private boolean valide;

	public void verify() {

		log.fine("Veryfing UUID: " + verificationId);

		valide = userService.validateEmail(verificationId, email);

		if (! valide) {
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
			}
			log.warning("EmailVerification failed for: verificationId=" + verificationId +",  email=" + email + "; from remote IP=" + ipAddress);
		}
	}


}
