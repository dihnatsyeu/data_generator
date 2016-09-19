package error_reporter;

import org.apache.log4j.Logger;

public class ErrorReporter {
    
    
    private static Logger logger = Logger.getLogger(ErrorReporter.class);
    
    
    public static void reportError(Throwable e){
        logger.error(e.getMessage());
        logger.trace(e.getMessage(), e);
    }
    
    public static void reportAndRaiseError(String message, Throwable e) {
        reportError(e);
        throw new Error(message);
    }
    
    public static void raiseError(String message) {
        throw new Error(message);
    }
}
