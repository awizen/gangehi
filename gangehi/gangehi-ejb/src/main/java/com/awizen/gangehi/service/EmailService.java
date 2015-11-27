package com.awizen.gangehi.service;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.awizen.gangehi.model.User;
import com.awizen.gangehi.util.UserDecorator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

@Singleton
public class EmailService {

	@Inject
	private PropertyService propertyService;

	@Inject
	private UserDAO userDao;

	@Inject
	private Logger log;

	private Configuration cfg;

	public void sendMessage(String emailSubject, Map<String, String> data, Set<String> toSet, String templateFileName) {
		String replyTo = null;
		sendMessage(replyTo, emailSubject, data, toSet, templateFileName);
	}

	public void sendMessage(String replyTo, String emailSubject, Map<String, String> data, Set<String> toSet, String templateFileName) {

		enrichData(data);
		String noreplyAdress = propertyService.getString("mail.address.noreply");

		try {
			Template template = cfg.getTemplate(templateFileName);

			for (String to : toSet) {
				String showReplyWarning = "false";
				log.fine("Preparing email to: " + to);
				Writer out = new StringWriter();
				User toUser = userDao.findUserByUserId(to);
				String decoratedName = toUser != null ? UserDecorator.getNameForEmail(toUser) : to;
				data.put("toName", decoratedName);

				Message message = new MimeMessage(getMailSession());
				message.setFrom(new InternetAddress(noreplyAdress));

				if (replyTo != null && !replyTo.equalsIgnoreCase(to)) {
					Address[] addresses = new Address[] { new InternetAddress(replyTo) };
					message.setReplyTo(addresses);
					showReplyWarning = "true";
				}
				data.put("showReplyWarning", showReplyWarning);

				to = checkForTestRecipient(to);
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
				message.setSubject(emailSubject);

				template.process(data, out);
				message.setContent(out.toString(), "text/html");
				Transport.send(message);

				log.fine("Email sent to: " + to);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@PostConstruct
	private void initialize() {
		cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		cfg.setClassForTemplateLoading(this.getClass(), "/");
		cfg.setDefaultEncoding("UTF-8");
		// During web page *development*
		// TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	}

	private String checkForTestRecipient(String to) {

		if (propertyService.getBoolean("mail.send.toRealRecipient")) {
			return to;
		}

		String adminAdress = propertyService.getString("mail.address.test");
		log.warning("Email sent to admin (" + adminAdress + ") instead of regular recepient(s): " + to);
		return adminAdress;
	}

	private void enrichData(Map<String, String> data) {
		try {
			byte[] imgBytes = IOUtils.toByteArray(getClass().getResourceAsStream("/img/gangehi-logo.svg"));
			byte[] imgBytesAsBase64 = Base64.encodeBase64(imgBytes);
			String imgDataAsBase64 = new String(imgBytesAsBase64);
			String imgAsBase64 = "data:image/svg+xml;base64," + imgDataAsBase64;

			data.put("imgAsBase64", imgAsBase64);

		} catch (Exception e) {
			log.severe(e.toString());
			data.put("imgAsBase64", "");
		}
	}

	private Session getMailSession() {
		Properties properties = new Properties();

		properties.put("mail.smtp.auth", propertyService.getString("mail.smtp.auth"));
		properties.put("mail.smtp.starttls.enable", propertyService.getString("mail.smtp.starttls.enable"));
		properties.put("mail.smtp.host", propertyService.getString("mail.smtp.host"));
		properties.put("mail.smtp.port", propertyService.getString("mail.smtp.port"));

		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(propertyService.getString("mail.userName"), propertyService.getString("mail.password"));
			}
		});
		return session;
	}

}
