package timofeyqa.rococo.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;
import timofeyqa.rococo.api.core.ThreadSafeCookieStore;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.jupiter.annotation.ApiLogin;
import timofeyqa.rococo.jupiter.annotation.Token;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.page.MainPage;
import timofeyqa.rococo.service.api.AuthRestClient;
import timofeyqa.rococo.service.api.UserRestClient;

import javax.annotation.Nonnull;

import static timofeyqa.rococo.jupiter.extension.UserExtension.setUser;


public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
    private static final Config CFG = Config.getInstance();

    private final AuthRestClient authApiClient = new AuthRestClient();
    private final UserRestClient usersApiClient = new UserRestClient();

    private final boolean setupBrowser;

    private ApiLoginExtension(boolean setupBrowser){
        this.setupBrowser = setupBrowser;
    }
    public ApiLoginExtension(){
        this.setupBrowser = true;
    }

    public static ApiLoginExtension rest(){
        return new ApiLoginExtension(false);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {
                    UserJson user;
                    UserJson extensionUser = UserExtension.createdUser();
                    if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())){
                        if (extensionUser == null) {
                            throw new IllegalStateException("@User must be present in case that @ApiLogin is empty!");
                        }
                        user = extensionUser;
                    } else {
                        if (extensionUser != null) {
                            throw new IllegalStateException("@User must not be present in case that @ApiLogin contains username and password!");
                        }
                        user = enrichmentUser(apiLogin);
                        setUser(user);
                    }

                    setToken(authApiClient.token(user));

                    if (setupBrowser) {
                        setupBrowserSession();
                    }

                });
    }

    @Step("Pre condition: Setup authorized web session")
    private void setupBrowserSession(){
        Selenide.open(CFG.frontUrl());
        Selenide.localStorage().setItem("id_token", getToken());
        WebDriverRunner.getWebDriver().manage().addCookie(getJsessionIdCookie());
        Selenide.open(CFG.frontUrl(), MainPage.class).checkThatPageLoaded();
    }

    private UserJson enrichmentUser(@Nonnull ApiLogin apiLogin){
        final String username = apiLogin.username();
        UserJson user = usersApiClient.getUser(username);

        return user.withPassword(apiLogin.password());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(String.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get("token", String.class);
    }

    public static void setToken(@Nonnull String token){
        TestMethodContextExtension.context().getStore(NAMESPACE).put("token",token);
    }

    public static String getToken(){
        return (String) TestMethodContextExtension.context().getStore(NAMESPACE).get("token");
    }

    public static void setCode(@Nonnull String code){
        TestMethodContextExtension.context().getStore(NAMESPACE).put("code",code);
    }

    public static String getCode(){
        return (String) TestMethodContextExtension.context().getStore(NAMESPACE).get("code");
    }

    public static Cookie getJsessionIdCookie(){
        return new Cookie(
                "JSESSIONID",
                ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
        );
    }
}
