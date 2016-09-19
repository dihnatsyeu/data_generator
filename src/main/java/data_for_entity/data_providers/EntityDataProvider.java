package data_for_entity.data_providers;

/**
 * Basic interface that is used to generate random data.
 */
public interface EntityDataProvider {
    
    int dataLength = 10;
    int collectionLength = 1;
    
    Object generate(int length);
}
