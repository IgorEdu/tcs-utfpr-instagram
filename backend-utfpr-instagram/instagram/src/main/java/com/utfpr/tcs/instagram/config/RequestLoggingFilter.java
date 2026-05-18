package com.utfpr.tcs.instagram.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Ignora rotas de arquivos estáticos, swagger e actuator para não poluir o log
        String uri = request.getRequestURI();
        if (uri.startsWith("/v3/api-docs") || uri.startsWith("/swagger-ui") || uri.startsWith("/actuator") || uri.startsWith("/api-specs.json")) {
            filterChain.doFilter(request, response);
            return;
        }

        CachedBodyHttpServletRequest requestWrapper = new CachedBodyHttpServletRequest(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long timeTaken = System.currentTimeMillis() - startTime;

            String requestBody = getStringValue(requestWrapper.getCachedBody(), request.getCharacterEncoding());
            String responseBody = getStringValue(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding());

            String formattedRequest = requestBody.isEmpty() ? "Vazio" : requestBody.replaceAll("[\\r\\n]+", "");
            String formattedResponse = responseBody.isEmpty() ? "Vazio" : responseBody.replaceAll("[\\r\\n]+", "");

            String logMessage = "HTTP {} {} | STATUS: {} | TEMPO: {}ms | PAYLOAD: {} | RESPONSE: {}";
            int status = response.getStatus();

            if (status >= 500) {
                logger.error(logMessage, request.getMethod(), uri, status, timeTaken, formattedRequest, formattedResponse);
            } else if (status >= 400) {
                logger.warn(logMessage, request.getMethod(), uri, status, timeTaken, formattedRequest, formattedResponse);
            } else {
                logger.info(logMessage, request.getMethod(), uri, status, timeTaken, formattedRequest, formattedResponse);
            }

            responseWrapper.copyBodyToResponse();
        }
    }

    private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
        if (contentAsByteArray == null || contentAsByteArray.length == 0) {
            return "";
        }
        try {
            return new String(contentAsByteArray, 0, contentAsByteArray.length, characterEncoding != null ? characterEncoding : "UTF-8");
        } catch (Exception e) {
            return new String(contentAsByteArray, StandardCharsets.UTF_8);
        }
    }
}
