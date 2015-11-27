package com.awizen.gangehi.controller;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;

import com.awizen.gangehi.model.User;
import com.awizen.gangehi.service.UserService;

@SuppressWarnings("serial")
@Named
@ViewScoped
@Getter
public class Register  implements Serializable{

	@Inject
	private UserService userService;

	private User user = new User();

	public String register(){

		userService.register(user);
		userService.sendVerificationEmail(user);

		return "verificationEmailSent";
	}

}
