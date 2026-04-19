package com.utfpr.tcs.instagram.services;

import com.utfpr.tcs.instagram.dtos.UsuarioCadastroDTO;
import com.utfpr.tcs.instagram.entities.Usuario;
import com.utfpr.tcs.instagram.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private CriptografiaService criptografiaService;

    public Usuario cadastrar(UsuarioCadastroDTO dto) {
        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já está em uso.");
        }
        if (repository.findByUsuario(dto.getUsuario()).isPresent()) {
            throw new RuntimeException("Nome de usuário já está em uso.");
        }

        Usuario novoUsuario = Usuario.builder()
                .nomeCompleto(dto.getNomeCompleto())
                .usuario(dto.getUsuario())
                .email(dto.getEmail())
                .biografia(dto.getBiografia())
                .fotoUrl(dto.getFotoUrl())
                .senha(criptografiaService.criptografarSenha(dto.getSenha()))
                .build();

        return repository.save(novoUsuario);
    }

    public Usuario atualizar(Long id, com.utfpr.tcs.instagram.dtos.UsuarioAtualizacaoDTO dto) {
        Usuario usuarioExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (dto.getEmail() != null) {
            if (!usuarioExistente.getEmail().equals(dto.getEmail())
                    && repository.findByEmail(dto.getEmail()).isPresent()) {
                throw new RuntimeException("E-mail já está em uso.");
            }
            usuarioExistente.setEmail(dto.getEmail());
        }

        if (dto.getUsuario() != null) {
            if (!usuarioExistente.getUsuario().equals(dto.getUsuario())
                    && repository.findByUsuario(dto.getUsuario()).isPresent()) {
                throw new RuntimeException("Nome de usuário já está em uso.");
            }
            usuarioExistente.setUsuario(dto.getUsuario());
        }

        if (dto.getNomeCompleto() != null) {
            usuarioExistente.setNomeCompleto(dto.getNomeCompleto());
        }

        if (dto.getBiografia() != null) {
            usuarioExistente.setBiografia(dto.getBiografia());
        }

        if (dto.getFotoUrl() != null) {
            usuarioExistente.setFotoUrl(dto.getFotoUrl());
        }

        if (dto.getSenha() != null && !dto.getSenha().trim().isEmpty()) {
            usuarioExistente.setSenha(criptografiaService.criptografarSenha(dto.getSenha()));
        }

        return repository.save(usuarioExistente);
    }

    public void deletar(Long id) {
        Usuario usuarioExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        usuarioExistente.setAtivo(false);
        repository.save(usuarioExistente);
    }

    public java.util.List<Usuario> listarTodos() {
        // Retorna a lista contendo apenas usuários ativos
        return repository.findByAtivoTrue();
    }

    public Usuario obterPorId(Long id) {
        return repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }
}
