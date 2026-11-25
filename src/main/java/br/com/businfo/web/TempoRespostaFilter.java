package br.com.businfo.web;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Marca o início de cada requisição para o cabeçalho X-Response-Time (RNF01). */
@Component
public class TempoRespostaFilter extends OncePerRequestFilter {

    static final String INICIO = "businfo.inicioNanos";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        request.setAttribute(INICIO, System.nanoTime());
        if (request.getRequestURI().equals("/api/v1/openapi.json")) {
            response.setHeader(HttpHeaders.CACHE_CONTROL, "public, max-age=300");
        }
        chain.doFilter(request, response);
    }
}
