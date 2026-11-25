package br.com.businfo.dto;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import io.swagger.v3.oas.annotations.media.Schema;

/** Estrutura única das respostas de erro. */
@Schema(name = "ErroPadrao")
public record ErroResponse(
        @Schema(example = "404") int status,
        @Schema(example = "Linha ou recurso solicitado não encontrado.") String message,
        @Schema(nullable = true) Object data,
        @Schema(format = "date-time", example = "2026-11-25T19:40:00.000Z") String timestamp) {

    private static final DateTimeFormatter ISO_MILISSEGUNDOS =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);

    public static ErroResponse de(int status, String message) {
        return new ErroResponse(status, message, null, ISO_MILISSEGUNDOS.format(Instant.now()));
    }
}
