package data_for_entity;

import data_for_entity.data_providers.DependencyData;
import data_for_entity.data_providers.EntityDataProvider;
import data_for_entity.instance_managers.DefaultInstanceManager;
import data_for_entity.instance_managers.InstanceManager;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;

/**
 * Purpose of this class is to create an object with filled fields. Call method generateObject
 * with type of object to create.
 * Object fields are filled according to fields' types. All java primitive types are recognized.
 * Map interface is not supported.Nested collections are not supported (e.g. collection inside a collection etc).
 * You can mark field with {@link data_for_entity.annotations.DataIgnore} to skip field filling.
 * You can control generated value type by setting {@link EntityDataProvider}
 * via {@link data_for_entity.annotations.WithDataOptions}. In case field value depends on
 * other field's, you must specify {@link data_for_entity.annotations.WithDataDependencies}. Only dependencies
 * from the same object's field is supported, {@link EntityDataProvider} must be specified in this case.
 * By default, lib will try to create instance using default constructor. If different behavior is needed,
 * specify custom {@link InstanceManager}.
 */
public class ObjectInitializer {
    
    private TypeManager typeManager = new TypeManager();
    private Logger logger = Logger.getLogger(ObjectInitializer.class);
    private InstanceManager instanceManager = new DefaultInstanceManager();
    
    /**
     * Sets passed InstanceManager. Object is used to create instance by passed
     * types. Must be set before calling generateObject
     * @param instanceManager {@link InstanceManager} object.
     */
    public void setInstanceManager(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }
    
    /**
     * Creates an object with the passed type with filled fields according to specified
     * rules. Only mandatory fields are filled, i.e. those that are not marked
     * with {@link data_for_entity.annotations.DataIgnore}
     * @param objectType Type of object to create.
     * @return object with passed type with filled fields.
     */
    public Object generateObject(Class<?> objectType) {
        logger.info("Creating object with type: "+ objectType);
        typeManager.setObjectType(objectType);
        Object instance = instanceManager.createInstance(objectType);
        logger.debug("Searching for required fields");
        List<ObjectField> requiredFields = typeManager.createFieldsFilter().filterByRequired(typeManager.getAllFields());
        if (requiredFields == null) {
            logger.debug("There are no required fields for entity: " + instance +
            "Instance will be returned as it is");
            return instance;
        }
        generateValues(requiredFields, instance);
        logger.info("Object is created with filled fields");
        return instance;
    }
    
    /**
     * Internal method to fill fields with generated values according to specified rules.
     * @param fields List of fields that should be filled.
     * @param instance Object, which fields should be filled.
     */
    private void generateValues(List<ObjectField> fields, Object instance) {
        logger.debug("Generating values for required fields");
        for (ObjectField field: fields) {
            if (field.getValue(instance) !=null) {
                logger.debug("Field: " + field.getName() + " already has value. Skipping...");
                continue;
            }
            logger.debug("Generating value for field: " + field.getName());
            FieldsService fieldsService = new FieldsService(field);
            EntityDataProvider provider;
            String[] dependenceFieldsNames = field.getDependencies().getFields();
            //if field has dependencies, first those fields will initialized with values
            if (dependenceFieldsNames != null) {
                logger.debug("Found dependencies for field. Dependencies: " + dependenceFieldsNames);
                List<ObjectField> dependenceFields = typeManager.getFields(dependenceFieldsNames);
                if (dependenceFields == null) {
                    logger.debug("Cannot fill in dependencies as fields are not found! "+ dependenceFieldsNames);
                    logger.debug(String.format("Field %s is skipped", field.getName()));
                    continue;
                }
                generateValues(dependenceFields, instance);
                //in case field has dependencies, dataProvider must be specified for this field.
                DependencyData dependencyData = new DependencyDataManager().createDependencyData(dependenceFields, instance);
                provider = fieldsService.getProviderForDependenceData(dependencyData);
            } else {
                //finding data provider based on field type. Can be null of field type is unknown;
                provider = fieldsService.getDataProvider();
            }
            //receiving data and assigning data to instance's field
            Object data = new DataGenerator(provider, field).generateValue();
            field.setValue(instance, data);
            
        }
    }
    
    /**
     * This internal class is used to generate data based on data provider.
     */
    private class DataGenerator {
        
        private EntityDataProvider provider;
        private ObjectField field;
        
        private DataGenerator(EntityDataProvider provider, ObjectField field){
            this.provider = provider;
            this.field = field;
        }
    
        /**
         * Generates Value that will be used further to assign this value to instance's field.
         * @return value Object.
         */
        private Object generateValue() {
            logger.debug("Generating value for the field: " + field.getName());
            DataOptions dataOptions = field.getDataOptions();
            DataType dataType = field.getDataType();
            //if field is a collection, then a value will be stored in collection with this type.
            // Size of collection is got from DataOptions
            if (dataType.isCollection()) {
                logger.debug("Field type is collection. Values will be accumulated into a collection");
                Collection collection = (Collection) instanceManager.createInstance(dataType.getCollectionDataType());
                for (int i=1; i <= dataOptions.getCollectionSize(); i++) {
                    collection.add(generateSingleData());
                }
                return collection;
            }
            return generateSingleData();
            
        }
    
        /**
         * Generates single row of data based on the data provider. If
         * data provider is not defined, new object of field's type
         * is created as value.
         * @return data object.
         */
        private Object generateSingleData() {
            Object value;
            if (provider == null) {
                // recursion call to ObjectInitializer.generateObject
                value = generateObject(field.getDataType().getClassType());
                return value;
            }
            return provider.generate(field.getDataOptions().getDataLength());
        }
    }
    
    /**
     * Purpose of this class is to create a {@link DependencyData} object that will be
     * used further in {@link data_for_entity.data_providers.DependencyDataProvider} to
     * generate value of this field.
     */
    private class DependencyDataManager {
        
        private DependencyData createDependencyData(List<ObjectField> dependencyFields, Object instance) {
            DependencyData dependencyData = new DependencyData();
            for(ObjectField field: dependencyFields) {
                dependencyData.insertData(field.getName(), field.getValue(instance));
            }
            return dependencyData;
        }
    }
}
    
