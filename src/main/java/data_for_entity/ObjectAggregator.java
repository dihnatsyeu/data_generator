package data_for_entity;

import data_for_entity.data_providers.DependencyData;
import data_for_entity.data_providers.DependencyDataProvider;
import data_for_entity.data_providers.EntityDataProvider;
import data_for_entity.data_types.FieldDataType;
import data_for_entity.instance_managers.DefaultInstanceManager;
import data_for_entity.instance_managers.InstanceManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Class encapsulates algorithm of parsing and populating object's fields with values
 */
class ObjectAggregator {
    
    private Logger logger = Logger.getLogger(ObjectAggregator.class);
    private TypeInformation typeInformation = new TypeInformation();
    private InstanceManager instanceManager = new DefaultInstanceManager();
    private List<ObjectField> fieldSet = new ArrayList<>();
    
    public List<ObjectField> getFieldSet() {
        return fieldSet;
    }
    
    public void setFieldSet(List<ObjectField> fieldSet) {
        this.fieldSet = fieldSet;
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
        typeInformation.setObjectType(objectType);
        Object instance = instanceManager.createInstance(objectType);
        logger.debug("Searching for required fields");
        List<ObjectField> requiredFields = typeInformation.createFieldsFilter().filterByRequired(typeInformation.getAllFields());
        if ((requiredFields == null) || requiredFields.size() == 0) {
            logger.debug("There are no required fields for entity: " + instance +
                    "Instance will be returned as it is");
            return instance;
        }
        setFieldSet(requiredFields);
        TasksExecutor executor = new TasksExecutor();
        for (ObjectField objectField:getFieldSet()) {
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
         * Helps to create {@link DependencyData} object.
         * @param dependencyFields List of fields that current field is depend on.
         * @param instance object for that values should be captured with dependencyFields
         * @return {@link DependencyData} instance.
         */
        private  DependencyData createDependencyData(List<ObjectField> dependencyFields, Object instance) {
            DependencyData dependencyData = new DependencyData();
            for(ObjectField field: dependencyFields) {
                dependencyData.insertData(field.getName(), field.getValue(instance));
            }
            return dependencyData;
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
                if (fieldOptions.getDependencies()!=null) {
                    logger.debug("Found dependencies flag for field.");
                    List<ObjectField> dependenceFields = typeInformation.createFieldsFilter()
                            .filterByDependent(field, getFieldSet());
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
                EntityDataProvider resultProvider;
                // if field has assigned dependencies, data provider will be read from
                // dependencies options
                if (fieldOptions.getDependencies()!=null) {
                    logger.debug("Generating dependency data and provider");
                    DependencyDataProvider dependencyProvider = fieldOptions.getDependencies().getProvider();
                    List<ObjectField> dependencyFields = typeInformation.createFieldsFilter()
                            .filterByDependent(field, getFieldSet());
                    dependencyProvider.setDependencyData(createDependencyData(dependencyFields, instance));
                    logger.debug("Dependency provider is successfully set");
                    resultProvider = dependencyProvider;
                    //otherwise trying to read data provider from the other available
                    //field options
                } else {
                   logger.debug("Trying to receive provider from field options");
                   resultProvider = fieldOptions.getDataProvider();
                }
                //if provider cannot be get from field options, trying to get it
                //based on field's type
                if (resultProvider == null) {
                    logger.debug("Provider from field's option is not received.");
                    logger.debug("Getting provider from field type");
                    FieldDataType dataType = DataTypeByField.getDataType(classType);
                    resultProvider = DataTypeManager.getDataTypeProvider(dataType);
                }
                Object value;
                //if field type is unknown, i.e. resultProvider==null,
                //getting value by generating another bean
                if (resultProvider == null) {
                    logger.debug("Provider is not recognized by field's type." +
                            "Generating java bean as value");
                    value = generateObjectFields(classType);
                    //otherwise generating value from Data provider
                } else {
                    logger.debug("Generating value from provider");
                    int dataLength = fieldOptions.getDataSize();
                    value = resultProvider.generate(dataLength);
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
