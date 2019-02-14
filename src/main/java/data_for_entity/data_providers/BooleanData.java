package data_for_entity.data_providers;

import java.util.Random;

public class BooleanData implements EntityDataProvider<Boolean> {

    @Override
    public Boolean generate(int length) {
        return new Random().nextBoolean();
    }
}
