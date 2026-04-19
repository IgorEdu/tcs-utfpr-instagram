package com.utfpr.tcs.instagram.services;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class CriptografiaService {

    public String criptografarSenha(String senhaPlana) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senhaPlana.getBytes(StandardCharsets.UTF_8));

            // Converte os bytes do hash para uma string hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao criptografar senha: " + e.getMessage(), e);
        }
    }

    public boolean verificarSenha(String senhaPlana, String senhaCriptografada) {
        String hashGerado = criptografarSenha(senhaPlana);
        return hashGerado.equals(senhaCriptografada);
    }
}
