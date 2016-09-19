package utils;


import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;

import java.util.*;

public class RandomGenerator {

    private static Logger log = Logger.getLogger(RandomGenerator.class);

    public static LinkedHashSet<String> generatePhones(int maxPhones) {

         class RandomPhone {
            private String num, num1, num2, num3; //3-4 numbers in area code
            private String set1, set2, set3; //sequence 2 and 3 of the phone number

            private Random generator = new Random();

            public String generate() {
                num = generator.nextInt(1) == 1 ? "1" : "";
                num1 = Integer.toString(generator.nextInt(7) + 1);
                num2 = Integer.toString(generator.nextInt(8));
                num3 = Integer.toString(generator.nextInt(8));

                set1 = Integer.toString(generator.nextInt(9999)); //city code

                set2 = Integer.toString(generator.nextInt(643) + 100);
                set3 = Integer.toString(generator.nextInt(8999) + 1000);

                return num+num1+num2+num3+set1+set2+set3;
            }
        }
        LinkedHashSet<String> phoneNumbers = new LinkedHashSet<>();
        int phones = RandomUtils.nextInt(1, maxPhones);
        int i = 0;
        do {
            phoneNumbers.add(new RandomPhone().generate());
            i++;
        } while (i < phones);

        return phoneNumbers;
    }

    public static LinkedHashSet<String> generateLanguagesCodes(int maxLanguages) {
        LinkedHashSet<String> languages = new LinkedHashSet<>();
        int numLanguages = RandomUtils.nextInt(1, maxLanguages);
        List<String> countryCodes = Arrays.asList(Locale.getISOCountries());
        int i = 0;
        do {
            languages.add(countryCodes.get(RandomUtils.nextInt(1, countryCodes.size())));
            i++;
        } while (i < numLanguages);
        return languages;
    }
    
    public static String generateCountryCode() {
        List<String> countryCodes = Arrays.asList(Locale.getISOCountries());
        return countryCodes.get(RandomUtils.nextInt(1,countryCodes.size()));
    }

    public static String getCountryName(String countryCode) {
        Locale locale = new Locale("", countryCode);
        return locale.getDisplayCountry();
    }
    
    public static String getCountryCode(String countryName) {
        Map<String, String> countries = new HashMap<>();
        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            countries.put(l.getDisplayCountry(), iso);
        }
        try {
            return countries.get(countryName);
        } catch (NullPointerException e) {
            throw new AssertionError("There is no country with name " + countryName);
        }
    }

    

    
    }


