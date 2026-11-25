package br.com.businfo.exception;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.com.businfo.dto.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;

/** Tratamento centralizado: toda falha vira { status, message, data, timestamp }. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final MediaType JSON_UTF8 =
            new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErroResponse> erroDeNegocio(ApiException ex) {
        return erro(ex.getStatus(), ex.getMessage());
    }

    /** O único parâmetro tipado da API é o {id} numérico. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> idNaoNumerico() {
        return erro(HttpStatusCode.valueOf(400), ApiException.ID_INVALIDO);
    }

    /** Campos ausentes ou em branco, ou corpo que não foi enviado como JSON. */
    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ErroResponse> camposObrigatorios() {
        return erro(HttpStatusCode.valueOf(400), ApiException.CAMPOS_OBRIGATORIOS);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> jsonInvalido() {
        return erro(HttpStatusCode.valueOf(400), "Corpo da requisição possui JSON inválido ou malformado.");
    }

    /** Rota ou método inexistente dentro de /api. */
    @ExceptionHandler({NoResourceFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErroResponse> endpointNaoEncontrado(HttpServletRequest request) {
        String caminho = request.getRequestURI();
        if (!caminho.equals("/api") && !caminho.startsWith("/api/")) {
            return ResponseEntity.notFound().build();
        }
        String query = request.getQueryString();
        String url = query == null ? caminho : caminho + "?" + query;
        return erro(HttpStatusCode.valueOf(404), "Endpoint não encontrado: " + request.getMethod() + " " + url);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> erroInterno(Exception ex) {
        // Exceções do próprio Spring MVC já carregam o status correto (ex.: 406).
        if (ex instanceof ErrorResponse doSpring) {
            return erro(doSpring.getStatusCode(), "Requisição não pôde ser processada.");
        }
        log.error("Erro interno da API", ex);
        return erro(HttpStatusCode.valueOf(500), "Erro interno do servidor ao processar a requisição.");
    }

    private ResponseEntity<ErroResponse> erro(HttpStatusCode status, String message) {
        return ResponseEntity.status(status)
                .contentType(JSON_UTF8)
                .body(ErroResponse.de(status.value(), message));
    }
}
