package data_for_entity.instance_managers;

public interface InstanceManager {
    
   <T>T createInstance(Class<T> classType) throws Error;
    
}
