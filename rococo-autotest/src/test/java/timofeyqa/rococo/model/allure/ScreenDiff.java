package timofeyqa.rococo.model.allure;

public record ScreenDiff(
        String expected,
        String actual,
        String diff
) {

}
