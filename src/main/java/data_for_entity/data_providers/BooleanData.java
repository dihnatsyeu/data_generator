package data_for_entity.data_providers;


import java.util.Random;

public class BooleanData implements EntityDataProvider {
    
    @Override
    public Object generate(int length) {
        return new Random().nextBoolean();
    }
}
