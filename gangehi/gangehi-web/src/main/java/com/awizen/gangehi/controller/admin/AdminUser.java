package com.awizen.gangehi.controller.admin;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

import org.kie.internal.identity.IdentityProvider;

import com.awizen.gangehi.controller.UserBean;
import com.awizen.gangehi.model.User;
import com.awizen.gangehi.service.UserDAO;
import com.awizen.gangehi.service.UserService;

@SuppressWarnings("serial")
@Named
@ViewScoped
@Getter
public class AdminUser  implements Serializable{

	@Inject
	private UserService userService;

	@Inject
	private UserDAO userDAO;

	@Inject
	private transient IdentityProvider identityProvider;

	@Inject
	private UserBean userBean;

	private User user;

	@Setter
	private boolean changePassword;

	public String update(){

		userService.update(user, changePassword);
		userBean.setUser(getUser());

		return "/secure/schedule";
	}

	@PostConstruct
	private void init(){
		user = userDAO.findUserByUserId(identityProvider.getName());
	}


}
