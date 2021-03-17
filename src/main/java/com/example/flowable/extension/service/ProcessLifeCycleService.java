package com.example.flowable.extension.service;

import com.example.flowable.extension.domain.Constants;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ProcessLifeCycleService {

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private RepositoryService repositoryService;

	private static final Logger log = LoggerFactory.getLogger(ProcessLifeCycleService.class);


	public String startProcess(String processKey, Map<String, Object> variables, String trigger, String triggerDefinition) {
		log.info("Starting process");
		if(trigger != null && trigger.equals(Constants.SIGNAL)) {
			log.info("Signal: "+triggerDefinition);
			runtimeService.signalEventReceived(triggerDefinition, variables);
			return runtimeService.createExecutionQuery()
					.signalEventSubscriptionName(triggerDefinition)
					.singleResult().getProcessInstanceId();

		}else if(trigger != null && trigger.equals(Constants.MESSAGE)) {
			log.info("Message: "+triggerDefinition);
			//runtimeService.startProcessInstanceByMessage(triggerDefinition,variables);
			return runtimeService.createExecutionQuery()
					.messageEventSubscriptionName(triggerDefinition)
					.singleResult().getProcessInstanceId();

		}else
			return runtimeService.startProcessInstanceByKey(processKey, variables).getProcessInstanceId()/*.getReferenceId()*/;
	}



	public void progressProcessByTaskId(String taskId, Map<String, Object> variables) {
		log.info("Progress process");
		taskService.complete(taskId, variables);
	}


	public void progressProcessByProcessId(String processInstanceId, Map<String, Object> variables) {
		log.info("Progress process");
		taskService.complete(taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult().getId(), variables);
	}


	public void regressProcessByTaskId(String currentTaskId ) {
		log.info("Regress process");
		Task currentTask = taskService.createTaskQuery()
				.taskId(currentTaskId)
				.singleResult();

		HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(currentTask.getProcessInstanceId())
				.orderByHistoricTaskInstanceEndTime()
				.desc().list().get(0);
		log.info(currentTask.getTaskDefinitionKey() +" to "+ historicTaskInstance.getTaskDefinitionKey());

		runtimeService.createChangeActivityStateBuilder()
				.processInstanceId(currentTask.getProcessInstanceId())
				.moveActivityIdTo(currentTask.getTaskDefinitionKey(), historicTaskInstance.getTaskDefinitionKey());
	}


	public void regressProcessByProcessId(String processInstanceId) {
		log.info("Regress process");
		String currentTaskId = taskService.createTaskQuery().processDefinitionKey(processInstanceId).singleResult().getId();

		HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId)
				.orderByHistoricTaskInstanceEndTime()
				.desc().list().get(0);

		Task currentTask = taskService.createTaskQuery()
				.taskId(currentTaskId)
				.singleResult();

		log.info(currentTask.getTaskDefinitionKey() +" to "+ historicTaskInstance.getTaskDefinitionKey());

		runtimeService.createChangeActivityStateBuilder()
				.processInstanceId(processInstanceId)
				.moveActivityIdTo(currentTask.getTaskDefinitionKey(), historicTaskInstance.getTaskDefinitionKey())
				.changeState();


	}


	public void finishProcess(String processId, String deleteReason) {
		log.info("Finish process");
		runtimeService.deleteProcessInstance(processId, deleteReason);
	}


	public List<Map<String, Object>> getTasksUser(String assignee) {
		log.info("Fetching user tasks");
		return taskService.createTaskQuery().taskCandidateUser(assignee).list()
				.stream().map(Task -> {
					Map<String, Object> task = new HashMap<>();
					task.put(Constants.TASK_ID, Task.getId());
					task.put(Constants.PROCESS_INSTANCE_ID, Task.getProcessInstanceId());
					task.put(Constants.TASK_NAME, Task.getName());
					task.put(Constants.TASK_CREATION_TIME, Task.getCreateTime());
					task.put(Constants.TASK_OWNER, Task.getOwner());
					task.put(Constants.TASK_ASSIGNEE, Task.getAssignee());
					return task;
				}).collect(Collectors.toList());
	}


	public List<Map<String, Object>> getTasksGroup(String assigneeGroup) {
		log.info("Fetching group tasks");
		return taskService.createTaskQuery().taskCandidateGroup(assigneeGroup).list()
				.stream().map(Task -> {
					Map<String, Object> task = new HashMap<>();
					task.put(Constants.TASK_ID, Task.getId());
					task.put(Constants.PROCESS_INSTANCE_ID, Task.getProcessInstanceId());
					task.put(Constants.TASK_NAME, Task.getName());
					task.put(Constants.TASK_CREATION_TIME, Task.getCreateTime());
					task.put(Constants.TASK_OWNER, Task.getOwner());
					task.put(Constants.TASK_ASSIGNEE, Task.getAssignee());
					return task;
				}).collect(Collectors.toList());
	}

	public List<Map<String, Object>> getTasksAssignee(String assignee) {
		log.info("Fetching group tasks");
		return taskService.createTaskQuery().taskAssignee(assignee).list()
				.stream().map(Task -> {
					Map<String, Object> task = new HashMap<>();
					task.put(Constants.TASK_ID, Task.getId());
					task.put(Constants.PROCESS_INSTANCE_ID, Task.getProcessInstanceId());
					task.put(Constants.TASK_NAME, Task.getName());
					task.put(Constants.TASK_CREATION_TIME, Task.getCreateTime());
					task.put(Constants.TASK_OWNER, Task.getOwner());
					task.put(Constants.TASK_ASSIGNEE, Task.getAssignee());
					task.put(Constants.TASK_FORM_KEY, Task.getFormKey());
					return task;
				}).collect(Collectors.toList());
	}

	public List<Map<String, Object>> getFinishedProcessTasks(String processInsatnceId) {
		log.info("Fetching finished Tasks for Process"+ processInsatnceId);
		return historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInsatnceId)
				.finished()
				.includeProcessVariables()
				.orderByHistoricTaskInstanceEndTime().asc()
				.list()
				.stream().map(Activity -> {
					Map<String, Object> act = new HashMap<>();
					act.put(Constants.TASK_ID, Activity.getId());
					act.put(Constants.TASK_NAME, Activity.getName());
					act.put(Constants.TASK_START_TIME, Activity.getCreateTime());
					act.put(Constants.TASK_OWNER, Activity.getEndTime());
					act.put(Constants.TASK_ASSIGNEE, Activity.getAssignee());
					return act;
				}).collect(Collectors.toList());
	}

	public List<Map<String, Object>> getFinishedProcessActivities(String processKey) {
		log.info("Fetching finished Tasks ");
		return historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(processKey)
				.finished()
				.orderByHistoricActivityInstanceEndTime().asc()
				.list()
				.stream().map(Activity -> {
					Map<String, Object> act = new HashMap<>();
					act.put(Constants.TASK_ID, Activity.getId());
					act.put(Constants.ACTIVITY_ID, Activity.getActivityId());
					act.put(Constants.TASK_NAME, Activity.getActivityName());
					act.put(Constants.TASK_START_TIME, Activity.getStartTime());
					act.put(Constants.TASK_END_TIME, Activity.getEndTime());
					act.put(Constants.TASK_ASSIGNEE, Activity.getAssignee());
					act.put(Constants.ACTIVITY_CLASS, Activity.getClass());
					act.put(Constants.ACTIVITY_TYPE, Activity.getActivityType());
					return act;
				}).collect(Collectors.toList());
	}

	public List<Map<String, Object>> getFinishedProcessWithCallActivityActivities(String processKey) {
		log.info("Fetching finished Tasks ");
		List<List<Map<String, Object>>> activities = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(processKey)
				.finished()
				.orderByHistoricActivityInstanceEndTime().asc()
				.list()
				.stream().map(Activity -> {
					if(Activity.getActivityType().equals(Constants.CALL_ACTIVITY)){
						return historyService.createHistoricActivityInstanceQuery()
								.processInstanceId(Activity.getCalledProcessInstanceId())
								.finished()
								.orderByHistoricActivityInstanceEndTime().asc()
								.list()
								.stream().map(callActivity ->{
									Map<String, Object> callAct = new HashMap<>();
									callAct.put(Constants.TASK_ID, callActivity.getId());
									callAct.put(Constants.ACTIVITY_ID, callActivity.getActivityId());
									callAct.put(Constants.TASK_NAME, callActivity.getActivityName());
									callAct.put(Constants.TASK_START_TIME, callActivity.getStartTime());
									callAct.put(Constants.TASK_END_TIME, callActivity.getEndTime());
									callAct.put(Constants.TASK_ASSIGNEE, callActivity.getAssignee());
									callAct.put(Constants.ACTIVITY_CLASS, callActivity.getClass());
									callAct.put(Constants.ACTIVITY_TYPE, callActivity.getActivityType());
									return callAct;
								}).collect(Collectors.toList());
					}else{
						List<Map<String, Object>> actt = new ArrayList<>();
						Map<String, Object> act = new HashMap<>();
						act.put(Constants.TASK_ID, Activity.getId());
						act.put(Constants.ACTIVITY_ID, Activity.getActivityId());
						act.put(Constants.TASK_NAME, Activity.getActivityName());
						act.put(Constants.TASK_START_TIME, Activity.getStartTime());
						act.put(Constants.TASK_END_TIME, Activity.getEndTime());
						act.put(Constants.TASK_ASSIGNEE, Activity.getAssignee());
						act.put(Constants.ACTIVITY_CLASS, Activity.getClass());
						act.put(Constants.ACTIVITY_TYPE, Activity.getActivityType());
						actt.add(act);
						return actt;
					}

				}).collect(Collectors.toList());

		return activities.stream()
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

	//user will become the assignee of the task, and the task will disappear from every task list of the other members of the group
	public void claimTaskByTaskId(String taskId, String assignee) {
		log.info("Claiming Task process");
		taskService.claim(taskId, assignee);
	}

	public void claimTaskByProcessId(String processInstanceId, String assignee) {
		log.info("Claiming Task process");
		taskService.claim(taskService.createTaskQuery()
				.processInstanceId(processInstanceId)
				.singleResult().getId(), assignee);
	}

	public void triggerProcess(String processId, String trigger, String triggerDefinition) {
		log.info("trigger process "+ processId +" last Execution with :"+triggerDefinition +" "+trigger);
		List<Execution> processExecutions = runtimeService.createExecutionQuery()
				.processInstanceId(processId)
				.orderByProcessInstanceId()
				.desc().list();

		if(trigger.equals(Constants.SIGNAL)) {

			log.info("Signal: "+triggerDefinition);
			runtimeService.signalEventReceived(triggerDefinition);

		}else if(trigger.equals(Constants.ESCALATION)) {

		}else if(trigger.equals(Constants.MESSAGE)) {

			log.info("Message: "+triggerDefinition);
			runtimeService.messageEventReceived(triggerDefinition, processExecutions.get(0).getId());
		}

	}

	public void triggerEventSubprocess(String processId, String subprocessKey, String trigger, String triggerDefinition) {
		log.info("trigger Event Subprocess "+ subprocessKey +" of "+ processId +" with :"+triggerDefinition +" "+trigger);

		Execution signalExcution = runtimeService.createExecutionQuery()
				.processInstanceId(processId)
				.activityId(subprocessKey).singleResult();

		log.info("execution "+ signalExcution);

		if(trigger.equals(Constants.SIGNAL)) {

			log.info("Signal: "+triggerDefinition);
			runtimeService.signalEventReceived(triggerDefinition, signalExcution.getId());

		}else if(trigger.equals(Constants.ESCALATION)) {

		}else if(trigger.equals(Constants.MESSAGE)) {

			log.info("Message: "+triggerDefinition);
			runtimeService.messageEventReceived(triggerDefinition, signalExcution.getId());
		}

	}

	public void progressProcessWithCallActivityByProcessId(String processInstanceId, Map<String, Object> variables) {
		log.info("Progress process");
		try {
			String callInstanceId = runtimeService.createProcessInstanceQuery()
					.superProcessInstanceId(processInstanceId).active().singleResult().getProcessInstanceId();

			log.info("call activity: "+callInstanceId);

			log.info("Active call activity");
			taskService.complete(taskService.createTaskQuery()
					.processInstanceId(callInstanceId)
					.singleResult().getId(), variables);

		}catch(Exception e) {
			log.info("call activity exception: "+e.getMessage());
			log.info("No Active call activity");
			taskService.complete(taskService.createTaskQuery()
					.processInstanceId(processInstanceId)
					.orderByTaskCreateTime()
					.active().desc().list().get(0).getId(), variables);
		}


	}
	//TODO: have to fix causes problems when calling in parallel active tasks state
	public Task getTaskFromExecution(String processId){
		log.info("Process Id: {} get Task from Execution: {}", processId, taskService.createTaskQuery().processInstanceId(processId).list());
		return taskService.createTaskQuery().processInstanceId(processId)
				.active().singleResult();
	}

	public void exportBPMNModel(String processKey){
		ProcessDefinition process = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processKey).latestVersion().singleResult();
		InputStream stream = repositoryService.getResourceAsStream(process.getDeploymentId(), process.getDiagramResourceName());

		File targetFile = new File(processKey+ Constants.MODEL_EXTENSION);

		try {
			java.nio.file.Files.copy(
					stream,
					targetFile.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
