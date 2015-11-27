package com.awizen.gangehi.test;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.awizen.gangehi.model.SimpleApproval;
import com.awizen.gangehi.service.AbstractDAO;
import com.awizen.gangehi.service.SimpleApprovalDAO;
import com.awizen.gangehi.util.PersistenceResources;

@RunWith(Arquillian.class)
public class SimpleApprovalTest {

	@Inject
	SimpleApprovalDAO crud;

	@Inject
	Logger log;

	@Deployment
	public static Archive<?> createTestArchive() {
		return ShrinkWrap.create(WebArchive.class, "test.war")
				.addClasses(SimpleApproval.class, AbstractDAO.class, PersistenceResources.class)
				.addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				// Deploy our test datasource
				.addAsWebInfResource("test-ds.xml", "test-ds.xml");
	}

	@Test
	public void testSimpleApproval() throws Exception {

		SimpleApproval approval = new SimpleApproval();
		approval.setSubject("test");
		approval.setDueDate(new Date());
		approval.setApprovalText("test lkjlj");

		crud.persist(approval);

		assertNotNull(approval.getId());

		log.info(approval.toString());
	}

}
