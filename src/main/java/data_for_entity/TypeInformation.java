package data_for_entity;

import error_reporter.ErrorReporter;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Encapsulates information that can be received from a class type:
 * set of fields. Also provides interface to a {@link FieldsFilter}
 * to get filtered data
 */
class TypeInformation {
    
    private Class<?> objectType;
    private Logger logger = Logger.getLogger(TypeInformation.class);
    
    TypeInformation() {
    }
    
    /**
     * Set param object Type
     * @param objectType class of object for inspection
     */
    void setObjectType(Class<?> objectType) {
        this.objectType = objectType;
    }
    
    /**
     * Gets all declared fields
     * @return list of {@link ObjectField} within this type.
     */
    List<ObjectField> getAllFields() {
        ArrayList<Field> listFields = new ArrayList<>(Arrays.asList(objectType.getDeclaredFields()));
        return fieldsToObjectFields(listFields);
    }
    
    /**
     * Gets list of Fields based on names
     * @param fieldNames names of fields
     * @return List of {@link ObjectField} that is received from passed names.
     */
    private List<ObjectField> getFields(String... fieldNames) {
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
    
    /**
     * Internal method to transfer list of {@link Field} objects to list
     * of {@link ObjectField} objects.
     * @param fields list of {@link Field} objects.
     * @return list of {@link ObjectField} objects.
     */
    private List<ObjectField> fieldsToObjectFields(List<Field> fields) {
        if (fields == null) {
            return null;
        }
        return fields.stream().map(ObjectField::new).collect(Collectors.toList());
    }
    
    /**
     * Creates a {@link FieldsFilter} instance
     * @return {@link FieldsFilter}
     */
    FieldsFilter createFieldsFilter() {
        return  new FieldsFilter();
    }
    
    /**
     * Helps to filter list of objects by criteria.
     */
     class FieldsFilter {
    
        /**
         * Filter list of object by required attributes. Required attribute if received from
         * {@link FieldOptionsManager}
         * @param objectFields list of fields to filter.
         * @return filtered {@link ObjectField} list.
         */
        List<ObjectField> filterByRequired(List<ObjectField> objectFields) {
            if (objectFields == null) {
                return null;
            }
            return objectFields.stream().filter(field -> !new FieldOptionsManager(field.getField()).shouldBeIgnored())
                    .collect(Collectors.toList());
        }
    
        /**
         * Filter list of dependent fields against this field.
         * @param field to search for dependent fields against.
         * @param allFields list of fields to filter.
         * @return list of {@link ObjectField}
         */
        List<ObjectField> filterByDependent(ObjectField field, List<ObjectField> allFields) {
            String[] dependencyFields = new FieldOptionsManager(field.getField()).getDependencies().getFields();
            return allFields.stream().filter(allField ->  Arrays.asList(dependencyFields).contains(allField.getName()))
                    .collect(Collectors.toList());
            
        }
        
        
    }
}
