package data_for_entity.data_providers;

import java.util.Date;

public class DateData implements EntityDataProvider {
    
    @Override
    public Object generate(int length) {
        return new Date();
    }
}
