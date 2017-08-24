package data_for_entity.data_providers;


import org.apache.commons.lang3.RandomUtils;

public class IntData implements EntityDataProvider {

    private boolean isShort;
    private boolean isByte;

    public IntData(boolean isByte, boolean isShort) {
        this.isByte = isByte;
        this.isShort = isShort;
    }
    
    @Override
    public Object generate(int length) {
        int maxLength;
        if (isShort) {
            maxLength =4;
        }
        else if (isByte) {
            maxLength = 2;
        }
        else {
            maxLength = length;
        }

        return RandomUtils.nextInt(1, maxLength);
    }
}
