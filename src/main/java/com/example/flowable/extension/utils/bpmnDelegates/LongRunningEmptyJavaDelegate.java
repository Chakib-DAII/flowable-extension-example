package com.example.flowable.extension.utils.bpmnDelegates;

import org.flowable.common.engine.api.async.AsyncTaskInvoker;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.FutureJavaDelegate;

import java.util.concurrent.CompletableFuture;

/**
 * Created by chakib.daii on 3/18/2021.
 */
public class LongRunningEmptyJavaDelegate implements FutureJavaDelegate<String> {
    @Override
    public CompletableFuture<String> execute(DelegateExecution execution, AsyncTaskInvoker taskInvoker) {
        // This is running in the same transaction as the process instance and is still possible to set and extract data from the execution
        String input = (String) execution.getVariable("input");
        // The taskInvoker is a common invoker provided by Flowable that can be used to submit complex executions on a new thread.
        // However, you don't have to use it, you can use your own custom ExecutorService or return a CompletableFuture from your own services.
        return taskInvoker.submit(() -> {
            // This is running on a new thread. The execution shouldn't be used here.
            // There is also no transaction here. In case a new transaction is needed, then it should be managed by your own services
            // Perform some complex logic that takes some time, e.g. invoking an external service
            return "done";
        });
    }

    @Override
    public void afterExecution(DelegateExecution execution, String executionData) {
        // This is running in the same transaction and thread as the process instance and data can be set on the execution
        execution.setVariable("longRunningResult", executionData);
    }
}
