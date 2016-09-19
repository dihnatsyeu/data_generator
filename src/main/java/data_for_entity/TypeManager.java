package data_for_entity;

import error_reporter.ErrorReporter;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class TypeManager {
    
    private Class<?> objectType;
    private Logger logger = Logger.getLogger(TypeManager.class);
    
    TypeManager() {
    }
    
    void setObjectType(Class<?> objectType) {
        this.objectType = objectType;
    }
    
    List<ObjectField> getAllFields() {
        ArrayList<Field> listFields = new ArrayList<>(Arrays.asList(objectType.getDeclaredFields()));
        return fieldsToObjectFields(listFields);
    }
    
    List<ObjectField> getFields(String... fieldNames) {
        List<Field> foundFields = new ArrayList<>();
        for (String fieldname: fieldNames) {
            try {
                foundFields.add(objectType.getDeclaredField(fieldname));
            } catch (NoSuchFieldException e) {
                ErrorReporter.reportError(e);
                logger.debug("There is no field with name " + fieldname);
            }
        }
        if (foundFields.size() == 0){
            logger.debug("No fields were found by passed names: " + fieldNames);
            return null;
        }
        return fieldsToObjectFields(foundFields);
    }
    
    private List<ObjectField> fieldsToObjectFields(List<Field> fields) {
        if (fields == null) {
            return null;
        }
        return fields.stream().map(ObjectField::new).collect(Collectors.toList());
    }
    
    
    
    FieldsFilter createFieldsFilter() {
        return  new FieldsFilter();
    }
    
    
    class FieldsFilter {
        
        List<ObjectField> filterByRequired(List<ObjectField> objectFields) {
            if (objectFields == null) {
                return null;
            }
            return objectFields.stream().filter(field -> !field.shouldBeIgnored()).collect(Collectors.toList());
            
        }
        
        
    }
}
