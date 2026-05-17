ALTER TABLE usuarios
    ADD COLUMN medico_id BIGINT NULL;

ALTER TABLE usuarios
    ADD CONSTRAINT fk_usuarios_medico
        FOREIGN KEY (medico_id) REFERENCES medicos(id);

UPDATE usuarios
SET medico_id = 3
WHERE username = 'wmartin';
