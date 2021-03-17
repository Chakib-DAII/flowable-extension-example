package com.example.flowable.extension.api;

import com.example.flowable.extension.service.ProcessLifeCycleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/process")
public class ProcessController {
		
	@Autowired
	ProcessLifeCycleService processService;
	
	@Autowired
    ObjectMapper mapper;
	
    @PostMapping(value="/start/{processKey}/{trigger}")
	public ResponseEntity<String> startProcess(@PathVariable(name = "processKey") String processKey, @RequestParam(required=false) String trigger, @RequestParam(required=false) String triggerDefinition, @RequestBody Map<String, Object> variables) {
		try {
				return new ResponseEntity<String>(processService.startProcess(processKey, variables, trigger, triggerDefinition), HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
    @PostMapping(value="/claim-pid/{processId}/{assignee}")
	public ResponseEntity<String> claimTaskByProcessId(@PathVariable(name="processId") String processId, @PathVariable(name="assignee") String assignee) {
    	try {
    		processService.claimTaskByProcessId(processId, assignee);
			return new ResponseEntity<String>(processId, HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
    
    @PostMapping(value="/progress-pid/{processId}")
	public ResponseEntity<String> progressProcessByProcessId(@PathVariable(name="processId") String processId, @RequestBody String variables) {
    	try {
    		processService.progressProcessByProcessId(processId, mapper.readValue(variables,Map.class));
			return new ResponseEntity<String>(processId, HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
    
    @PostMapping(value="/progress-call-activity-pid/{processId}")
	public ResponseEntity<String> progressProcessWithCallActivityByProcessId(@PathVariable(name="processId") String processId, @RequestBody String variables) {
    	try {
    		processService.progressProcessWithCallActivityByProcessId(processId, mapper.readValue(variables,Map.class));
			return new ResponseEntity<String>(processId, HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
    
    @PostMapping(value="/progress-tid/{taskId}")
	public ResponseEntity<String> progressProcessByTaskId(@PathVariable(name="taskId") String taskId, @RequestBody String variables) {
    	try {
    		processService.progressProcessByTaskId(taskId, mapper.readValue(variables,Map.class));
			return new ResponseEntity<String>(taskId, HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
    @PostMapping(value="/regress-pid/{processId}")
	public ResponseEntity<String> regressProcessByProcessId(@PathVariable(name="processId") String processId) {
    	try {
    		processService.regressProcessByProcessId(processId);
			return new ResponseEntity<String>(processId, HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    }
    
    @PostMapping(value="/regress-tid/{taskId}")
	public ResponseEntity<String> regressProcessByTaskId(@PathVariable(name="taskId") String taskId) {
    	try {
    		processService.regressProcessByTaskId(taskId);
			return new ResponseEntity<String>(taskId, HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
    
    @PostMapping(value="/finish/{processId}")
	public ResponseEntity<String> finishProcess(@PathVariable(name="processId") String processId, @RequestParam String deleteReason) {
    	try {
    		processService.finishProcess(processId, deleteReason);
			return new ResponseEntity<String>(processId, HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    }
	
    @GetMapping(value="/tasks-user")
	public ResponseEntity<String> getTasksUser(@RequestParam String assignee) {
    	try {
			return new ResponseEntity<String>(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(processService.getTasksUser(assignee)), HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    }
    
    @GetMapping(value="/tasks-group")
	public ResponseEntity<String> getTasksGroup(@RequestParam String assignee) {
    	try {
			return new ResponseEntity<String>(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(processService.getTasksUser(assignee)), HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    }
    
    @GetMapping(value="/tasks-assignee")
	public ResponseEntity<String> getTasksAssignee(@RequestParam String assignee) {
    	try {
			return new ResponseEntity<String>(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(processService.getTasksAssignee(assignee)), HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    }

	@GetMapping(value="/finished-tasks/{processKey}")
	public ResponseEntity<String> getFinishedProcessTasks(@PathVariable(name = "processKey") String processKey) {
		try {
			return new ResponseEntity<String>(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(processService.getFinishedProcessTasks(processKey)), HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

    @GetMapping(value="/finished-activities/{processKey}")
	public ResponseEntity<String> getFinishedProcessActivities(@PathVariable(name = "processKey") String processKey) {
    	try {
			return new ResponseEntity<String>(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(processService.getFinishedProcessActivities(processKey)), HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    }

    @GetMapping(value="/finished-activities-call-activity/{processKey}")
	public ResponseEntity<String> getFinishedProcessWithCallActivityActivities(@PathVariable(name = "processKey") String processKey) {
		try {
			return new ResponseEntity<String>(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(processService.getFinishedProcessWithCallActivityActivities(processKey)), HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

    @PostMapping(value="/trigger/{processId}/{trigger}")
	public ResponseEntity<String> triggerProcess(@PathVariable(name = "processId") String processId, @PathVariable(name = "trigger") String trigger, @RequestParam String triggerDefinition) {
		try {
				processService.triggerProcess(processId, trigger, triggerDefinition);
				return new ResponseEntity<>(HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value="/triggerInstance/{processId}/{subprocessKey}/{trigger}")
	public ResponseEntity<String> triggerProcessInstance(@PathVariable(name = "processId") String processId, @PathVariable(name = "subprocessKey") String subprocessKey, @PathVariable(name = "trigger") String trigger, @RequestParam String triggerDefinition) {
		try {
			processService.triggerEventSubprocess(processId, subprocessKey, trigger, triggerDefinition);
			return new ResponseEntity<>(HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value="/export/{processKey}")
	public ResponseEntity<String> exportProcessModel(@PathVariable(name = "processKey") String processKey) {
		try {
			processService.exportBPMNModel(processKey);
			return new ResponseEntity<>(HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
