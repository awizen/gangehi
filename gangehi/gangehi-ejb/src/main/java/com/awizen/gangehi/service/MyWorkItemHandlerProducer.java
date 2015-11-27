package com.awizen.gangehi.service;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.manager.WorkItemHandlerProducer;

public class MyWorkItemHandlerProducer implements WorkItemHandlerProducer {

	@Inject
	private MySendTaskHandler mySendTaskHandler;

	@Override
	public Map<String, WorkItemHandler> getWorkItemHandlers(String identifier, Map<String, Object> params) {
		Map<String, WorkItemHandler> handlerMap = new HashMap<String, WorkItemHandler>();
		handlerMap.put("Send Task", mySendTaskHandler);
		return handlerMap;
	}

}
