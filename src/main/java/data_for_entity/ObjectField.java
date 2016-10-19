package data_for_entity;

import error_reporter.ErrorReporter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;


/**
 * High level representation of {@link Field} in terms of Data.
 * Provides interface  a field. Is used to synchronize Future tasks.
 */
class ObjectField {
    
    private Field field;
    private Logger logger = Logger.getLogger(ObjectField.class);
    
    ObjectField(Field field) {
        this.field = field;
    }
    
    Field getField() {
        return this.field;
    }
    
    /**
     * Returns name of the field.
     * @return String representation if field's name.
     */
    String getName() {
        return field.getName();
    }
    
    /**
     * Set value for the object on this field.
     * @param object Object to set value for.
     * @param value Value that should be set.
     */
    void setValue(Object object, Object value) {
        synchronized (this) {
            try {
                BeanUtils.setProperty(object, field.getName(), value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                ErrorReporter.reportError(e);
                logger.debug(String.format("Cannot set value %s for field: %s due to error", value, field.getName()));
            }
        }
    }
    
    /**
     * Get value of this field for the passed object
     * @param object object for which data should be got.
     * @return String representation of value or null if
     * error occurred.
     */
    String getValue(Object object) {
        synchronized (this) {
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
    
}
