package data_for_entity.data_providers;

import java.util.Collection;
import java.util.HashMap;

/**
 * Object is used in {@link DependencyDataProvider} to manipulate
 * with data
 */
public class DependencyData {
    
    private HashMap<String, String> fieldValues = new HashMap<>();
    
    public Collection<String> getValues() {
        return fieldValues.values();
    }
    
    public void insertData(String name, String value) {
        fieldValues.put(name, value);
    }
}
