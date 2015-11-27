package com.awizen.gangehi.util;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.cdi.Kjar;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.internal.identity.IdentityProvider;

@ApplicationScoped
public class Resources {

	@PersistenceUnit(unitName = "org.jbpm.domain")
	private EntityManagerFactory emf;

	@Produces
	public EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}

	@Inject
	private Principal principal;

	@Produces
	@Named("Logs")
	public TaskLifeCycleEventListener produceTaskAuditListener() {
		return new JPATaskLifeCycleEventListener(true);
	}

	@Inject
	@Kjar
	private DeploymentService deploymentService;

	@Produces
	public DeploymentService getDeploymentService() {
		return deploymentService;
	}

	@Produces
	public IdentityProvider produceIdentityProvider() {

		return new IdentityProvider() {

			@Override
			public String getName() {
				return principal.getName().trim().toLowerCase(Locale.ROOT);
			}

			@Override
			public List<String> getRoles() {

				ArrayList<String> arrayList = new ArrayList<String>();
				arrayList.add("myGroup");
				return arrayList;
			}

			@Override
			public boolean hasRole(String arg0) {
				return false;
			}
		};

	}
}
