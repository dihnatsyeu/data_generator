package data_for_entity;

import data_for_entity.data_providers.EntityDataProvider;
import data_for_entity.instance_managers.DefaultInstanceManager;
import data_for_entity.instance_managers.InstanceManager;
import data_for_entity.provider_resolver.ProviderResolver;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Class encapsulates algorithm of parsing and populating object's fields with values
 */
class ObjectAggregator {
    
    private Logger logger = Logger.getLogger(ObjectAggregator.class);
    private FieldsCollection fieldsCollection = new FieldsCollection();
    private InstanceManager instanceManager = new DefaultInstanceManager();
    
    
    void setInstanceManager(InstanceManager manager) {
        this.instanceManager = manager;
    }
    
    /**
     * For every field Future task for aggregation is created and pushed to executor.
     * Method waits unless all tasks are completed and returned populated java bean
     * according to specified rules.
     * @param objectType Type of object to populate with data.
     * @return object of passed objectType with populated data.
     */
    Object generateObjectFields(Class<?> objectType) {
        fieldsCollection.collectFieldsByType(objectType);
        logger.debug("Searching for required fields");
        List<ObjectField> requiredFields = fieldsCollection.createFieldsFilter().filterByRequired();
        Object instance = instanceManager.createInstance(objectType);
        if ((requiredFields == null) || requiredFields.size() == 0) {
            logger.debug("There are no required fields for entity: " + instance +
                    "Instance will be returned as it is");
            return instance;
        }
        TasksExecutor executor = new TasksExecutor();
        for (ObjectField objectField:requiredFields) {
            FieldAggregator fieldAggregator = new FieldAggregator(objectField, instance);
            executor.submitTask(fieldAggregator);
            
        }
        Boolean tasksCompleted = executor.waitForCompletion();
        if (!tasksCompleted) {
            logger.warn("Not all fields are generated! View debug logs for details");
        }
        logger.info("Object is created with filled fields");
        return instance;
    }
    
    /**
     * Purpose of this class is to populate passed {@link ObjectField} with value that is generated
     * based on field's options or field type. Fields options have higher priority in defining of a value.
     */
    private class FieldAggregator implements Runnable {
    
        private final ObjectField field;
        private Object instance;
        private FieldOptionsManager fieldOptions;
        
        FieldAggregator(ObjectField objectField, Object instance) {
            this.field = objectField;
            this.instance = instance;
            this.fieldOptions = new FieldOptionsManager(objectField.getField());
        }
    
      
    
        /**
         * algorithm that defines rules for populating field with value.
         * If field has dependencies, first those fields should be populated.
         * Then current field is populated based on field's options or field's type.
         */
        @Override
        public void run() {
            logger.debug("Generating value for required field:" + field.getName());
            synchronized (field) {
                if (field.getValue(instance) != null) {
                    logger.debug("Field: " + field.getName() + " already has value. Skipping...");
                    return;
                }
                //if field has dependencies, first those fields will initialized with values
                if (fieldOptions.hasDependencies()) {
                    logger.debug("Found dependencies flag for field.");
                    List<ObjectField> dependenceFields = fieldsCollection.createFieldsFilter().
                            filterByDependentFields(fieldOptions.getDependencies().getFields());
                    if (dependenceFields == null) {
                        logger.debug("Cannot fill in dependencies as fields are not found!");
                        logger.debug(String.format("Field %s is skipped", field.getName()));
                        return;
                    }
                    for (ObjectField field : dependenceFields) {
                        new FieldAggregator(field, instance).run();
                        
                    }
                    
                }
                //appropriate data generator is created based on field type
                Class<?> classType = field.getField().getType();
                FieldData dataGenerator;
                if (Helpers.isCollection(classType)) {
                    logger.debug("Field type is recognized as collection");
                    dataGenerator = new CollectionFieldData();
                } else if (Helpers.isMap(classType)) {
                    logger.debug("Field type is recognized as map");
                    dataGenerator = new MapFieldData();
                } else  {
                    logger.debug("Field type is recognized as unit");
                    dataGenerator = new FieldData();
                }
                
                Object value = dataGenerator.generateData();
                field.setValue(instance, value);
    
                }
            }
    
        /**
         * Data class for field that is not a container.
         */
        private class FieldData extends Data {
    
            /**
             * Generates data for field based on assigned options or field type.
             * @param classType type of data to generate value for.
             * @return generated data.
             */
            Object generateSingleData(Class<?> classType) {
                ProviderResolver dependencyResolver = new DependencyProviderResolver(field.getField(), instance);
                ProviderResolver optionsResolver = new ProviderFieldOptionsResolver(field.getField());
                ProviderResolver typeResolver = new FieldTypeResolver(classType);
                optionsResolver.setNextResolver(typeResolver);
                dependencyResolver.setNextResolver(optionsResolver);
                EntityDataProvider provider = dependencyResolver.getProvider();
                Object value;
                if (provider == null) {
                    logger.debug("Provider is not recognized by field's type." +
                            "Generating java bean as value");
                    value = generateObjectFields(classType);
                } else {
                    logger.debug("Generating value from provider");
                    int dataLength = fieldOptions.getDataSize();
                    value = provider.generate(dataLength);
                }
                return value;
            }
            
            @Override
            Object generateData() {
                return generateSingleData(field.getField().getType());
            }
            
        }
    
        /**
         * Data generator class for container class fields.
         */
        private class CollectionFieldData extends FieldData {
    
            @Override
            Object generateData() {
                Collection collection;
                Class<?> objectClass = field.getField().getType();
                try {
                    collection = (Collection) instanceManager.createInstance(objectClass);
                } catch (Error er) {
                    logger.debug("Error occurred when creating instance of a collection. Might collection" +
                            " is abstract or is an interface. Examine logs for further details." +
                            "You must declare exact implementation class as a type!");
                    return null;
                }
                int collectionSize = fieldOptions.getCollectionSize();
                Class<?> collectionGenericType = Helpers.getCollectionType(field.getField());
                for (int i=1; i <= collectionSize; i++) {
                    collection.add(generateSingleData(collectionGenericType));
                }
                return objectClass.cast(collection);
            }
        }
    
        /**
         * Data generator class for container class fields(for Map).
         */
        private class MapFieldData extends FieldData {
            
            @Override
            Object generateData() {
                Map map;
                Class<?> objectClass = field.getField().getType();
                try {
                    map = (Map) instanceManager.createInstance(objectClass);
                } catch (Error er) {
                    logger.debug("Error occurred during creation of Map instance");
                    return null;
                }
                int collectionSize = fieldOptions.getCollectionSize();
                Class<?>[] typeClasses = Helpers.getMapTypes(field.getField());
                for (int i=1;i<=collectionSize;i++) {
                    Object key = generateSingleData(typeClasses[0]);
                    Object value = generateSingleData(typeClasses[1]);
                    map.put(key, value);
                }
                return objectClass.cast(map);
            }
        }
    }
    
    
}
