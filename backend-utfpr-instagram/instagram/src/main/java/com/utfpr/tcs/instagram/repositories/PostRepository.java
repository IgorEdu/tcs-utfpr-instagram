package com.utfpr.tcs.instagram.repositories;

import com.utfpr.tcs.instagram.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUsuarioIdAndAtivoTrue(Long usuarioId);
    Optional<Post> findByIdAndUsuarioIdAndAtivoTrue(Long id, Long usuarioId);
    Optional<Post> findByIdAndAtivoTrue(Long id);
}
