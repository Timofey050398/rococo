package timofeyqa.rococo.page;

import timofeyqa.rococo.config.Config;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

    public abstract T checkThatPageLoaded();
    protected static final Config CFG = Config.getInstance();
}
