package data_for_entity.data_providers;

import org.apache.commons.lang3.RandomStringUtils;

public class Numeric implements EntityDataProvider {
    
    @Override
    public Object generate(int length) {
        return RandomStringUtils.randomNumeric(length);
    }
}
