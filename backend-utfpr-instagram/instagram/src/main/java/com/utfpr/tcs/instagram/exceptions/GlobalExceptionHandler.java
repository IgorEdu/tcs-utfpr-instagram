package com.utfpr.tcs.instagram.exceptions;

import com.utfpr.tcs.instagram.dtos.ErroPadraoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroPadraoDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String mensagemErro = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        /* 
         * Alternativa numeral acionável:
         * .codigo(String.valueOf(HttpStatus.BAD_REQUEST.value())) // Retornaria o literal "400"
         */
        ErroPadraoDTO erro = ErroPadraoDTO.builder()
                .codigo("DADOS_INVALIDOS")
                .mensagem(mensagemErro)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroPadraoDTO> handleRuntimeExceptions(RuntimeException ex) {
        // Mapeando algumas exceções genéricas de regra de negócio para formato JSON OpenAPI
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String codigo = "REGRA_NEGOCIO_ERRO";

        if (ex.getMessage().contains("não encontrado")) {
            status = HttpStatus.NOT_FOUND;
            codigo = "RECURSO_NAO_ENCONTRADO";
        } else if (ex.getMessage().contains("Credenciais")) {
            status = HttpStatus.UNAUTHORIZED;
            codigo = "CREDENCIAIS_INVALIDAS";
        } else if (ex.getMessage().contains("já está em uso")) {
            status = HttpStatus.CONFLICT;
            codigo = "CONFLITO_DADOS";
        }

        /*
         * NOTA ARQUITETURAL SOBRE STATUS CODE
         * Caso o time decida no futuro que o campo "codigo" do JSON não deve
         * mais ser textual (ex: "DADOS_INVALIDOS") e passe a ser o numeral puro 
         * do Código HTTP, basta substituir a inicialização acimo por:
         * 
         * String codigo = String.valueOf(status.value());
         */

        ErroPadraoDTO erro = ErroPadraoDTO.builder()
                .codigo(codigo)
                .mensagem(ex.getMessage())
                .build();

        return ResponseEntity.status(status).body(erro);
    }
}
