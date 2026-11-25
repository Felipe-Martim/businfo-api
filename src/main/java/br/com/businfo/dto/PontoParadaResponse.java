package br.com.businfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.businfo.model.PontoParada;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PontoParada")
public record PontoParadaResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Terminal Central") String nome,
        @Schema(example = "Av. Presidente Vargas, 100 - Centro") String endereco,
        @Schema(example = "1") Integer ordem,
        @JsonProperty("linha_id") @Schema(example = "1") Long linhaId) {

    public static PontoParadaResponse de(PontoParada p) {
        return new PontoParadaResponse(p.getId(), p.getNome(), p.getEndereco(), p.getOrdem(),
                p.getLinha().getId());
    }
}
