package com.awizen.gangehi.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import lombok.Data;

import org.kie.internal.identity.IdentityProvider;

import com.awizen.gangehi.model.User;
import com.awizen.gangehi.service.UserDAO;
import com.awizen.gangehi.util.UserDecorator;

@SuppressWarnings("serial")
@Named
@SessionScoped
@Data
public class UserBean implements Serializable {

	@Inject
	private transient IdentityProvider identityProvider;

	@Inject
	private UserDAO userDAO;

	private User user;

	public String logout() {
		((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).invalidate();
		return "/index";
	}

	@PostConstruct
	private void init(){
		user = userDAO.findUserByUserId(identityProvider.getName());
	}

	public String getPrettyName(){

		return UserDecorator.getNameForPageHeader(user);
	}

}