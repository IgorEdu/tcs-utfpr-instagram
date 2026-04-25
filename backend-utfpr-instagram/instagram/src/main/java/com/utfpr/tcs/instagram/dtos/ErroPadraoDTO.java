package com.utfpr.tcs.instagram.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErroPadraoDTO {
    
    @Builder.Default
    private String status = "erro";
    
    private String codigo;
    
    private String mensagem;
}
