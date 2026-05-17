CREATE TABLE IF NOT EXISTS login_attempts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    intentos_fallidos INT NOT NULL,
    bloqueado_desde DATETIME(6) NULL,
    actualizado DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_login_attempts_username (username)
);
