package br.com.businfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/** Dados devolvidos por GET /api/v1/status (RNF04). */
@Schema(name = "Status")
public record StatusResponse(
        @Schema(example = "BusInfo API") String servico,
        @Schema(example = "1.1.0") String versao,
        @Schema(example = "99.9%") String disponibilidade,
        @JsonProperty("banco_dados") @Schema(example = "SQLite Conectado (WAL Mode + Índices Ativos)") String bancoDados,
        CacheStats cache,
        Totais totais) {

    public record Totais(
            long linhas,
            long horarios,
            @JsonProperty("pontos_parada") long pontosParada,
            @JsonProperty("avisos_ativos") long avisosAtivos) {
    }
}
