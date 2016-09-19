package data_for_entity;

import data_for_entity.annotations.DataIgnore;
import data_for_entity.annotations.DataStatic;
import data_for_entity.annotations.WithDataDependencies;
import data_for_entity.annotations.WithDataOptions;
import error_reporter.ErrorReporter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;


/**
 * High level representation of {@link Field} in terms of Data.
 * Provides interface to read run options applied to a field and
 * other properties
 */
class ObjectField {
    
    private Field field;
    private RunOptionsManager runOptionsManager;
    private Logger logger = Logger.getLogger(ObjectField.class);
    private DataType dataType;
    
    ObjectField(Field field) {
        this.field = field;
        this.runOptionsManager = new RunOptionsManager(field);
        this.dataType = new DataType(field);
    }
    
    DataType getDataType() {
        return this.dataType;
    }
    
    /**
     * Returns name of the field.
     * @return String representation if field's name.
     */
    String getName() {
        return field.getName();
    }
    
    
    /**
     * Returns true if field is marked with annotation {@link DataIgnore}
     * @return Boolean if field is marked as DataIgnore, False otherwise.
     */
    Boolean shouldBeIgnored() {
        return runOptionsManager.isIgnorePresent();
    }
    
    /**
     * Gets static value for field if defined.
     * @return String representation of static value or null if not defined.
     */
    String getStaticValue() {
        DataStatic staticMarker = runOptionsManager.getStatic();
        if (staticMarker!=null) {
            return staticMarker.value();
        }
        return null;
    }
    
    /**
     * Reads an array of fieldNames current field is depends on.
     * @return {@link DataDependencies} instance that encapsulates {@link WithDataDependencies}
     */
    DataDependencies getDependencies() {
        WithDataDependencies dataDependencies = runOptionsManager.getDependencies();
        return new DataDependencies(dataDependencies);
        
    }
    
    /**
     * Gets {@link WithDataOptions} object if present and created {@link DataOptions}.
     * Otherwise null is returned.
     * @return {@link DataOptions} object
     */
    DataOptions getDataOptions() {
        WithDataOptions dataOptions = runOptionsManager.getDataOptions();
        return new DataOptions(dataOptions);
    }
    
    
    /**
     * Set value for the object on this field.
     * @param object Object to set value for.
     * @param value Value that should be set.
     */
    void setValue(Object object, Object value) {
        try {
            BeanUtils.setProperty(object, field.getName(), value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            ErrorReporter.reportError(e);
            logger.debug(String.format("Cannot set value %s for field: %s due to error", value, field.getName()));
        }
    }
    
    
    
    /**
     * Get value of this field for the passed object
     * @param object object for which data should be got.
     * @return String represntation of value or null if
     * error occurred.
     */
    String getValue(Object object) {
        try {
            return BeanUtils.getProperty(object, field.getName());
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            ErrorReporter.reportError(e);
            logger.debug(String.format("Cannot get value of field with name: %s for object: %s",
                    field.getName(), object));
            return null;
        }
    }
    
}
