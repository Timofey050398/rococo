package timofeyqa.rococo.jupiter.extension;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.data.jdbc.Connections;
import timofeyqa.rococo.data.jpa.EntityManagers;

public class DatabasesExtension implements SuiteExtension {

    private static final Logger LOG = LoggerFactory.getLogger(DatabasesExtension.class);
    private static final Config CFG = Config.getInstance();

    @Override
    public void beforeSuite(ExtensionContext context) {
        if ("docker".equals(System.getenv("test.env"))) {
            String host = StringUtils.substringBefore(
                StringUtils.substringAfter(CFG.jdbcUrl(), "jdbc:mysql://"),
                ":"+CFG.dbPort()
            );
            LOG.info("### Database env's host: {}", host);
        }
    }

    @Override
    public void afterSuite() {
        Connections.closeAllConnections();
        EntityManagers.closeAllEmfs();
    }
}