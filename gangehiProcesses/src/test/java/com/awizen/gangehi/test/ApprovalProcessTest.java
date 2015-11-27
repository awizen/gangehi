package com.awizen.gangehi.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;

/**
 * This is a sample test of the evaluation process.
 */
public class ApprovalProcessTest extends MyJbpmJUnitBaseTestCase {


	@Test
	public void testDeleteProcess() {

		RuntimeManager manager = createRuntimeManager("approval.bpmn");
		RuntimeEngine engine = getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		KieRuntimeLogger log = KieServices.Factory.get().getLoggers().newThreadedFileLogger(ksession, "test", 1000);
		ksession.getWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());

		TaskService taskService = engine.getTaskService();

		long processInstanceId = startProcess(ksession, taskService);

		callDelete(taskService);

		assertProcessInstanceCompleted(processInstanceId);

		System.out.println("Process instance completed");
		log.close();

		manager.disposeRuntimeEngine(engine);
		manager.close();
	}

	@Test
	public void testEvaluationProcess() {

		RuntimeManager manager = createRuntimeManager("approval.bpmn");
		RuntimeEngine engine = getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		KieRuntimeLogger log = KieServices.Factory.get().getLoggers().newThreadedFileLogger(ksession, "test", 1000);
		ksession.getWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());

		TaskService taskService = engine.getTaskService();

		long processInstanceId = startProcess(ksession, taskService);

		approveTask(taskService, "userA", "userB");
		assertNodeTriggered(processInstanceId, "notify next approver");
		List<TaskSummary> tasks = taskService.getTasksOwned("userB", "en-UK");
		assertEquals(1, tasks.size());

		approveTask(taskService, "userB", "userC");
		assertNodeTriggered(processInstanceId, "notify next approver");
		tasks = taskService.getTasksOwned("userC", "en-UK");
		assertEquals(1, tasks.size());


		approveTask(taskService, "userC", "userD");
		tasks = taskService.getTasksOwned("userD", "en-UK");
		assertEquals(1, tasks.size());

		approveTask(taskService, "userD", null);

		assertNodeTriggered(processInstanceId, "authorised");

		System.out.println("Process instance completed");
		log.close();

		manager.disposeRuntimeEngine(engine);
		manager.close();
	}

	@Test
	public void testRejectAbortProcess() {

		RuntimeManager manager = createRuntimeManager("approval.bpmn");
		RuntimeEngine engine = getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		KieRuntimeLogger log = KieServices.Factory.get().getLoggers().newThreadedFileLogger(ksession, "test", 1000);
		ksession.getWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());

		TaskService taskService = engine.getTaskService();

		long processInstanceId = startProcess(ksession, taskService);

		approveTask(taskService, "userA", "userB");
		assertNodeTriggered(processInstanceId, "notify next approver");
		List<TaskSummary> tasks = taskService.getTasksOwned("userB", "en-UK");
		assertEquals(1, tasks.size());

		rejectTask(taskService, "userB", "userC");
		assertNodeTriggered(processInstanceId, "notify originator");
		tasks = taskService.getTasksOwned("userC", "en-UK");
		assertEquals(1, tasks.size());

		TaskSummary taskSummary = tasks.get(0);
		assertEquals("edit approval item", taskSummary.getName());

		abortTask(taskService, "userC");
		assertNodeTriggered(processInstanceId, "aborted");

		System.out.println("Process instance completed");
		log.close();

		manager.disposeRuntimeEngine(engine);
		manager.close();
	}

	private void callDelete(TaskService taskService) {
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("userA", "en-UK");
		assertEquals(1, tasks.size());
		TaskSummary taskSummary = tasks.get(0);

		System.out.println("'userA' performing delete");

		Map<String, Object> results = new HashMap<String, Object>();
		results.put("userAction", "delete");
		taskService.complete(taskSummary.getId(), "userA", results);
	}

	private void approveTask(TaskService taskService, String currentApprover, String nextApprover) {
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(currentApprover, "en-UK");
		assertEquals(1, tasks.size());
		TaskSummary taskSummary = tasks.get(0);

		System.out.println("'" + currentApprover + "' performing approve");

		Map<String, Object> results = new HashMap<String, Object>();
		results.put("nextApprover", nextApprover);
		results.put("userAction", "approve");
		if (taskSummary.getStatus().equals(Status.Reserved)) {
			taskService.start(taskSummary.getId(), currentApprover);
		}
		taskService.complete(taskSummary.getId(), currentApprover, results);

		System.out.println("Approval for '" + currentApprover + "' completed");
	}

	private void rejectTask(TaskService taskService, String currentApprover, String nextApprover) {
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(currentApprover, "en-UK");
		TaskSummary taskSummary = tasks.get(0);

		System.out.println("'" + currentApprover + "' performing reject");

		Map<String, Object> results = new HashMap<String, Object>();
		results.put("nextApprover", nextApprover);
		results.put("userAction", "reject");
		if (taskSummary.getStatus().equals(Status.Reserved)) {
			taskService.start(taskSummary.getId(), currentApprover);
		}
		taskService.complete(taskSummary.getId(), currentApprover, results);

		System.out.println("Reject for '" + currentApprover + "' completed");
	}

	private void abortTask(TaskService taskService, String currentApprover) {
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(currentApprover, "en-UK");
		TaskSummary taskSummary = tasks.get(0);

		System.out.println("'" + currentApprover + "' performing abort");

		Map<String, Object> results = new HashMap<String, Object>();
		results.put("userAction", "abort");
		if (taskSummary.getStatus().equals(Status.Reserved)) {
			taskService.start(taskSummary.getId(), currentApprover);
		}
		taskService.complete(taskSummary.getId(), currentApprover, results);

		System.out.println("Abort for '" + currentApprover + "' completed");
	}

	private long startProcess(KieSession ksession, TaskService taskService) {
		// start a new process instance
		ProcessInstance processInstance = ksession.startProcess("com.awizen.gangehi.simple_approval");


		long processInstanceId = processInstance.getId();
		String processId = processInstance.getProcessId();

		System.out.println("Process started ... processInstance:" + processInstanceId + " processId:" + processId );

		List<Long> tasksByProcessInstanceId = taskService.getTasksByProcessInstanceId(processInstanceId);
		assertEquals(1, tasksByProcessInstanceId.size());

		Long taskId = tasksByProcessInstanceId.get(0);

		List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();
		potentialOwners.add(new UserImpl("userA"));
		taskService.nominate(taskId, "Administrator", potentialOwners );

		taskService.start(taskId, "userA");
		return processInstanceId;
	}

	public ApprovalProcessTest() {
		super(true, true);
	}

	@Override
	@Before
	public void setUp() throws Exception{
		super.setUp();
	}

}
