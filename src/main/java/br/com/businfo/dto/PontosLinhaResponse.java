package br.com.businfo.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PontosLinha")
public record PontosLinhaResponse(
        @Schema(example = "101") String linha,
        @Schema(example = "Centro - Bairro São José") String nome,
        @JsonProperty("pontos_parada") List<PontoParadaResponse> pontosParada) {
}
