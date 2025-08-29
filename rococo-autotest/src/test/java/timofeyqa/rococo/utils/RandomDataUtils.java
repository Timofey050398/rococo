package timofeyqa.rococo.utils;

import com.github.javafaker.Faker;
import net.javacrumbs.jsonunit.core.util.ResourceUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static timofeyqa.rococo.utils.PhotoConverter.loadImageAsBytes;

public class RandomDataUtils {
    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    public static String randomUsername(){
        return faker.name().username();
    }

    public static String randomFirstname(){
        return faker.name().firstName();
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

    public static String fakeJwt() {
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = String.format("{\"sub\":\"%s\",\"iat\":%d}",
            UUID.randomUUID(), System.currentTimeMillis() / 1000);

        String header = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(headerJson.getBytes());
        String payload = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(payloadJson.getBytes());

        String signature = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(UUID.randomUUID().toString().getBytes());

        return header + "." + payload + "." + signature;
    }

    public static String randomWord(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    public static byte[] randomImage(String folderName){
        return loadImageAsBytes(randomFilePath(folderName));
    }

    public static String randomFilePath(String folderName) {
        folderName = "img/content/" + folderName;
        URL resource = ResourceUtils.class.getClassLoader().getResource(folderName);
        if (resource == null) {
            throw new IllegalArgumentException("Folder not found: " + folderName);
        }

        File folder;
        try {
            folder = new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        if (!folder.isDirectory()) {
            throw new IllegalArgumentException(folderName + " is not a directory");
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            throw new IllegalStateException("No files in folder: " + folderName);
        }

        File randomFile = files[ThreadLocalRandom.current().nextInt(files.length)];
        return folderName + "/" + randomFile.getName();
    }
}
