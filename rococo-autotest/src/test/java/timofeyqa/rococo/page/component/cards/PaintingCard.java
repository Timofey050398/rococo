package timofeyqa.rococo.page.component.cards;

public class PaintingCard extends Card<PaintingCard> {
  private final static String TITLE_TAG = "div";

  public PaintingCard(String title) {
    super(title,TITLE_TAG);
  }

  @Override
  String titleTag() {
    return TITLE_TAG;
  }
}
