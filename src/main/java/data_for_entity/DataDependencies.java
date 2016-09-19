package data_for_entity;

import data_for_entity.annotations.WithDataDependencies;
import data_for_entity.data_providers.DependencyDataProvider;
import error_reporter.ErrorReporter;
import org.apache.log4j.Logger;

/**
 * Represents {@link WithDataDependencies} options that are received
 * from field.
 */
class DataDependencies {
    
    private WithDataDependencies withDataDependencies;
    private Logger logger = Logger.getLogger(DataDependencies.class);
    
    
    DataDependencies(WithDataDependencies withDataDependencies) {
        this.withDataDependencies = withDataDependencies;
    }
    
    /**
     * Names of fields that are passed to {@link WithDataDependencies} in fields param.
     * @return Array of Fields' names.
     */
    String[] getFields() {
        if (withDataDependencies!=null) {
            return withDataDependencies.fields();
        }
        return null;
    }
    
    /**
     * Gets implementation of {@link DependencyDataProvider} from
     * {@link WithDataDependencies}. If not specified, default
     * {@link data_for_entity.data_providers.SequenceDataProvider is returned
     * @return {@link DependencyDataProvider } object.
     */
    DependencyDataProvider getProvider() {
        if (withDataDependencies !=null) {
            Class<? extends DependencyDataProvider> providerClass = withDataDependencies.provider();
            try {
                return providerClass.newInstance();
            } catch (InstantiationException | IllegalAccessException  e) {
                ErrorReporter.reportError(e);
                logger.debug("Data provider for dependency field cannot be created");
                return null;
            }
        }
        return null;
    }
    
    
}
