package data_for_entity;

import data_for_entity.annotations.DataIgnore;
import data_for_entity.annotations.WithDataDependencies;
import data_for_entity.data_providers.DependencyDataProvider;
import data_for_entity.data_providers.EntityDataProvider;
import data_for_entity.data_types.FieldDataType;
import data_for_entity.instance_managers.DefaultInstanceManager;
import org.apache.log4j.Logger;
import utils.RandomGenerator;

import java.lang.reflect.Field;

/**
 * Encapsulates working with options that are assigned to a bean's fields via
 * annotations. Annotation itself it parsed and translated via {@link AnnotationReader} object.
 */
class FieldOptionsManager {
    
    private AnnotationReader annotationReader;
    private Logger logger = Logger.getLogger(FieldOptionsManager.class);
    
    
    FieldOptionsManager(Field field) {
        this.annotationReader = new AnnotationReader(field);
    }
    
    
    /**
     * Returns true if field is marked with annotation {@link DataIgnore}
     * @return Boolean if field is marked as DataIgnore, False otherwise.
     */
    Boolean shouldBeIgnored() {
        return annotationReader.isIgnorePresent();
    }
    
    
    /**
     * Parses assigned options to a field and tries to create {@link EntityDataProvider} object
     * based on the received values.
     * @return {@link EntityDataProvider} object or null if neither {@link EntityDataProvider} nor
     * {@link FieldDataType} is not assigned to a field via options.
     */
    EntityDataProvider getDataProvider() {
        logger.debug("Reading data provider from fields options");
        EntityDataProvider provider;
        Class<? extends EntityDataProvider> dataProviderClass = annotationReader.getDataProvider();
        logger.debug("Entity data provider: " + dataProviderClass);
        FieldDataType fieldDataType = annotationReader.getFieldDataType();
        logger.debug("Field data type: " + fieldDataType);
        if (dataProviderClass != null) {
            logger.debug("Creating provider based on provider's class");
            provider = new DefaultInstanceManager().createInstance(dataProviderClass);
        } else if (fieldDataType != null) {
            logger.debug("Creating provider based on field's type");
            provider = DataTypeManager.getDataTypeProvider(fieldDataType);
        } else {
            logger.debug("Provider cannot be set");
            provider = null;
        }
        return provider;
    }
    
    
    /**
     * If field has assigned dependencies.
     * @return True if field has dependency, false otherwise.
     */
    boolean hasDependencies() {
        return annotationReader.getDependencyFields() != null;
    }
    
    /**
     * Reads an array of fieldNames current field is depends on.
     * @return {@link DataDependencies} instance that encapsulates {@link WithDataDependencies}
     */
    DataDependencies getDependencies() {
        DataDependencies dataDependencies = null;
        String[] dependencyFields = annotationReader.getDependencyFields();
        if (dependencyFields != null) {
            dataDependencies = new DataDependencies();
            dataDependencies.setFields(dependencyFields);
            Class<? extends DependencyDataProvider> providerClass = annotationReader.getDependencyProvider();
            DependencyDataProvider provider = new DefaultInstanceManager().createInstance(providerClass);
            dataDependencies.setProvider(provider);
            
        }
        return dataDependencies;
        
    }
    
    /**
     * Reads assigned data length from field's options. If not defined,
     * generates random length between min and max values from {@link FieldDataSizeOptions}
     * @return Integer value of data size
     */
    Integer getDataSize() {
        Integer dataSizeFromAnnotation = annotationReader.getDataSize();
        Integer dataSize;
        if (dataSizeFromAnnotation == null) {
            dataSize = RandomGenerator.generateRandomInteger(
                    FieldDataSizeOptions.getMinLength(), FieldDataSizeOptions.getMaxLength());
        } else {
            dataSize = dataSizeFromAnnotation;
        }
        return dataSize;
    }
    
    /**
     * Reads assigned collection's length from field's options. If not defined,
     * generates random length between min and max values from {@link FieldDataSizeOptions}
     * @return Integer value of collection size.
     */
    Integer getCollectionSize() {
        Integer collectionSizeFromAnnotation =  annotationReader.getCollectionSize();
        Integer collectionSize;
        if (collectionSizeFromAnnotation == null) {
            collectionSize = RandomGenerator.generateRandomInteger(
                    FieldDataSizeOptions.getMinCollectionLength(),
                    FieldDataSizeOptions.getMaxCollectionLength());
            
        } else {
            collectionSize = collectionSizeFromAnnotation;
        }
        return collectionSize;
    }
    
    
    
}
