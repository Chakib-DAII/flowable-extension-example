package com.example.flowable.extension.utils.bpmnDelegates;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by chakib.daii on 9/24/2020.
 */
@Component
public class EmptyDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmptyDelegate.class);

    @Override
    public void execute(DelegateExecution delegateExecution) {
        LOGGER.info("\nExecution Info: "+ delegateExecution.getProcessDefinitionId()
                +"\nExecution Class: "+ delegateExecution.getCurrentFlowElement().getClass().getCanonicalName()
                + "\nTask: "+ delegateExecution.getCurrentFlowElement().getName()
                +"\nEvent: "+ delegateExecution.getCurrentFlowableListener().getEvent()
                +"\nVariables: \n"+ delegateExecution.getVariables().toString()
        );
    }
}
