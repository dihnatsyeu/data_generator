package data_for_entity;

import error_reporter.ErrorReporter;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class TasksExecutor {
    
    private ExecutorService executor;
    private final long timeout = 60;
    private Logger logger = Logger.getLogger(TasksExecutor.class);
    
    TasksExecutor() {
       executor = Executors.newWorkStealingPool();
    }
    
    Task submitTask(Runnable task) {
        return new Task(executor.submit(task));
    }
    
    boolean waitForCompletion() {
        executor.shutdown();
        try {
            return executor.awaitTermination(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            ErrorReporter.reportError(e);
            logger.debug("Waiting for completion was interrupted");
            return false;
        }
    }
}
