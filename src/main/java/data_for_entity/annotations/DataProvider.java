package data_for_entity.annotations;

import data_for_entity.data_providers.EntityDataProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DataProvider {
    Class<? extends EntityDataProvider> value();
}
