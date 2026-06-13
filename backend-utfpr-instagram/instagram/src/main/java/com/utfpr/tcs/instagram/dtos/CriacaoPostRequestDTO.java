package com.utfpr.tcs.instagram.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CriacaoPostRequestDTO {
    @NotBlank(message = "A imagem é obrigatória")
    private String imagem;

    @NotBlank(message = "A legenda é obrigatória")
    @Size(min = 5, max = 200, message = "A legenda deve ter entre 5 e 200 caracteres")
    private String legenda;
}
