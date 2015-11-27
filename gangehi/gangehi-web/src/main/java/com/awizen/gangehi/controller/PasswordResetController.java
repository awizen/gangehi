package com.awizen.gangehi.controller;

import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.jboss.crypto.CryptoUtil;

import com.awizen.gangehi.model.User;
import com.awizen.gangehi.service.UserDAO;
import com.awizen.gangehi.service.UserService;

@SuppressWarnings("serial")
@Named
@ViewScoped
public class PasswordResetController  implements Serializable{

	@Inject
	private Logger log;

	@Inject
	private UserService userService;

	@Inject
	private UserDAO userDAO;

	@Setter
	@Getter
	private User user = new User();

	public String requestReset(){

		String userId = user.getUserId();
		user = userDAO.findUserByUserId(userId);

		if (user == null) {
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "The userId: '" + userId + "' does not exist!", "User unknown!");
			FacesContext.getCurrentInstance().addMessage(null, m);
			return "requestPasswordReset";
		}

		userService.resetRandomUUID(user);
		userService.sendResetEmail(user);

		return "resetEmailSent";
	}

	public String reset(){

		String userId = user.getUserId();
		User userToChange = userDAO.findUserByUserId(userId);

		if (user.getRandomUUID().equals(userToChange.getRandomUUID())) {

			userToChange.setPasswd(CryptoUtil.createPasswordHash("MD5", CryptoUtil.BASE16_ENCODING, null, null, user.getPasswd()));
			// invalidate with another randomUUID
			userToChange.setRandomUUID(UUID.randomUUID().toString());
			userDAO.merge(userToChange);

			return "pwdChanged";
		} else {
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
			}
			log.warning("Password reset failed for: randomUUID=" + user.getRandomUUID() +",  email=" + userId + "; from remote IP=" + ipAddress);

			return "pwdChangeFailure";
		}


	}

}
