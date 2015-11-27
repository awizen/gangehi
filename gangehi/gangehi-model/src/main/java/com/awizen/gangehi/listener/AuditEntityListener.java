package com.awizen.gangehi.listener;

import java.util.Date;

import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.awizen.gangehi.model.AuditableAbstractEntity;

public class AuditEntityListener {

	@PrePersist
	public void prePersist(AuditableAbstractEntity e) {
		String userName = getPrincipalName();

		Date now = new Date();

		e.setCreatedBy(userName);
		e.setCreatedOn(now);

		e.setChangedBy(userName);
		e.setChangedOn(now);
	}

	private String getPrincipalName() {
		try {
			SessionContext sctxLookup = (SessionContext) new InitialContext().lookup("java:comp/EJBContext");
			return sctxLookup.getCallerPrincipal().getName();
		} catch (NamingException ex) {
			throw new IllegalStateException(ex);
		}
	}

	@PreUpdate
	public void preUpdate(AuditableAbstractEntity e) {
		e.setChangedBy(getPrincipalName());
		e.setChangedOn(new Date());
	}
}
