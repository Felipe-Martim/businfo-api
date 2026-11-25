package br.com.businfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.businfo.model.Aviso;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AvisoOperacional")
public record AvisoResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Alteração de Trajeto") String tipo,
        @Schema(example = "Desvio temporário devido a obras viárias.") String descricao,
        @JsonProperty("data_registro") @Schema(example = "2026-10-18") String dataRegistro,
        @Schema(example = "1") Integer ativo,
        @JsonProperty("linha_id") @Schema(example = "1") Long linhaId,
        @JsonProperty("linha_numero") @Schema(example = "101") String linhaNumero,
        @JsonProperty("linha_nome") @Schema(example = "Centro - Bairro São José") String linhaNome) {

    public static AvisoResponse de(Aviso aviso) {
        return new AvisoResponse(aviso.getId(), aviso.getTipo(), aviso.getDescricao(),
                aviso.getDataRegistro(), aviso.getAtivo(), aviso.getLinha().getId(),
                aviso.getLinha().getNumero(), aviso.getLinha().getNome());
    }
}
