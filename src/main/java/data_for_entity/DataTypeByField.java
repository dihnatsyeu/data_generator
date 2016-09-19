package data_for_entity;

import data_for_entity.data_types.FieldDataType;

import java.util.HashMap;

/**
 * Table that helps to resolve {@link FieldDataType} based on field's type.
 * {@link FieldDataType} is used further to determine {@link data_for_entity.data_providers.EntityDataProvider}
 * based on field type.
 * */
public class DataTypeByField {
    
    
    private static HashMap<Class<?>, FieldDataType> dataTypeHashMap = new HashMap<>();
    
    static {
        dataTypeHashMap.put(int.class, FieldDataType.NUMERIC);
        dataTypeHashMap.put(byte.class, FieldDataType.NUMERIC);
        dataTypeHashMap.put(short.class, FieldDataType.NUMERIC);
        dataTypeHashMap.put(long.class, FieldDataType.NUMERIC);
        dataTypeHashMap.put(Integer.class, FieldDataType.NUMERIC);
        dataTypeHashMap.put(float.class, FieldDataType.NUMERIC);
        dataTypeHashMap.put(double.class, FieldDataType.NUMERIC);
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
