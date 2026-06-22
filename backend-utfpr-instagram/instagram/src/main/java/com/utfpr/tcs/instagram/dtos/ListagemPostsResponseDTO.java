package com.utfpr.tcs.instagram.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListagemPostsResponseDTO extends SucessoPadraoDTO<Void> {
    private List<PostResponseDTO> posts;

    public ListagemPostsResponseDTO(String codigo, String mensagem, List<PostResponseDTO> posts) {
        this.setCodigo(codigo);
        this.setMensagem(mensagem);
        this.posts = posts;
    }
}
