package timofeyqa.rococo.utils;

import com.github.javafaker.Faker;

import java.util.Random;

public class RandomDataUtils {
    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    public static String randomUsername(){
        return faker.name().username();
    }
    public static String randomName(){
        return faker.name().fullName();
    }
    public static String randomPaintingTitle(){
        return faker.book().title();
    }

    public static String randomMuseumName() {
        return faker.company().name() + " Museum";
    }

    public static String randomCity() {
        return faker.address().city();
    }

    public static String randomDescription(){
        return randomSentence(4);
    }

    public static String randomSentence(int wordsCount){
        StringBuilder sentence = new StringBuilder();

        for (int i = 0; i < wordsCount; i++) {
            sentence.append(faker.lorem().word()).append(" ");
        }

        return sentence.toString().trim();
    }
}
