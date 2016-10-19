package data_for_entity;

import data_for_entity.data_providers.*;
import data_for_entity.data_types.FieldDataType;

import java.util.HashMap;

/**
 * Should be user to get {@link EntityDataProvider} based on {@link FieldDataType}
 */
public class DataTypeManager {
    
    static EntityDataProvider getDataTypeProvider(FieldDataType fieldDataType) {
        if (!DataTypesProviders.providerHashMap.containsKey(fieldDataType)) {
            return null;
        }
        return DataTypesProviders.providerHashMap.get(fieldDataType);
        
    }
    
    public static void putToProviders(FieldDataType dataType, EntityDataProvider provider) {
        
        DataTypesProviders.providerHashMap.put(dataType, provider);
    }
    
    
    private static class DataTypesProviders {
        
        private static HashMap<FieldDataType, EntityDataProvider> providerHashMap = new HashMap<>();
        
        static {
            providerHashMap.put(FieldDataType.DIGIT, new IntData());
            providerHashMap.put(FieldDataType.ALPHANUMERIC, new AlphaNumeric());
            providerHashMap.put(FieldDataType.BOOLEAN, new BooleanData());
            providerHashMap.put(FieldDataType.COLLECTION, null);
            providerHashMap.put(FieldDataType.DATE, new DateData());
            providerHashMap.put(FieldDataType.NUMERIC, new Numeric());
            providerHashMap.put(FieldDataType.STRING, new StringData());
            providerHashMap.put(FieldDataType.UNKNOWN, null);
            providerHashMap.put(FieldDataType.LONG, new LongData());
        }
        
    }
    
}