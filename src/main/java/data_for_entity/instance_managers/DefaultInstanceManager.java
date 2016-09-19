package data_for_entity.instance_managers;

import error_reporter.ErrorReporter;

public class DefaultInstanceManager implements InstanceManager {
    
    @Override
    public Object createInstance(Class<?> classType) throws Error {
        try {
            return  classType.newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassCastException e) {
            ErrorReporter.reportAndRaiseError("Cannot create instance of class "+classType, e);
        }
        return null;
    }
}
