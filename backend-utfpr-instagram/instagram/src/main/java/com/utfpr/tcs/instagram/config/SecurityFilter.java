package com.utfpr.tcs.instagram.config;

import com.utfpr.tcs.instagram.entities.Usuario;
import com.utfpr.tcs.instagram.repositories.UsuarioRepository;
import com.utfpr.tcs.instagram.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private com.utfpr.tcs.instagram.services.TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            // Nota de Arquitetura Spec: Bloqueio estrito baseado em cache statful
            if (tokenBlacklistService.isRevogado(tokenJWT)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Sessão finalizada. Este token foi revogado no sistema!");
                return;
            }

            try {
                var subject = tokenService.getSubject(tokenJWT);
                
                Usuario usuario = repository.findByUsuario(subject)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

                var authorities = Boolean.TRUE.equals(usuario.getIsAdmin()) 
                        ? java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")) 
                        : java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"));
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException ex) {
                // Se de erro no parsing, bloqueamos não logando o contexto. Spring devolve 401.
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
