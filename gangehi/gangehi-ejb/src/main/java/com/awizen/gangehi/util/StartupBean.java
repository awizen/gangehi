package com.awizen.gangehi.util;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.cdi.Kjar;

@Singleton
@Startup
public class StartupBean {

	public static final String PROCESSES_DEPLOYMENT_ID = "com.awizen:gangehi-processes:1.0";

	@Inject
	@Kjar
	DeploymentService deploymentService;

	@PostConstruct
	public void init() {
		String[] gav = PROCESSES_DEPLOYMENT_ID.split(":");
		DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(gav[0], gav[1], gav[2]);
		deploymentService.deploy(deploymentUnit);

	}

}
