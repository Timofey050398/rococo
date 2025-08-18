package timofeyqa.rococo.page.component.cards;


import com.codeborne.selenide.SelenideElement;

public class MuseumCard extends Card<MuseumCard> {
  private final static String TITLE_TAG = "div";
  private final SelenideElement geo = self.$x("./div[1]");

  public MuseumCard(String title) {
    super(title,TITLE_TAG);
  }

  @Override
  public String titleTag() {
    return TITLE_TAG;
  }

  public String getGeo(){
    return geo.getText();
  }
}
