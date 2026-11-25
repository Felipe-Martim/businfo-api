package br.com.businfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.businfo.model.Horario;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Horario")
public record HorarioResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "06:00") String horario,
        @JsonProperty("dia_semana") @Schema(example = "Dias Úteis") String diaSemana,
        @JsonProperty("linha_id") @Schema(example = "1") Long linhaId) {

    public static HorarioResponse de(Horario h) {
        return new HorarioResponse(h.getId(), h.getHorario(), h.getDiaSemana(), h.getLinha().getId());
    }
}
