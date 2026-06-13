package com.utfpr.tcs.instagram.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurtidaResponseDTO {
    @JsonProperty("id_usuario")
    private String idUsuario;

    @JsonProperty("id_post")
    private String idPost;
}
