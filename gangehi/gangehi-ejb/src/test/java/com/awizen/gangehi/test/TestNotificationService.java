package com.awizen.gangehi.test;

import java.util.logging.Logger;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;

import org.junit.Test;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

import com.awizen.gangehi.service.MySendTaskHandler;

public class TestNotificationService {

	@Mocked
	WorkItem workItem;

	@Mocked
	WorkItemManager manager;

	@Injectable
	Logger log;

	@Test
	public void test() {

		// Record phase:
		new Expectations() {{
			workItem.getParameter(MySendTaskHandler.NOTIFICATION_TYPE); result = MySendTaskHandler.AUTHORISED_NOTIFICATION;
			manager.completeWorkItem(anyLong, null);
		}

		};

		// Replay phase:
		MySendTaskHandler handler = new MySendTaskHandler();
		handler.executeWorkItem(workItem, manager);

		// Verify phase:


	}

}
