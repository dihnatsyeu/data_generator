package data_for_entity;

import java.lang.reflect.Field;


/**
 * Encapsulates information relates to type of object's field
 */
class DataType {
    private Field field;
    
   DataType(Field field) {
       this.field = field;
   }
   
   
   Boolean isCollection() {
       return Helpers.isCollection(field.getType());
   }
   
   Class<?> getClassType() {
       return field.getType();
   }
   
   Class<?> getCollectionDataType() {
       if (!isCollection()) {
           return null;
       }
       return Helpers.getCollectionType(field);
   }
    
    
}
