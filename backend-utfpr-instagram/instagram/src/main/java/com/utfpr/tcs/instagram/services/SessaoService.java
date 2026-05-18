package com.utfpr.tcs.instagram.services;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SessaoService {

    private final Map<String, SessaoAtiva> sessoesAtivas = new ConcurrentHashMap<>();
    private final MeterRegistry meterRegistry;
    private final MultiGauge activeUsersGauge;

    public SessaoService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.activeUsersGauge = MultiGauge.builder("instagram_user_active_sessions").register(meterRegistry);
        
        meterRegistry.gauge("instagram_active_sessions", sessoesAtivas, map -> map.values().stream().filter(SessaoAtiva::isAtiva).count());
    }

    public void registrarSessao(String token, String username, String ip) {
        sessoesAtivas.put(token, new SessaoAtiva(username, ip, true));
        meterRegistry.counter("instagram_login_events", "username", username, "ip", ip).increment();
        atualizarGrafana();
    }

    public void removerSessao(String token) {
        SessaoAtiva sessao = sessoesAtivas.get(token);
        if (sessao != null) {
            // Em vez de remover do mapa, apenas marcamos como inativo.
            // Isso força o MultiGauge a enviar '0' para o Prometheus, limpando a tabela do Grafana instantaneamente
            // sem acionar o 'Stale Marker' de 5 minutos do Prometheus.
            sessao.setAtiva(false);
            atualizarGrafana();
        }
    }

    private void atualizarGrafana() {
        var rows = sessoesAtivas.values().stream()
                .map(sessao -> MultiGauge.Row.of(Tags.of("username", sessao.username(), "ip", sessao.ip()), sessao.isAtiva() ? 1 : 0))
                .collect(Collectors.toList());
        activeUsersGauge.register(rows, true);
    }

    public Collection<SessaoAtiva> getSessoesAtivas() {
        return sessoesAtivas.values().stream().filter(SessaoAtiva::isAtiva).collect(Collectors.toList());
    }

    public static class SessaoAtiva {
        private final String username;
        private final String ip;
        private boolean ativa;

        public SessaoAtiva(String username, String ip, boolean ativa) {
            this.username = username;
            this.ip = ip;
            this.ativa = ativa;
        }

        public String username() { return username; }
        public String ip() { return ip; }
        public boolean isAtiva() { return ativa; }
        public void setAtiva(boolean ativa) { this.ativa = ativa; }
    }
}
