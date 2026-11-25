package br.com.businfo.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Itinerario")
public record ItinerarioResponse(
        @Schema(example = "101") String linha,
        @Schema(example = "Centro - Bairro São José") String nome,
        @Schema(example = "Terminal Central") String origem,
        @Schema(example = "Bairro São José") String destino,
        List<String> pontos,
        @JsonProperty("pontos_parada") List<PontoParadaResponse> pontosParada) {
}
