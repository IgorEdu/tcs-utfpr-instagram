package com.utfpr.tcs.instagram.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UsuarioAtualizacaoDTO {

    @JsonProperty("nome_completo")
    private String nomeCompleto;

    private String usuario;

    @Email(message = "Formato de e-mail inválido")
    private String email;

    private String biografia;

    @JsonProperty("foto")
    private String fotoUrl;

    private String senha;
}
