package com.utfpr.tcs.instagram.services;

import com.utfpr.tcs.instagram.dtos.AtualizacaoPostRequestDTO;
import com.utfpr.tcs.instagram.dtos.CriacaoPostRequestDTO;
import com.utfpr.tcs.instagram.entities.Post;
import com.utfpr.tcs.instagram.entities.Usuario;
import com.utfpr.tcs.instagram.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UsuarioService usuarioService;

    public Post criar(Long usuarioId, CriacaoPostRequestDTO dto) {
        Usuario usuario = usuarioService.obterPorId(usuarioId);
        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new RuntimeException("Usuário inativo.");
        }
        Post post = new Post();
        post.setUsuario(usuario);
        post.setImagemBase64(dto.getImagem());
        post.setLegenda(dto.getLegenda());
        return postRepository.save(post);
    }

    public List<Post> listarAtivos(Long usuarioId) {
        Usuario usuario = usuarioService.obterPorId(usuarioId);
        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
             throw new RuntimeException("Usuário inativo.");
        }
        return postRepository.findByUsuarioIdAndAtivoTrue(usuarioId);
    }

    public Post atualizarLegenda(Long idPost, Long usuarioId, AtualizacaoPostRequestDTO dto) {
        Post post = postRepository.findByIdAndUsuarioIdAndAtivoTrue(idPost, usuarioId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado para este usuário."));
        post.setLegenda(dto.getLegenda());
        return postRepository.save(post);
    }

    public void deletar(Long idPost, Long usuarioId) {
        Post post = postRepository.findByIdAndUsuarioIdAndAtivoTrue(idPost, usuarioId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado para este usuário."));
        post.setAtivo(false);
        postRepository.save(post);
    }

    public void curtir(Long idPost, Long usuarioId, Long donoId) {
        // Encontrar o post apenas para o dono especifico. Se for "qualquer post", poderia ser diferente,
        // Mas a rota é /usuarios/{donoId}/posts/{idPost}
        Post post = postRepository.findByIdAndUsuarioIdAndAtivoTrue(idPost, donoId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado."));
        
        Usuario autorCurtida = usuarioService.obterPorId(usuarioId);
        
        if (post.getCurtidas().contains(autorCurtida)) {
            post.getCurtidas().remove(autorCurtida);
        } else {
            post.getCurtidas().add(autorCurtida);
        }
        postRepository.save(post);
    }
}
