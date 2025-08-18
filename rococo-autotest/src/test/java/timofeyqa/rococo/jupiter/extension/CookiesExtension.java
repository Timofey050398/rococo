package timofeyqa.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import timofeyqa.rococo.api.core.ThreadSafeCookieStore;

public class CookiesExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        ThreadSafeCookieStore.INSTANCE.removeAll();
    }
}