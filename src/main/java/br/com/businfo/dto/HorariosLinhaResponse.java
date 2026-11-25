package br.com.businfo.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HorariosLinha")
public record HorariosLinhaResponse(
        @Schema(example = "101") String linha,
        @Schema(example = "Centro - Bairro São José") String nome,
        @JsonProperty("dia_semana") @Schema(example = "Todos") String diaSemana,
        List<String> horarios,
        List<HorarioResponse> detalhes) {
}
