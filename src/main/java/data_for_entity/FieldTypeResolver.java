package data_for_entity;

import data_for_entity.data_providers.EntityDataProvider;
import data_for_entity.data_types.FieldDataType;
import data_for_entity.provider_resolver.ProviderResolver;

class FieldTypeResolver extends ProviderResolver {
    
    private Class<?> fieldType;
    
    FieldTypeResolver(Class<?> fieldType) {
        this.fieldType = fieldType;
    }
    
    @Override
    protected EntityDataProvider getInternalProvider() {
        FieldDataType dataType = DataTypeByField.getDataType(fieldType);
        return DataTypeManager.getDataTypeProvider(dataType);
       
    }
}
