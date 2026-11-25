package br.com.businfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.businfo.model.Aviso;
import io.swagger.v3.oas.annotations.media.Schema;

/** Aviso exibido dentro do detalhe de uma linha. */
@Schema(name = "AvisoResumo")
public record AvisoResumoResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Alteração de Trajeto") String tipo,
        @Schema(example = "Desvio temporário devido a obras viárias.") String descricao,
        @JsonProperty("data_registro") @Schema(example = "2026-10-18") String dataRegistro,
        @Schema(example = "1") Integer ativo) {

    public static AvisoResumoResponse de(Aviso aviso) {
        return new AvisoResumoResponse(aviso.getId(), aviso.getTipo(), aviso.getDescricao(),
                aviso.getDataRegistro(), aviso.getAtivo());
    }
}
