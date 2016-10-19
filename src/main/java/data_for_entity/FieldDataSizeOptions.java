package data_for_entity;

public class FieldDataSizeOptions {
    
    private static int minLength=5;
    private static int maxLength=10;
    private static int minCollectionLength=1;
    private static int maxCollectionLength=5;
    
    
    public static int getMinLength() {
        return minLength;
    }
    
    public static void setMinLength(int min) {
        minLength = min;
    }
    
    public static int getMaxLength() {
        return maxLength;
    }
    
    public static void setMaxLength(int max) {
        maxLength = max;
    }
    
    public static int getMinCollectionLength() {
        return minCollectionLength;
    }
    
    public static void setMinCollectionLength(int minCollection) {
        minCollectionLength = minCollection;
    }
    
    public static int getMaxCollectionLength() {
        return maxCollectionLength;
    }
    
    public static void setMaxCollectionLength(int maxCollection) {
        maxCollectionLength = maxCollection;
    }
    
}
