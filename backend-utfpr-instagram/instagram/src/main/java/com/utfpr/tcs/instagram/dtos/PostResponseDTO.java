package com.utfpr.tcs.instagram.dtos;

import com.utfpr.tcs.instagram.entities.Post;
import lombok.Data;

@Data
public class PostResponseDTO {
    private String id;
    private String conteudo;
    private String imagem;
    private String curtidas;

    public PostResponseDTO(Post post) {
        this.id = post.getId().toString();
        this.conteudo = post.getLegenda();
        this.imagem = post.getImagemBase64();
        this.curtidas = String.valueOf(post.getCurtidas().size());
    }
}
