package data_for_entity.data_providers;


/**
 * Abstract class is used to generate data that depends on other fields' values.
 */
public abstract class DependencyDataProvider implements EntityDataProvider {
    
    public void setDependencyData(DependencyData dependencyData) {
        this.dependencyData = dependencyData;
    }
    
    private DependencyData dependencyData;
    
    public DependencyData getDependencyData() {
        return dependencyData;
    }
    
    @Override
    public abstract Object generate(int length);
}
