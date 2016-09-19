package data_for_entity.annotations;

import data_for_entity.data_providers.EntityDataProvider;
import data_for_entity.data_types.FieldDataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface WithDataOptions {
    Class<? extends EntityDataProvider>  provider();
    int length() default 10;
    FieldDataType dataType();
    int collectionSize() default 1;
}
