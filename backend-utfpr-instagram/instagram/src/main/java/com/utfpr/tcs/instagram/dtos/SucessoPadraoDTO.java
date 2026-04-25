package com.utfpr.tcs.instagram.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SucessoPadraoDTO<T> {
    
    @Builder.Default
    private String status = "sucesso";
    
    private String codigo;
    
    private String mensagem;
    
    private T dados;
}
