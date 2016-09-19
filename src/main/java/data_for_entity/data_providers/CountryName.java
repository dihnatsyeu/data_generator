package data_for_entity.data_providers;

import utils.RandomGenerator;

public class CountryName implements EntityDataProvider {
    @Override
    public Object generate(int length) {
        return RandomGenerator.getCountryName(RandomGenerator.generateCountryCode());
    }
}
