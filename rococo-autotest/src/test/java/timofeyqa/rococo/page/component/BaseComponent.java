package timofeyqa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public abstract class BaseComponent<T extends BaseComponent<?>> {

    @Getter
    protected final SelenideElement self;

    protected BaseComponent(SelenideElement self) {
        this.self = self;
    }

    public <B> B toPage(Class<B> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create instance of " + clazz, e);
        }
    }
}