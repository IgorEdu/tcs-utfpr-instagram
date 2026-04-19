package com.utfpr.tcs.instagram.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.utfpr.tcs.instagram.entities.Usuario;
import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    
    @JsonProperty("nome_completo")
    private String nomeCompleto;
    
    private String usuario;
    private String email;
    private String biografia;
    
    @JsonProperty("foto")
    private String fotoUrl;

    public UsuarioDTO(Usuario obj) {
        this.id = obj.getId();
        this.nomeCompleto = obj.getNomeCompleto();
        this.usuario = obj.getUsuario();
        this.email = obj.getEmail();
        this.biografia = obj.getBiografia();
        this.fotoUrl = obj.getFotoUrl();
    }
}
