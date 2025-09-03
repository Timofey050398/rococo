package timofeyqa.rococo.jupiter.extension;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.util.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Objects;

public class BrowserExtension implements
    BeforeEachCallback,
    AfterEachCallback,
    TestExecutionExceptionHandler,
    LifecycleMethodExecutionExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(BrowserExtension.class);

  static {
    String browser = System.getProperty("browser");
    if (StringUtils.isBlank(browser)) {
      browser = "chrome";
    }
    Configuration.timeout = 10000;
    Configuration.pageLoadStrategy = "eager";
    Configuration.browser = browser;

    LOG.info("### Browser: {}",Configuration.browser);

    ChromeOptions chromeOptions = new ChromeOptions();
    FirefoxOptions firefoxOptions = new FirefoxOptions();

    // тёмная тема
    if ("chrome".equalsIgnoreCase(Configuration.browser)) {
      chromeOptions.addArguments("--force-dark-mode");
      chromeOptions.setExperimentalOption("prefs", Map.of(
          "webkit.webprefs.preferredColorScheme", 2
      ));
    } else if ("firefox".equalsIgnoreCase(Configuration.browser)) {
      firefoxOptions.addPreference("ui.systemUsesDarkTheme", 1);
    }

    // docker-режим
    if ("Y".equals(System.getenv("SELENOID"))) {
      Configuration.remote = "http://selenoid:4444/wd/hub";

      if ("firefox".equalsIgnoreCase(Configuration.browser)) {
        Configuration.browserVersion = "125.0";
      } else {
        chromeOptions.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        Configuration.browserVersion = "127.0";
      }

      setRussianLocale(chromeOptions, firefoxOptions);
    }

    // финальное присвоение
    if ("firefox".equalsIgnoreCase(Configuration.browser)) {
      Configuration.browserCapabilities = firefoxOptions;
    } else {
      Configuration.browserCapabilities = chromeOptions;
    }
  }

  private static void setRussianLocale(ChromeOptions chromeOptions, FirefoxOptions firefoxOptions) {
    switch (Configuration.browser.toLowerCase()) {
      case "chrome", "edge" -> {
        chromeOptions.addArguments("--lang=ru-RU");
        chromeOptions.setExperimentalOption("prefs", Map.of(
            "intl.accept_languages", "ru-RU",
            "webkit.webprefs.preferredColorScheme", 2
        ));
      }
      case "firefox" -> {
        firefoxOptions.addPreference("intl.accept_languages", "ru-RU");
        firefoxOptions.addPreference("ui.systemUsesDarkTheme", 1);
      }
      default -> {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("args", "ru-RU");
        Configuration.browserCapabilities = caps;
      }
    }
  }


  @Override
  public void afterEach(ExtensionContext context) {
    if (WebDriverRunner.hasWebDriverStarted()) {
      Selenide.closeWebDriver();
    }
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
        .savePageSource(false)
        .screenshots(false)
    );
  }

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    doScreenshot();
    throw throwable;
  }

  @Override
  public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    doScreenshot();
    throw throwable;
  }

  @Override
  public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    doScreenshot();
    throw throwable;
  }

  private static void doScreenshot() {
    if (WebDriverRunner.hasWebDriverStarted()) {
      Allure.addAttachment(
          "Screen on fail",
          new ByteArrayInputStream(
              ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES)
          )
      );
    }
  }
}
