package com.awizen.gangehi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.jbpm.services.api.RuntimeDataService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;

import com.awizen.gangehi.model.SimpleApproval;

@Startup
@Singleton
public class ReminderService {

	@Resource
	TimerService timerService;

	@Inject
	private PropertyService propertyService;

	@Inject
	private SimpleApprovalDAO simpleApprovalDAO;

	@Inject
	private RuntimeDataService runtimeDataService;

	@Inject
	private EmailService emailService;

	@Inject
	private Logger log;

	@PostConstruct
	public void setTimer() {

		// TODO: move the configuration to the properties table
		ScheduleExpression schedule = new ScheduleExpression();
		schedule.second("4");
		schedule.minute("*");
		schedule.hour("*");
		// schedule.hour("8, 13, 16");
		schedule.dayOfWeek("Mon-Fri");
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setPersistent(false);
		timerService.createCalendarTimer(schedule, timerConfig);
	}

	@Timeout
	public void timeout(Timer timer) {
		if (propertyService.getBoolean("mail.reminder.active")) {
			log.info("Reminder activated.");

			List<SimpleApproval> simpleApprovals = simpleApprovalDAO.findOpenApprovalsProcessInstanceId();
			for (SimpleApproval simpleApproval : simpleApprovals) {
				log.info("ProcessInstanceId: " + simpleApproval);
				// TODO: make the query more efficient
				List<Status> statusList = new ArrayList<Status>();
				statusList.add(Status.InProgress);
				statusList.add(Status.Reserved);
				List<TaskSummary> tasks = runtimeDataService.getTasksByStatusByProcessInstanceId(simpleApproval.getProcessInstanceId(), statusList, null);
				if (tasks.size() > 1) {
					log.warning("More then one task for processInstanceId: " + simpleApproval.getProcessInstanceId());
				}
				TaskSummary task = tasks.get(0);
				String actualOwner = task.getActualOwnerId();
				sendReminderEmail(actualOwner, simpleApproval.getProcessInstanceId(), simpleApproval.getSubject());
			}
		} else {
			log.warning("Reminder inactive!");
		}

	}

	public void sendReminderEmail(String actualOwner, long processInstanceId, String taskSubject) {

		String templateFileName = "mail-reminder.ftl";
		String emailSubject = "Gangehi System Open Tasks Reminder";
		Set<String> toSet = new HashSet<String>();
		toSet.add(actualOwner);

		String applicationUrl = propertyService.getString("mail.application.url");
		String taskUrl = applicationUrl + "secure/simpleWf.xhtml?processInstanceId=" + processInstanceId;

		Map<String, String> data = new HashMap<String, String>();

		data.put("taskUrl", taskUrl);
		data.put("taskSubject", taskSubject);

		emailService.sendMessage(emailSubject, data, toSet, templateFileName);
	}

}
