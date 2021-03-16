package com.example.flowable.extension.utils.bpmnListeners.taskListeners;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by chakib.daii on 9/24/2020.
 */
@Component
public class BpmnTaskListener implements TaskListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BpmnTaskListener.class);

    @Override
    public void notify(DelegateTask delegateTask) {

        LOGGER.info("\nTask Info: "+ delegateTask.getEventName()
                        + "\nTask Name: "+ delegateTask.getName()
                        + "\nTask Key: "+ delegateTask.getTaskDefinitionKey()
                        + "\nTask Id: "+ delegateTask.getId()
                        + "\nForm Key: "+ delegateTask.getFormKey()
                        +"\nVariables: \n"+ delegateTask.getVariables().toString()
        );
    }
}
