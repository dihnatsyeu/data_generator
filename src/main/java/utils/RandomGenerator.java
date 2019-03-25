package utils;


import java.util.*;

public class RandomGenerator {

    public static int generateRandomInteger(int minNumber, int maxNumber) {
        Random random = new Random();
        return random.nextInt((maxNumber - minNumber) + 1) + minNumber;
    }

    public static <T> T randomItemFromCollection(T[] collection) {
        int index = generateRandomInteger(0, collection.length - 1);
        return collection[index];
    }

}


