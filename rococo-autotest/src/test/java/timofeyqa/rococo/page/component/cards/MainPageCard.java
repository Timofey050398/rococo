package timofeyqa.rococo.page.component.cards;

public class MainPageCard extends Card<MainPageCard> {
  private final static String TITLE_TAG = "div";

  public MainPageCard(String title) {
    super(title,TITLE_TAG);
  }

  @Override
  public String titleTag() {
    return TITLE_TAG;
  }
}
