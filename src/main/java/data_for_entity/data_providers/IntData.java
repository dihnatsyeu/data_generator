package data_for_entity.data_providers;


import org.apache.commons.lang3.RandomUtils;

public class IntData implements EntityDataProvider {
    
    @Override
    public Object generate(int length) {
        return RandomUtils.nextInt(1, length);
    }
}
