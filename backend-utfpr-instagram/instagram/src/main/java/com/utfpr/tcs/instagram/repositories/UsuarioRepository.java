package com.utfpr.tcs.instagram.repositories;

import com.utfpr.tcs.instagram.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByUsuario(String usuario);

    java.util.List<Usuario> findByAtivoTrue();

    Optional<Usuario> findByIdAndAtivoTrue(Long id);
}
