package data_for_entity;

import data_for_entity.data_providers.EntityDataProvider;
import data_for_entity.instance_managers.InstanceManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose of this class is to create an object with filled fields. Call method randomEntity
 * with type of object to create.
 * Object fields are filled according to fields' types. All java primitive types are recognized.
 * Map, Collection interface are supported.Nested collections are not supported (e.g. collection inside a collection etc).
 * You can mark field with {@link data_for_entity.annotations.DataIgnore} to skip field filling.
 * You can control generated value type by setting {@link EntityDataProvider}
 * via {@link data_for_entity.annotations.WithFieldDataType}. In case field value depends on
 * other field's, you must specify {@link data_for_entity.annotations.WithDataDependencies}. Only dependencies
 * from the same object's field is supported, {@link EntityDataProvider} must be specified in this case.
 * By default, lib will try to create instance using default constructor. If different behavior is needed,
 * specify custom {@link InstanceManager}.
 */
public class RandomEntities {
    
    
    private Logger logger = Logger.getLogger(RandomEntities.class);
    private ObjectAggregator objectInitializer = new ObjectAggregator();
    
    /**
     * Sets passed InstanceManager. Object is used to create instance by passed
     * types. Must be set before calling randomEntity
     * @param instanceManager {@link InstanceManager} object.
     */
    public void setInstanceManager(InstanceManager instanceManager) {
        objectInitializer.setInstanceManager(instanceManager);
    }
    
    /**
     * Creates an object with the passed type with filled fields according to specified
     * rules. Only mandatory fields are filled, i.e. those that are not marked
     * with {@link data_for_entity.annotations.DataIgnore}
     * @param objectType Type of object to create.
     * @return object with passed type with filled fields.
     */
    public <T>T randomEntity(Class<T> objectType) {
        logger.info("Creating object with type: "+ objectType);
        return objectType.cast(objectInitializer.generateObjectFields(objectType));
       
    }
    
    public <T> List<T> randomEntities(Class<T> classType, int collectionSize) {
        ArrayList<T> objectsList = new ArrayList<>();
        for (int i=1; i<=collectionSize; i++) {
            objectsList.add(randomEntity(classType));
        }
        return objectsList;
    }
    
    
}
    
