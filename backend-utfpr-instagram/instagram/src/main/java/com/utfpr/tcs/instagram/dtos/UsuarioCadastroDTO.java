package com.utfpr.tcs.instagram.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UsuarioCadastroDTO extends UsuarioBaseDTO {

    @NotBlank(message = "O nome completo é obrigatório")
    @Override
    public String getNomeCompleto() { return super.getNomeCompleto(); }

    @NotBlank(message = "O nome de usuário é obrigatório")
    @Override
    public String getUsuario() { return super.getUsuario(); }

    @NotBlank(message = "O e-mail é obrigatório")
    @Override
    public String getEmail() { return super.getEmail(); }

    @NotBlank(message = "A biografia é obrigatória no cadastro")
    @Override
    public String getBiografia() { return super.getBiografia(); }

    @NotBlank(message = "A senha é obrigatória")
    @Override
    public String getSenha() { return super.getSenha(); }
}
