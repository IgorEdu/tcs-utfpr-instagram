package com.utfpr.tcs.instagram.controllers;

import com.utfpr.tcs.instagram.dtos.UsuarioCadastroDTO;
import com.utfpr.tcs.instagram.dtos.UsuarioDTO;
import com.utfpr.tcs.instagram.entities.Usuario;
import com.utfpr.tcs.instagram.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.utfpr.tcs.instagram.dtos.LoginDTO;
import com.utfpr.tcs.instagram.dtos.TokenResponseDTO;
import com.utfpr.tcs.instagram.services.CriptografiaService;
import com.utfpr.tcs.instagram.services.TokenService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private CriptografiaService criptografiaService;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@jakarta.validation.Valid @RequestBody UsuarioCadastroDTO dto) {
        try {
            Usuario usuarioSalvo = service.cadastrar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioDTO(usuarioSalvo));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @jakarta.validation.Valid @RequestBody com.utfpr.tcs.instagram.dtos.UsuarioAtualizacaoDTO dto) {
        try {
            Usuario usuarioAtualizado = service.atualizar(id, dto);
            return ResponseEntity.ok(new UsuarioDTO(usuarioAtualizado));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Usuário não encontrado.")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            service.deletar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<java.util.List<UsuarioDTO>> listar() {
        var listaUsuariosDto = service.listarTodos().stream().map(UsuarioDTO::new).toList();
        return ResponseEntity.ok(listaUsuariosDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@jakarta.validation.Valid @RequestBody LoginDTO loginDTO) {
        try {
            Usuario usuario = service.obterPorUsuario(loginDTO.getUsuario());

            boolean senhaCorreta = criptografiaService.verificarSenha(loginDTO.getSenha(), usuario.getSenha());
            if (!senhaCorreta) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
            }

            String token = tokenService.gerarToken(usuario.getUsuario());
            return ResponseEntity.ok(new TokenResponseDTO(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obterPorId(@PathVariable Long id) {
        try {
            Usuario usuario = service.obterPorId(id);
            return ResponseEntity.ok(new UsuarioDTO(usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
