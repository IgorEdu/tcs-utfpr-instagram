package com.utfpr.tcs.instagram.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListagemPadraoDTO<T> {
    private List<T> usuarios; // Deixando "usuarios" para manter o Swagger igual (ou itens universal)
}
