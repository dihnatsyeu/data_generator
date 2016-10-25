package data_for_entity;

import data_for_entity.data_providers.DependencyData;
import data_for_entity.data_providers.DependencyDataProvider;
import data_for_entity.data_providers.EntityDataProvider;
import data_for_entity.provider_resolver.ProviderResolver;

import java.lang.reflect.Field;
import java.util.List;

class DependencyProviderResolver extends ProviderResolver {
    
    private Object instance;
    private Field field;
    
    DependencyProviderResolver(Field field, Object instance) {
        this.field = field;
        this.instance = instance;
    }
    
    
    private DataDependencies readDataDependency() {
        FieldOptionsManager optionsManager = new FieldOptionsManager(field);
        return optionsManager.getDependencies();
    }
    
    /**
     * Helps to create {@link DependencyData} object.
     * @param dependencyFields List of fields that current field is depend on.
     * @return {@link DependencyData} instance.
     */
    private DependencyData createDependencyData(List<ObjectField> dependencyFields) {
        DependencyData dependencyData = new DependencyData();
        for(ObjectField field: dependencyFields) {
            dependencyData.insertData(field.getName(), field.getValue(instance));
        }
        return dependencyData;
    }
    
    
    @Override
    protected EntityDataProvider getInternalProvider() {
        DataDependencies dataDependencies = readDataDependency();
        if (dataDependencies == null) {
            return null;
        }
        FieldsCollection fieldsCollection = new FieldsCollection();
        fieldsCollection.collectFieldsByType(instance.getClass());
        List<ObjectField> dependencyFields =
                fieldsCollection.createFieldsFilter().filterByDependentFields(dataDependencies.getFields());
        DependencyDataProvider provider = dataDependencies.getProvider();
        provider.setDependencyData(createDependencyData(dependencyFields));
        return provider;
        
    }
}
