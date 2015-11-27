package com.awizen.gangehi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ejb.Singleton;
import javax.inject.Inject;

import org.jboss.crypto.CryptoUtil;

import com.awizen.gangehi.model.User;
import com.awizen.gangehi.model.UserRole;

@Singleton
public class UserService {

	@Inject
	private UserDAO userDAO;

	@Inject
	UserRoleDAO userRoleDAO;

	@Inject
	private PropertyService propertyService;

	@Inject
	private EmailService emailService;

	public void register(User user) {
		user.setPasswd(CryptoUtil.createPasswordHash("MD5", CryptoUtil.BASE16_ENCODING, null, null, user.getPasswd()));

		String randomUUID = UUID.randomUUID().toString();
		user.setRandomUUID(randomUUID);

		List<UserRole> userRoles = new ArrayList<UserRole>();
		UserRole userRoleUser = userRoleDAO.findUserRoleByRole(UserRole.USER);
		userRoles.add(userRoleUser);
		user.setUserRoles(userRoles);
		userDAO.persist(user);

	}

	public void update(User user, boolean changePassword) {
		// FIXME check why after password change the old password is still valid?
		if (changePassword) {
			user.setPasswd(CryptoUtil.createPasswordHash("MD5", CryptoUtil.BASE16_ENCODING, null, null, user.getPasswd()));
		}
		userDAO.merge(user);
	}

	public void resetRandomUUID(User user) {

		String randomUUID = UUID.randomUUID().toString();
		user.setRandomUUID(randomUUID);

		userDAO.merge(user);

	}

	public void sendVerificationEmail(User user) {

		Map<String, String> data = new HashMap<String, String>();
		String templateFileName = "mail-verify-email.ftl";
		String emailSubject = "Gangehi System Email Verification";

		Set<String> toSet = new HashSet<String>();
		toSet.add(user.getUserId());

		buildVerificationUrl(user, data, templateFileName);
		emailService.sendMessage(emailSubject, data, toSet, templateFileName );
	}

	public void sendInvitationEmail(User user, String originator) {

		Map<String, String> data = new HashMap<String, String>();
		String templateFileName = "mail-invitation-email.ftl";
		String emailSubject = "Gangehi Approval System Invitation";

		data.put("originator", originator);

		Set<String> toSet = new HashSet<String>();
		toSet.add(user.getUserId());

		buildVerificationUrl(user, data, templateFileName);
		emailService.sendMessage(originator, emailSubject, data, toSet, templateFileName );
	}

	private void buildVerificationUrl(User user, Map<String, String> data, String templateFileName) {

		String applicationUrl = propertyService.getString("mail.application.url");
		String verificationUrl = applicationUrl + "verification.xhtml?verificationId=" + user.getRandomUUID() + "&email="+user.getUserId();


		data.put("verificationUrl", verificationUrl);
	}

	public void sendResetEmail(User user) {

		user = userDAO.findUserByUserId(user.getUserId());

		String templateFileName = "mail-password-reset.ftl";
		String emailSubject = "Gangehi System Password Reset Email";
		Set<String> toSet = new HashSet<String>();
		toSet.add(user.getUserId());

		String applicationUrl = propertyService.getString("mail.application.url");
		String verificationUrl = applicationUrl + "passwordReset.xhtml?verificationId=" + user.getRandomUUID() + "&email="+user.getUserId();

		Map<String, String> data = new HashMap<String, String>();

		data.put("verificationUrl", verificationUrl);

		emailService.sendMessage(emailSubject, data, toSet, templateFileName );
	}

	public boolean validateEmail(String verificationId, String email) {
		User user = userDAO.findUserByUserId(email);

		if (user !=null && user.isEmailVerified()) {
			return true;
		}

		if (user !=null && user.getRandomUUID().equals(verificationId)) {
			user.setEmailVerified(true);
			// invalidate with another randomUUID
			user.setRandomUUID(UUID.randomUUID().toString());
			return true;
		}
		return false;
	}

}
