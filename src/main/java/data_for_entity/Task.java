package data_for_entity;

import error_reporter.ErrorReporter;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Encapsulates Future task
 */
class Task {
    
    
    private Future future;
    private Logger logger = Logger.getLogger(Task.class);
    private final long timeout = 60;
    
    Task(Future future) {
        this.future = future;
    }
    
    Object getResult() {
        try {
            return future.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            ErrorReporter.reportError(e);
            logger.debug("Error occurred waiting for results of task");
            return null;
        }
    }
    
}
