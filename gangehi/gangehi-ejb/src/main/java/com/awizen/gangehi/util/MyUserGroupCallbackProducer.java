package com.awizen.gangehi.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.jbpm.services.cdi.Selectable;
import org.jbpm.services.cdi.producer.UserGroupInfoProducer;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.task.api.UserInfo;

@ApplicationScoped
@Alternative
@Selectable
public class MyUserGroupCallbackProducer implements UserGroupInfoProducer {

	private UserGroupCallback ugc = new MyUserGroupCallback();

	@Override
	public UserGroupCallback produceCallback() {
		return ugc;
	}

	@Override
	public UserInfo produceUserInfo() {
		return null;
	}

}
