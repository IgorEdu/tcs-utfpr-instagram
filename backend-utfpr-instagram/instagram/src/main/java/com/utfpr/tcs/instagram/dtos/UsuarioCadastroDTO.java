package com.utfpr.tcs.instagram.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioCadastroDTO {
    
    @NotBlank(message = "O nome completo é obrigatório")
    @JsonProperty("nome_completo")
    private String nomeCompleto;

    @NotBlank(message = "O nome de usuário é obrigatório")
    private String usuario;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String email;

    private String biografia;

    @JsonProperty("foto")
    private String fotoUrl;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;
}
