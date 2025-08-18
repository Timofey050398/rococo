package timofeyqa.rococo.jupiter.extension;


import timofeyqa.rococo.data.jdbc.Connections;
import timofeyqa.rococo.data.jpa.EntityManagers;

public class DatabasesExtension implements SuiteExtension {

    @Override
    public void afterSuite() {
        Connections.closeAllConnections();
        EntityManagers.closeAllEmfs();
    }
}