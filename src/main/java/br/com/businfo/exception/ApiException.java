package br.com.businfo.exception;

import org.springframework.http.HttpStatus;

/** Erro de negócio com o status HTTP que deve ser devolvido ao cliente. */
public class ApiException extends RuntimeException {

    public static final String ID_INVALIDO = "ID da linha inválido. Informe um número inteiro positivo.";
    public static final String NAO_ENCONTRADO = "Linha ou recurso solicitado não encontrado.";
    public static final String CAMPOS_OBRIGATORIOS = "Os campos numero, nome, origem e destino são obrigatórios.";

    private final HttpStatus status;

    private ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public static ApiException requisicaoInvalida(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message);
    }

    public static ApiException naoEncontrado() {
        return new ApiException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
    }

    public static ApiException conflito(String message) {
        return new ApiException(HttpStatus.CONFLICT, message);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
