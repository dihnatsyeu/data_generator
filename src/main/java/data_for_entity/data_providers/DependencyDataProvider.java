package data_for_entity.data_providers;


/**
 * Abstract class is used to generate data that depends on other fields' values.
 */
public abstract class DependencyDataProvider<T> implements EntityDataProvider<T> {
    
    public void setDependencyData(DependencyData dependencyData) {
        this.dependencyData = dependencyData;
    }
    
    private DependencyData dependencyData;
    
    public DependencyData getDependencyData() {
        return dependencyData;
    }
    
    @Override
    public abstract T generate(int length);
}
