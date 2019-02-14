package data_for_entity.data_providers;
import org.apache.commons.lang3.RandomStringUtils;


public class AlphaNumeric implements EntityDataProvider<String> {

    @Override
    public String generate(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
