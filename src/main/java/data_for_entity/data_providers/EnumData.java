package data_for_entity.data_providers;

import utils.RandomGenerator;

public class EnumData implements EntityDataProvider {

    private Class<?> enumClass;


    public EnumData(Class<?> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public Object generate(int length) {
        return RandomGenerator.randomItemFromCollection(enumClass.getEnumConstants());
    }
}
