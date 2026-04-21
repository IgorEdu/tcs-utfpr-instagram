package com.utfpr.tcs.instagram.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenBlacklistServiceImpl implements TokenBlacklistService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String BLACKLIST_PREFIX = "token_blacklist:";

    @Override
    public void adicionar(String token, Instant dataExpiracao) {
        // Cálculo temporal: Evita lotar a RAM do servidor. O Redis só manterá este valor 
        // em cache pelo mesmo tempo de vida útil que restava na assinatura do usuário original.
        long tempoRestanteEmSegundos = dataExpiracao.getEpochSecond() - Instant.now().getEpochSecond();
        
        if (tempoRestanteEmSegundos > 0) {
            redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token,
                "REVOGADO",
                tempoRestanteEmSegundos,
                TimeUnit.SECONDS
            );
        }
    }

    @Override
    public boolean isRevogado(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
