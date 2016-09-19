package data_for_entity.instance_managers;

public interface InstanceManager {
    
    Object createInstance(Class<?> classType) throws Error;
    
}
