package data_for_entity;

import data_for_entity.annotations.DataIgnore;
import data_for_entity.annotations.DataStatic;
import data_for_entity.annotations.WithDataDependencies;
import data_for_entity.annotations.WithDataOptions;
import error_reporter.ErrorReporter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Encapsulates the way field's options are loaded into system.
 */
class RunOptionsManager {
    
    private Field field;
    
    
    RunOptionsManager(Field field) {
        this.field = field;
    }
    
    
    
    /**
     * Returns true if field is marked with annotation {@link DataIgnore}
     * @return Boolean if field is marked as DataIgnore, False otherwise.
     */
    Boolean isIgnorePresent() {
        return field.isAnnotationPresent(DataIgnore.class);
    }
    
    
    /**
     * Gets static value for field if defined.
     * @return String representation of static value or null if not defined.
     */
    DataStatic getStatic() {
        DataStatic staticAnnotation = readAnnotation(DataStatic.class);
        return staticAnnotation;
    }
    
    
    /**
     * Reads an array of fieldNames current field is depends on.
     * @return Array of field names or null if there are no dependencies.
     */
    WithDataDependencies getDependencies() {
        WithDataDependencies dataDependencies = readAnnotation(WithDataDependencies.class);
        return dataDependencies;
        
    }
    
    /**
     * Gets {@link WithDataOptions} object if present.
     * Otherwise null is returned.
     * @return {@link WithDataOptions} object or null if not present
     * or error occurred while processing.
     */
    WithDataOptions getDataOptions() {
        WithDataOptions dataOptions = readAnnotation(WithDataOptions.class);
        return dataOptions;
    }
    
    
    
    
    
    /**
     * Internal method to read annotation object from field.
     * @param annotationClass Annotation class from {@link data_for_entity.annotations}
     * @param <T> object with type annotationClass
     * @return object with type annotationClass or null if annotation is not present
     * or error occured while downcasting to type annotationClass.
     */
    private <T extends Annotation>T readAnnotation(Class<T> annotationClass) {
        boolean annotationPresent = field.isAnnotationPresent(annotationClass);
        if (!annotationPresent) {
            return null;
        }
        try {
            Annotation annotation = field.getAnnotation(annotationClass);
            return (T) annotation;
        } catch (ClassCastException e) {
            ErrorReporter.reportError(e);
            return null;
        }
    }
}
