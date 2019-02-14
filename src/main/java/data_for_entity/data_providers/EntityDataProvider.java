package data_for_entity.data_providers;

/**
 * Basic interface that is used to generate random data.
 */
public interface EntityDataProvider<T> {
    
    T generate(int length);
}
