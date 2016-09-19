package data_for_entity.annotations;


import data_for_entity.data_providers.DependencyDataProvider;
import data_for_entity.data_providers.SequenceDataProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface WithDataDependencies {
    String[]  fields();
    Class<? extends DependencyDataProvider> provider() default SequenceDataProvider.class;
}
