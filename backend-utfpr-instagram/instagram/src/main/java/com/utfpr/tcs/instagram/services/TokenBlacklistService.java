package com.utfpr.tcs.instagram.services;

import java.time.Instant;

public interface TokenBlacklistService {

    void adicionar(String token, Instant dataExpiracao);

    boolean isRevogado(String token);
}
