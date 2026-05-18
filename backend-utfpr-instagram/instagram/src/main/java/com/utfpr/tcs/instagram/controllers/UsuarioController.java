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
import com.utfpr.tcs.instagram.dtos.SucessoPadraoDTO;
import com.utfpr.tcs.instagram.dtos.ListagemPadraoDTO;
import com.utfpr.tcs.instagram.services.CriptografiaService;
import com.utfpr.tcs.instagram.services.TokenService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.utfpr.tcs.instagram.exceptions.AcessoNegadoException;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private CriptografiaService criptografiaService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private com.utfpr.tcs.instagram.services.TokenBlacklistService tokenBlacklistService;

    @Autowired
    private com.utfpr.tcs.instagram.services.SessaoService sessaoService;

    @PostMapping
    public ResponseEntity<SucessoPadraoDTO<UsuarioDTO>> cadastrar(@jakarta.validation.Valid @RequestBody UsuarioCadastroDTO dto) {
        Usuario usuarioSalvo = service.cadastrar(dto);
        // Código alternativo interno utilizado anteriormente: .codigo("CADASTRO_CONCLUIDO")
        SucessoPadraoDTO<UsuarioDTO> sucesso = SucessoPadraoDTO.<UsuarioDTO>builder()
            .codigo("USUARIO_CRIADO")
            .mensagem("Usuário criado com sucesso.")
            .dados(new UsuarioDTO(usuarioSalvo))
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(sucesso);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SucessoPadraoDTO<UsuarioDTO>> atualizar(@PathVariable Long id, @jakarta.validation.Valid @RequestBody com.utfpr.tcs.instagram.dtos.UsuarioAtualizacaoDTO dto) {
        validarPermissao(id);
        Usuario usuarioAtualizado = service.atualizar(id, dto);
        SucessoPadraoDTO<UsuarioDTO> sucesso = SucessoPadraoDTO.<UsuarioDTO>builder()
            .codigo("ATUALIZACAO_CONCLUIDA")
            .mensagem("Dados atualizados com sucesso.")
            .dados(new UsuarioDTO(usuarioAtualizado))
            .build();
        return ResponseEntity.ok(sucesso);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SucessoPadraoDTO<Void>> deletar(@PathVariable Long id, jakarta.servlet.http.HttpServletRequest request) {
        validarPermissao(id);
        service.deletar(id);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Usuario) {
            Usuario logado = (Usuario) auth.getPrincipal();
            if (logado.getId().equals(id)) {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.replace("Bearer ", "");
                    java.time.Instant expiracao = tokenService.getExpirationDate(token);
                    tokenBlacklistService.adicionar(token, expiracao);
                    sessaoService.removerSessao(token);
                }
            }
        }

        SucessoPadraoDTO<Void> sucesso = SucessoPadraoDTO.<Void>builder()
            .codigo("USUARIO_DESATIVADO")
            .mensagem("Conta de usuário desativada com sucesso.")
            .build();
        return ResponseEntity.ok(sucesso);
    }

    @GetMapping
    public ResponseEntity<SucessoPadraoDTO<ListagemPadraoDTO<UsuarioDTO>>> listar() {
        var listaUsuariosDto = service.listarTodos().stream().map(UsuarioDTO::new).toList();
        ListagemPadraoDTO<UsuarioDTO> listagem = ListagemPadraoDTO.<UsuarioDTO>builder()
            .usuarios(listaUsuariosDto)
            .build();
        // Código alternativo interno utilizado anteriormente: .codigo("LISTAGEM_CONCLUIDA")
        SucessoPadraoDTO<ListagemPadraoDTO<UsuarioDTO>> sucesso = SucessoPadraoDTO.<ListagemPadraoDTO<UsuarioDTO>>builder()
            .codigo("LISTAGEM_SUCESSO")
            .mensagem("Listagem de usuários recuperada.")
            .dados(listagem)
            .build();
        return ResponseEntity.ok(sucesso);
    }

    @PostMapping("/login")
    public ResponseEntity<SucessoPadraoDTO<TokenResponseDTO>> login(@jakarta.validation.Valid @RequestBody LoginDTO loginDTO, jakarta.servlet.http.HttpServletRequest request) {
        Usuario usuario = service.obterPorUsuario(loginDTO.getUsuario());

        boolean senhaCorreta = criptografiaService.verificarSenha(loginDTO.getSenha(), usuario.getSenha());
        if (!senhaCorreta) {
            throw new RuntimeException("Credenciais inválidas.");
        }

        String token = tokenService.gerarToken(usuario);
        
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        sessaoService.registrarSessao(token, usuario.getUsuario(), ip);

        SucessoPadraoDTO<TokenResponseDTO> sucesso = SucessoPadraoDTO.<TokenResponseDTO>builder()
            .codigo("LOGIN_CONCLUIDO")
            .mensagem("Autenticação realizada com sucesso.")
            .dados(new TokenResponseDTO(token, new UsuarioDTO(usuario)))
            .build();
        return ResponseEntity.ok(sucesso);
    }

    @PostMapping("/logout")
    public ResponseEntity<SucessoPadraoDTO<Void>> logout(jakarta.servlet.http.HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Credenciais inválidas.");
        }

        String token = authHeader.replace("Bearer ", "");
        java.time.Instant expiracao = tokenService.getExpirationDate(token);
        tokenBlacklistService.adicionar(token, expiracao);
        sessaoService.removerSessao(token);

        SucessoPadraoDTO<Void> sucesso = SucessoPadraoDTO.<Void>builder()
            .codigo("SESSAO_ENCERRADA")
            .mensagem("Logout realizado, token invalidado.")
            .build();
        return ResponseEntity.ok(sucesso);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucessoPadraoDTO<UsuarioDTO>> obterPorId(@PathVariable Long id) {
        Usuario usuario = service.obterPorId(id);
        SucessoPadraoDTO<UsuarioDTO> sucesso = SucessoPadraoDTO.<UsuarioDTO>builder()
            .codigo("RECURSO_RECUPERADO")
            .mensagem("Usuário encontrado.")
            .dados(new UsuarioDTO(usuario))
            .build();
        return ResponseEntity.ok(sucesso);
    }

    @GetMapping("/sessoes")
    public ResponseEntity<java.util.Collection<com.utfpr.tcs.instagram.services.SessaoService.SessaoAtiva>> listarSessoes() {
        return ResponseEntity.ok(sessaoService.getSessoesAtivas());
    }

    private void validarPermissao(Long idAlvo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Usuario) {
            Usuario logado = (Usuario) auth.getPrincipal();
            if (!logado.getId().equals(idAlvo) && !Boolean.TRUE.equals(logado.getIsAdmin())) {
                throw new AcessoNegadoException("Você não tem permissão para realizar esta operação na conta deste usuário.");
            }
        }
    }
}
