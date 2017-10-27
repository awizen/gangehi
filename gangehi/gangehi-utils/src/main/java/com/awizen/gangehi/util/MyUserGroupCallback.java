package com.awizen.gangehi.util;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.kie.api.task.UserGroupCallback;

@ApplicationScoped
public class MyUserGroupCallback implements UserGroupCallback {
	
	@Override
	public List<String> getGroupsForUser(String userId) {
		List<String> groups = new ArrayList<String>();
		groups.add("myGroup");
		return groups;
	}

	@Override
	public boolean existsUser(String userId) {
		return true;
	}

	@Override
	public boolean existsGroup(String groupId) {
		return true;
	}
}
