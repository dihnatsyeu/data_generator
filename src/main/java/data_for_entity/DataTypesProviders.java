package data_for_entity;

import data_for_entity.data_providers.*;
import data_for_entity.data_types.FieldDataType;

import java.util.HashMap;

class DataTypesProviders {
    
    private static HashMap<FieldDataType, EntityDataProvider> providerHashMap = new HashMap<>();
    
    static {
        providerHashMap.put(FieldDataType.DIGIT,new IntData());
        providerHashMap.put(FieldDataType.ALPHANUMERIC, new AlphaNumeric());
        providerHashMap.put(FieldDataType.BOOLEAN, new BooleanData());
        providerHashMap.put(FieldDataType.COLLECTION, null);
        providerHashMap.put(FieldDataType.DATE, new DateData());
        providerHashMap.put(FieldDataType.NUMERIC, new Numeric());
        providerHashMap.put(FieldDataType.STRING, new StringData());
        providerHashMap.put(FieldDataType.UNKNOWN, null);
    }
    
    static EntityDataProvider getDataTypeProvider(FieldDataType fieldDataType) {
            return providerHashMap.get(fieldDataType);
        
    }
}
