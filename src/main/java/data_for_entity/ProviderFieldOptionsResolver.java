package data_for_entity;

import data_for_entity.data_providers.EntityDataProvider;
import data_for_entity.provider_resolver.ProviderResolver;

import java.lang.reflect.Field;

class ProviderFieldOptionsResolver extends ProviderResolver {
    
    private Field field;
    
    ProviderFieldOptionsResolver(Field field) {
       this.field = field;
    }
    
    @Override
    protected EntityDataProvider getInternalProvider() {
        FieldOptionsManager fieldOptions = new FieldOptionsManager(field);
        return fieldOptions.getDataProvider();
    }
}
