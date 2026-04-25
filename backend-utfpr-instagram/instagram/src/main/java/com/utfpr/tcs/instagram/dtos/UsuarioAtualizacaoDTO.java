package com.utfpr.tcs.instagram.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UsuarioAtualizacaoDTO extends UsuarioBaseDTO {
    // Vazio! Ele herda todos os limites de tamanho e regex da UsuarioBaseDTO
    // sem tornar nada obrigatoriamente forçado (@NotBlank)
}
