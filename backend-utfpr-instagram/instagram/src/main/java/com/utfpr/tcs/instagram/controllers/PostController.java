package com.utfpr.tcs.instagram.controllers;

import com.utfpr.tcs.instagram.dtos.*;
import com.utfpr.tcs.instagram.entities.Post;
import com.utfpr.tcs.instagram.entities.Usuario;
import com.utfpr.tcs.instagram.exceptions.AcessoNegadoException;
import com.utfpr.tcs.instagram.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios/{id-usuario}/posts")
public class PostController {

    @Autowired
    private PostService service;

    @PostMapping
    public ResponseEntity<SucessoPadraoDTO<Void>> criar(@PathVariable("id-usuario") Long idUsuario,
                                                        @jakarta.validation.Valid @RequestBody CriacaoPostRequestDTO dto) {
        validarPropriedade(idUsuario);
        service.criar(idUsuario, dto);
        SucessoPadraoDTO<Void> sucesso = SucessoPadraoDTO.<Void>builder()
                .codigo("OPERACAO_SUCESSO")
                .mensagem("Post criado com sucesso")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(sucesso);
    }

    @GetMapping
    public ResponseEntity<ListagemPostsResponseDTO> listar(@PathVariable("id-usuario") Long idUsuario) {
        List<Post> posts = service.listarAtivos(idUsuario);
        List<PostResponseDTO> dtos = posts.stream().map(PostResponseDTO::new).toList();
        
        ListagemPostsResponseDTO resposta = new ListagemPostsResponseDTO("LISTAGEM_POSTS_SUCESSO", "Posts listados com sucesso", dtos);
        return ResponseEntity.ok(resposta);
    }

    @PatchMapping("/{id-post}")
    public ResponseEntity<SucessoPadraoDTO<Void>> atualizar(@PathVariable("id-usuario") Long idUsuario,
                                                            @PathVariable("id-post") Long idPost,
                                                            @jakarta.validation.Valid @RequestBody AtualizacaoPostRequestDTO dto) {
        validarPropriedade(idUsuario);
        service.atualizarLegenda(idPost, idUsuario, dto);
        SucessoPadraoDTO<Void> sucesso = SucessoPadraoDTO.<Void>builder()
                .codigo("OPERACAO_SUCESSO")
                .mensagem("Post atualizado com sucesso")
                .build();
        return ResponseEntity.ok(sucesso);
    }

    @DeleteMapping("/{id-post}")
    public ResponseEntity<SucessoPadraoDTO<Void>> deletar(@PathVariable("id-usuario") Long idUsuario,
                                                          @PathVariable("id-post") Long idPost) {
        validarPropriedade(idUsuario);
        service.deletar(idPost, idUsuario);
        SucessoPadraoDTO<Void> sucesso = SucessoPadraoDTO.<Void>builder()
                .codigo("OPERACAO_SUCESSO")
                .mensagem("Post deletado com sucesso")
                .build();
        return ResponseEntity.ok(sucesso);
    }

    @PostMapping("/{id-post}")
    public ResponseEntity<SucessoPadraoDTO<CurtidaResponseDTO>> curtir(@PathVariable("id-usuario") Long donoId,
                                                                       @PathVariable("id-post") Long idPost) {
        Usuario logado = getUsuarioLogado();
        service.curtir(idPost, logado.getId(), donoId);
        
        SucessoPadraoDTO<CurtidaResponseDTO> sucesso = SucessoPadraoDTO.<CurtidaResponseDTO>builder()
                .codigo("OPERACAO_SUCESSO")
                .mensagem("Interação registrada com sucesso")
                .dados(new CurtidaResponseDTO(logado.getId().toString(), idPost.toString()))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(sucesso);
    }

    private void validarPropriedade(Long idAlvo) {
        Usuario logado = getUsuarioLogado();
        if (!logado.getId().equals(idAlvo) && !Boolean.TRUE.equals(logado.getIsAdmin())) {
            throw new AcessoNegadoException("Acesso negado: Você não pode modificar os posts de outro usuário.");
        }
    }

    private Usuario getUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Usuario) {
            return (Usuario) auth.getPrincipal();
        }
        throw new RuntimeException("Usuário não autenticado");
    }
}
