CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    imagem_base64 TEXT NOT NULL,
    legenda VARCHAR(200) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT true,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_posts_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE TABLE curtidas_post (
    id_post BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    PRIMARY KEY (id_post, id_usuario),
    CONSTRAINT fk_curtidas_post FOREIGN KEY (id_post) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_curtidas_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios (id) ON DELETE CASCADE
);
