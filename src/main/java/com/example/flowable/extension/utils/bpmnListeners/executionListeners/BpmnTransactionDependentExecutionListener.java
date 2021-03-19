package com.example.flowable.extension.utils.bpmnListeners.executionListeners;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.delegate.TransactionDependentExecutionListener;

import java.util.Map;

/**
 * Created by chakib.daii on 3/18/2021.
 */
public class BpmnTransactionDependentExecutionListener implements TransactionDependentExecutionListener {
    @Override
    public void notify(String processInstanceId, String executionId, FlowElement flowElement, Map<String, Object> executionVariables, Map<String, Object> customPropertiesMap) {

    }
}
