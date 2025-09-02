package timofeyqa.rococo.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {

    public static final String caseName = "Rococo backend logs";
    private static final Set<String> serviceNames = Set.of(
        "auth",
        "userdata",
        "geo",
        "painting",
        "artist",
        "museum",
        "gateway"
    );

    @Override
    public void afterSuite() {
        if(!"docker".equals(System.getProperty("test.env"))) {
            final AllureLifecycle allureLifecycle = Allure.getLifecycle();
            final String caseId = UUID.randomUUID().toString();
            allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
            allureLifecycle.startTestCase(caseId);

            for (String serviceName : serviceNames) {
                logAttachment(serviceName, allureLifecycle);
            }

            allureLifecycle.stopTestCase(caseId);
            allureLifecycle.writeTestCase(caseId);
        }
    }


    private static void logAttachment(String serviceName, AllureLifecycle allureLifecycle) {
        String name = String.format("Rococo-%s log", serviceName);
        Path path = Path.of(String.format("./logs/rococo_%s/app.log", serviceName));

        if (Files.notExists(path)) {
            return;
        }

        try (var input = Files.newInputStream(path)) {
            allureLifecycle.addAttachment(
                name,
                "text/html",
                ".log",
                input
            );
        } catch (IOException e) {
            System.err.printf("Could not attach log for service %s: %s%n", serviceName, e.getMessage());
        }
    }
}
