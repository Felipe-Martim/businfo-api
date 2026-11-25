package br.com.businfo.dto;

import br.com.businfo.model.Linha;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Linha")
public record LinhaResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "101") String numero,
        @Schema(example = "Centro - Bairro São José") String nome,
        @Schema(example = "Terminal Central") String origem,
        @Schema(example = "Bairro São José") String destino) {

    public static LinhaResponse de(Linha linha) {
        return new LinhaResponse(linha.getId(), linha.getNumero(), linha.getNome(),
                linha.getOrigem(), linha.getDestino());
    }
}
