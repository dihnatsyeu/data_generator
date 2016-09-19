package data_for_entity;

import data_for_entity.data_providers.DependencyData;
import data_for_entity.data_providers.DependencyDataProvider;
import data_for_entity.data_providers.EntityDataProvider;
import data_for_entity.data_providers.StaticData;
import data_for_entity.data_types.FieldDataType;
import error_reporter.ErrorReporter;
import org.apache.log4j.Logger;


/**
 * Encapsulates high level operations on {@link ObjectField}. Gets information
 * from specified options via {@link data_for_entity.annotations}
 */
class FieldsService {
    
    private Logger logger = Logger.getLogger(FieldsService.class);
    private ObjectField field;
    
    FieldsService(ObjectField field) {
        this.field = field;
    }
    
    /**
     * Creates {@link EntityDataProvider} based on provider option received from
     * {@link DataDependencies} and passed {@link DependencyData}
     * @param dependencyData data that encapsulates field names and appropriate values.
     * Is used by {@link DependencyDataProvider} to generate value for the field based on
     * it's dependencies.
     * @return {@link EntityDataProvider} object.
     */
    EntityDataProvider getProviderForDependenceData(DependencyData dependencyData) {
        DataDependencies dataDependencies = field.getDependencies();
        DependencyDataProvider provider =  dataDependencies.getProvider();
        if (provider == null) {
            ErrorReporter.raiseError("Field must have DataProvider specified!");
        }
        provider.setDependencyData(dependencyData);
        return provider;
            
       
    }
    
    /**
     * Creates {@link EntityDataProvider} based on data option specified via
     * {@link data_for_entity.annotations}
     * @return {@link EntityDataProvider} for a field based on table {@link DataTypesProviders}
     */
    EntityDataProvider getDataProvider() {
        String dataStatic = field.getStaticValue();
        EntityDataProvider provider;
        //if field has set static data, data provider is this value.
        if (dataStatic != null) {
            return new StaticData(dataStatic);
        }
        DataOptions dataOptions = field.getDataOptions();
        provider = dataOptions.getProvider();
        if (provider == null) {
            provider = DataTypesProviders.getDataTypeProvider(getFieldType());
        }
        return provider;
    }
    
    /**
     * Gets type of the field by class type.
     * @return {@link FieldDataType}
     */
    private FieldDataType getFieldType() {
        DataOptions dataOptions = field.getDataOptions();
        FieldDataType fieldType;
        fieldType = dataOptions.getFieldDataType();
        if (fieldType == null) {
            Class<?> classType = field.getDataType().getClassType();
            fieldType = DataTypeByField.getDataType(classType);
        }
        return fieldType;
    
    }
    
}