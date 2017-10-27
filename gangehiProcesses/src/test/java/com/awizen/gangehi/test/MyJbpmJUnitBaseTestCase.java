package com.awizen.gangehi.test;

import org.jbpm.test.JbpmJUnitBaseTestCase;

import com.awizen.gangehi.util.MyUserGroupCallback;

public class MyJbpmJUnitBaseTestCase extends JbpmJUnitBaseTestCase {

	public MyJbpmJUnitBaseTestCase(boolean arg0, boolean arg1) {
		super(arg0, arg1);
		userGroupCallback = new MyUserGroupCallback();
	}
}
