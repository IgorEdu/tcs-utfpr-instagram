ALTER TABLE usuarios ADD COLUMN is_admin BOOLEAN NOT NULL DEFAULT FALSE;

INSERT INTO usuarios (nome_completo, usuario, email, senha, is_admin, ativo)
VALUES ('Administrador', 'admin', 'admin@testeutfpr.com.br', '$2b$12$iqqtp8HtzL2jfuCMWyg9BuDuIV0bdnBrm9p8fV1k.ZjH9QjrrdmTm', TRUE, TRUE);
