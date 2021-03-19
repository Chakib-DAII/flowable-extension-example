package com.example.flowable.extension.utils.bpmnListeners.taskListeners;

import org.flowable.bpmn.model.Task;
import org.flowable.engine.delegate.TransactionDependentTaskListener;

import java.util.Map;

/**
 * Created by chakib.daii on 3/18/2021.
 */
public class BpmnTransactionDependentTaskListener implements TransactionDependentTaskListener {
    @Override
    public void notify(String processInstanceId, String executionId, Task task, Map<String, Object> executionVariables, Map<String, Object> customPropertiesMap) {

    }
}
