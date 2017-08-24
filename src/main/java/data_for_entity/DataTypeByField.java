package data_for_entity;

import data_for_entity.data_types.FieldDataType;

import java.util.HashMap;

/**
 * Table that helps to resolve {@link FieldDataType} based on field's type.
 * {@link FieldDataType} is used further to determine {@link data_for_entity.data_providers.EntityDataProvider}
 * based on field type.
 * */
class DataTypeByField {
    
    
    private static HashMap<Class<?>, FieldDataType> dataTypeHashMap = new HashMap<>();
    
    static {
        dataTypeHashMap.put(int.class, FieldDataType.DIGIT);
        dataTypeHashMap.put(byte.class, FieldDataType.DIGIT_BYTE);
        dataTypeHashMap.put(short.class, FieldDataType.DIGIT_SHORT);
        dataTypeHashMap.put(long.class, FieldDataType.LONG);
        dataTypeHashMap.put(Long.class, FieldDataType.LONG);
        dataTypeHashMap.put(Integer.class, FieldDataType.DIGIT);
        dataTypeHashMap.put(float.class, FieldDataType.NUMERIC_DECIMAL);
        dataTypeHashMap.put(double.class, FieldDataType.NUMERIC_DECIMAL);
        dataTypeHashMap.put(boolean.class, FieldDataType.BOOLEAN);
        dataTypeHashMap.put(char.class, FieldDataType.STRING);
        dataTypeHashMap.put(String.class, FieldDataType.STRING);
    }
    
    public static FieldDataType getDataType(Class<?> typeClass) {
        if (!dataTypeHashMap.keySet().contains(typeClass)) {
            return FieldDataType.UNKNOWN;
        }
        else return dataTypeHashMap.get(typeClass);
        
    }
}

