package data_for_entity.data_providers;


import org.apache.commons.lang3.RandomUtils;

public class LongData implements EntityDataProvider {
    @Override
    public Object generate(int length) {
        return RandomUtils.nextLong(1, length);
    }
}
