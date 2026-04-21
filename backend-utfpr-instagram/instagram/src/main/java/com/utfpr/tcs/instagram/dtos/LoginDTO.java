package com.utfpr.tcs.instagram.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    
    @NotBlank(message = "O usuário é obrigatório")
    private String usuario;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;
}
