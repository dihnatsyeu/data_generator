package data_for_entity;

import data_for_entity.data_providers.EntityDataProvider;
import data_for_entity.instance_managers.DefaultInstanceManager;
import data_for_entity.instance_managers.InstanceManager;
import data_for_entity.provider_resolver.ProviderResolver;
import org.apache.log4j.Logger;

import java.util.*;


/**
 * Class encapsulates algorithm of parsing and populating object's fields with values
 */
class ObjectAggregator {
    
    private Logger logger = Logger.getLogger(ObjectAggregator.class);

    private InstanceManager instanceManager = new DefaultInstanceManager();
    private static List<Object> emptyValues = new ArrayList<>();
    static {
        emptyValues.add(0);
        emptyValues.add(0L);
        emptyValues.add(null);
    }
    
    
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
        FieldsCollection fieldsCollection = new FieldsCollection();
        fieldsCollection.collectFieldsByType(objectType);
        List<ObjectField> requiredFields = fieldsCollection.createFieldsFilter().filterByRequired();
        Object instance = instanceManager.createInstance(objectType);
        if ((requiredFields == null) || requiredFields.size() == 0) {
            return instance;
        }
        TasksExecutor executor = new TasksExecutor();
        for (ObjectField objectField:requiredFields) {
            FieldAggregator fieldAggregator = new FieldAggregator(objectField, instance, fieldsCollection);
            executor.submitTask(fieldAggregator);
            
        }
        boolean tasksCompleted = executor.waitForCompletion();

        if (!tasksCompleted) {
            logger.warn("Not all fields are generated! View debug logs for details");
        }
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
        private FieldsCollection fieldsCollection;
        
        FieldAggregator(ObjectField objectField, Object instance, FieldsCollection fieldsCollection) {
            this.fieldsCollection = fieldsCollection;
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
                if (!emptyValues.contains(field.getValue(instance))) {
                    logger.debug("Field: " + field.getName() + " already has value. Skipping...");
                    return;
                }
                //if field has dependencies, first those fields will initialized with values
                if (fieldOptions.hasDependencies()) {
                    List<ObjectField> dependenceFields = fieldsCollection.createFieldsFilter().
                            filterByDependentFields(fieldOptions.getDependencies().getFields());
                    if (dependenceFields == null) {
                        logger.debug("Cannot fill in dependencies as fields are not found for field" + field.getName());
                        logger.debug(String.format("Field %s is skipped", field.getName()));
                        return;
                    }
                    for (ObjectField field : dependenceFields) {
                        new FieldAggregator(field, instance, fieldsCollection).run();
                        
                    }
                    
                }
                //appropriate data generator is created based on field type
                Class<?> classType = field.getField().getType();
                FieldData dataGenerator;
                if (Helpers.isCollection(classType)) {
                    dataGenerator = new CollectionFieldData();
                } else if (Helpers.isMap(classType)) {
                    dataGenerator = new MapFieldData();
                } else  {
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
                    logger.debug("Provider is not recognized for field:" +field.getName()+ " by field's type." +
                            "Generating java bean as value");
                    value = generateObjectFields(classType);
                } else {
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
        @SuppressWarnings("unchecked")
        private class CollectionFieldData extends FieldData {
    
            @Override
            Object generateData() {
                Collection collection;
                Class<?> objectClass = field.getField().getType();
                try {
                    collection = (Collection) instanceManager.createInstance(objectClass);
                } catch (Error er) {
                    logger.error("Error occurred when creating instance of a collection. Might collection" +
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
        @SuppressWarnings("unchecked")
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
