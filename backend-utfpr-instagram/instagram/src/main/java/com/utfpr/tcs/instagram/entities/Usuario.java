package com.utfpr.tcs.instagram.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "usuario", nullable = false, unique = true)
    private String usuario;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "biografia", length = 150)
    private String biografia;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Builder.Default
    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;

    @Builder.Default
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
}
