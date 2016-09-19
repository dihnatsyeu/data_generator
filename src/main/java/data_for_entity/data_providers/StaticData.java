package data_for_entity.data_providers;

public class StaticData implements EntityDataProvider {
    
    private String value;
    
    public StaticData(String value) {
        this.value = value;
    }
    
    @Override
    public Object generate(int length) {
        return value;
    }
}
