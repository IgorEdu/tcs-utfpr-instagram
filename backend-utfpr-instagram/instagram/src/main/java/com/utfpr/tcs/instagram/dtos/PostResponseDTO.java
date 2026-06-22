package com.utfpr.tcs.instagram.dtos;

import com.utfpr.tcs.instagram.entities.Post;
import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PostResponseDTO {
    private String id;
    private String legenda;
    private String imagem;
    private String curtidas;
    private List<String> curtidasIds;

    public PostResponseDTO(Post post) {
        this.id = post.getId().toString();
        this.legenda = post.getLegenda();
        this.imagem = post.getImagemBase64();
        this.curtidas = String.valueOf(post.getCurtidas().size());
        this.curtidasIds = post.getCurtidas().stream()
                               .map(u -> u.getId().toString())
                               .collect(Collectors.toList());
    }
}
