package data_for_entity;

import data_for_entity.annotations.WithDataOptions;
import data_for_entity.data_providers.EntityDataProvider;
import data_for_entity.data_types.FieldDataType;
import error_reporter.ErrorReporter;
import org.apache.log4j.Logger;

/**
 * Class represents options captured from {@link WithDataOptions}
 */
public class DataOptions {
        
    private Logger logger = Logger.getLogger(DataOptions.class);
    private WithDataOptions options;
    
    
    DataOptions(WithDataOptions options) {
        this.options = options;
    }
    
    /**
     * Gets {@link EntityDataProvider} from {@link WithDataOptions} provider option.
     * @return {@link EntityDataProvider} object created from Class received from
     * provider option. If not specified then null returned.
     */
    EntityDataProvider getProvider() {
        EntityDataProvider provider = null;
        if (options!=null) {
            Class<? extends EntityDataProvider> providerClass = options.provider();
            try {
                provider = providerClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                ErrorReporter.reportError(e);
                logger.debug("Cannot create data provider from class: " + providerClass);
            }
        }
        return provider;
    }
    
    /**
     * Gets option length from {@link WithDataOptions}
     * @return set value for option length or default value.
     */
    int getDataLength() {
        int dataLength;
        if (options!=null) {
            dataLength = options.length();
            } else {
                dataLength = defaultLength("length");
        }
        if (dataLength == 0) {
            logger.debug("using default data length from Data Provider interface");
            dataLength = EntityDataProvider.dataLength;
        }
        return dataLength;
    }
    
    /**
     * Gets option collectionSize from {@link WithDataOptions}
     * @return set value for collection size option or default value.
     */
    int getCollectionSize() {
        int collectionSize;
        if (options!=null) {
            collectionSize = options.collectionSize();
        } else {
            collectionSize = defaultLength("collectionSize");
        }
        if (collectionSize == 0) {
            logger.debug("using default collection size from Data Provider interface");
            collectionSize = EntityDataProvider.collectionLength;
        }
        return collectionSize;
        
    }
    
    /**
     * Gets {@link FieldDataType} value from option dataType. Can be used as
     * alternative to provider option.
     * @return {@link FieldDataType} value or null if not set
     */
    FieldDataType getFieldDataType() {
        FieldDataType fieldDataType = null;
        if (options!=null) {
            fieldDataType = options.dataType();
        }
        return fieldDataType;
    }
    
    /**
     * internal method to get length attribute from {@link WithDataOptions}
     * @param field name of field to get length value
     * @return value of default length.
     */
    private int defaultLength(String field) {
        int defaultLength =0;
        try {
            defaultLength = Helpers.getAnnotationDefault(WithDataOptions.class, field);
        } catch (Exception e) {
            logger.debug("Error occurred getting default value from WithDataOptions annotation");
        }
        return defaultLength;
    }
    
    
}
