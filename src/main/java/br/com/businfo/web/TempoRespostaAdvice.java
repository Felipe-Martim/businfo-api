package br.com.businfo.web;

import java.util.Locale;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Grava X-Response-Time imediatamente antes de o corpo ser escrito
 * (depois disso os cabeçalhos já não podem mais ser alterados).
 */
@ControllerAdvice
public class TempoRespostaAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (request instanceof ServletServerHttpRequest servlet
                && servlet.getServletRequest().getAttribute(TempoRespostaFilter.INICIO) instanceof Long inicio) {
            double milissegundos = (System.nanoTime() - inicio) / 1_000_000.0;
            // Locale.ROOT: ponto decimal mesmo em um Windows configurado em pt-BR.
            response.getHeaders().set("X-Response-Time", String.format(Locale.ROOT, "%.2fms", milissegundos));
        }
        return body;
    }
}
