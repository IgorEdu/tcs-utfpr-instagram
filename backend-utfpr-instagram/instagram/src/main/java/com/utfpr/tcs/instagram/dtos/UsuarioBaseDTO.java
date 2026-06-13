package com.utfpr.tcs.instagram.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public abstract class UsuarioBaseDTO {

    @Size(min = 3, max = 60, message = "O nome deve ter entre 3 e 60 caracteres")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]*$", message = "Nome não deve conter números ou caracteres especiais")
    @JsonProperty("nome")
    private String nomeCompleto;

    @Size(min = 3, max = 30, message = "O usuário deve ter entre 3 e 30 caracteres")
    @Pattern(regexp = "^[a-z0-9_]*$", message = "Usuário apenas com letras minúsculas, números e underline")
    private String usuario;

    @Email(message = "Formato de e-mail inválido")
    @Size(min = 10, max = 35, message = "O email deve ter entre 10 e 35 caracteres")
    private String email;

    @Size(min = 5, max = 150, message = "Biografia deve conter entre 5 e 150 caracteres")
    private String biografia;

    @JsonProperty("foto")
    private String fotoUrl;

    @Size(min = 8, max = 24, message = "A senha deve ter entre 8 e 24 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "A senha deve conter apenas letras e números")
    private String senha;
}
