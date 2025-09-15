CREATE TABLE IF NOT EXISTS `logs` (
    id        BINARY(16)    NOT NULL DEFAULT (UUID_TO_BIN(UUID(), true)),
    service   VARCHAR(50)   NOT NULL,
    level     VARCHAR(20)   NOT NULL,
    thread    VARCHAR(100),
    logger    VARCHAR(255),
    message   TEXT,
    timestamp TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
