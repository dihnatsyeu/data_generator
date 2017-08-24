package data_for_entity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Encapsulates information that can be received from a class type:
 * set of fields. Also provides interface to a {@link FieldsFilter}
 * to get filtered data
 */
final class FieldsCollection {
    
    
    private List<ObjectField> fields;

    public void newCollection() {
        fields = new ArrayList<>();
    }
    
    /**
     * Gets all declared fields
     * @return list of {@link ObjectField} within this type.
     */
    List<ObjectField> getFields() {
        return Collections.unmodifiableList(fields);
    }
    
    /**
     * Set param object Type
     * @param objectType class of object for inspection
     */
    void collectFieldsByType(Class<?> objectType) {
        ArrayList<Field> listFields = new ArrayList<>(Arrays.asList(objectType.getDeclaredFields()));
        if (objectType.getSuperclass() != null) {
            collectFieldsByType(objectType.getSuperclass());
        }
        this.fields.addAll(fieldsToObjectFields(listFields));
    }
    
    
    /**
     * Internal method to transfer list of {@link Field} objects to list
     * of {@link ObjectField} objects.
     * @param fields list of {@link Field} objects.
     * @return list of {@link ObjectField} objects.
     */
    private List<ObjectField> fieldsToObjectFields(List<Field> fields) {
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
         
         * @return filtered {@link ObjectField} list.
         */
        List<ObjectField> filterByRequired() {
            return fields.stream().filter(field -> !new FieldOptionsManager(field.getField()).shouldBeIgnored())
                    .collect(Collectors.toList());
        }
    
        /**
         * Filter list of dependent fields against this field.
         * @param dependentFieldsNames Array of dependent fields' names.
         * @return list of {@link ObjectField} filtered by names provided in dependentFieldsNames
         */
        List<ObjectField> filterByDependentFields(String[] dependentFieldsNames) {
            return  fields.stream().filter(field ->  Arrays.asList(dependentFieldsNames).contains(field.getName()))
                    .collect(Collectors.toList());
        }

    }
}
