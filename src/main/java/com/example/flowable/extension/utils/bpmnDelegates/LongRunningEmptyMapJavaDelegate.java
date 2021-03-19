package com.example.flowable.extension.utils.bpmnDelegates;

import org.flowable.engine.delegate.MapBasedFlowableFutureJavaDelegate;
import org.flowable.engine.delegate.ReadOnlyDelegateExecution;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chakib.daii on 3/18/2021.
 */
public class LongRunningEmptyMapJavaDelegate implements MapBasedFlowableFutureJavaDelegate {
    @Override
    public Map<String, Object> execute(ReadOnlyDelegateExecution inputData) {
        // The execution contains a read only snapshot of the delegate execution
        // This is running on a new thread. The execution shouldn't be used here.
        // There is also no transaction here. In case a new transaction is needed, then it should be managed by your own services
        // Perform some complex logic that takes some time, e.g. invoking an external service
        Map<String, Object> result = new HashMap<>();
        result.put("longRunningResult", "done");
        // All the values from the returned map will be set on the execution
        return result;
    }
}
