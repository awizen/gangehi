package com.awizen.gangehi.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;

import com.awizen.gangehi.service.PropertyService;

@SuppressWarnings("serial")
@Named
@ApplicationScoped
public class NavigationController implements Serializable {

	@Inject
	private PropertyService propertyService;

	@Getter
	private boolean simpleNavigation;

	@PostConstruct
	private void init(){
		simpleNavigation = propertyService.getBoolean("simpleNavigation");
	}


}