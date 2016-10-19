package data_for_entity.data_providers;

import org.apache.commons.lang3.RandomStringUtils;

public class StringData implements EntityDataProvider {
    
    @Override
    public Object generate(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }
}
