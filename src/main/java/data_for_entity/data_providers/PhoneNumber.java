package data_for_entity.data_providers;

import utils.RandomGenerator;

public class PhoneNumber implements EntityDataProvider {
    @Override
    public Object generate(int length) {
        return RandomGenerator.generatePhones(length);
    }
}
