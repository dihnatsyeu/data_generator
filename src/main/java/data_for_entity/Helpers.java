package data_for_entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

/**
 * Util class to do background operation for {@link Field}
 */
public class Helpers {
    
    static boolean isCollection(Class<?> userClass) {
        return Collection.class.isAssignableFrom(userClass) ||
                Array.class.isAssignableFrom(userClass);
    }
    
    @SuppressWarnings("unchecked")
    static<T> T getAnnotationDefault(Class<? extends Annotation> annotationClass, String element) throws Exception {
        Method method = annotationClass.getMethod(element,(Class[])null);
        return((T)method.getDefaultValue());
    }
    
    static Class<?> getCollectionType(Field field) {
        Class<?> innerClass;
        Class<?> collectionClass = field.getType();
        if (Array.class.isAssignableFrom(collectionClass)) {
            innerClass = collectionClass.getComponentType();
        } else  if (Collection.class.isAssignableFrom(collectionClass)) {
            ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
            innerClass = (Class<?>) stringListType.getActualTypeArguments()[0];
        } else {
            innerClass = null;
        }
        return innerClass;
    }
    
    
}
