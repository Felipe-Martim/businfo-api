package br.com.businfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "NovaLinha")
public record NovaLinhaRequest(
        @NotBlank @Schema(example = "601") String numero,
        @NotBlank @Schema(example = "Centro - Aeroporto Municipal") String nome,
        @NotBlank @Schema(example = "Terminal Central") String origem,
        @NotBlank @Schema(example = "Aeroporto Municipal") String destino) {
}
