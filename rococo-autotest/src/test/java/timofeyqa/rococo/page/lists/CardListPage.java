package timofeyqa.rococo.page.lists;

import com.codeborne.selenide.ElementsCollection;
import timofeyqa.rococo.page.component.cards.Card;

public interface CardListPage<T extends CardListPage<T,?>, B extends Card<B>> extends ListPage<T> {
  B getCard(String title);

  ElementsCollection cards();

  T compareCardNotFound();

  T comparePageIsEmpty();

  @Override
  default ElementsCollection list(){
    return cards();
  }
}