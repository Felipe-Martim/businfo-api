package br.com.businfo.dto;

import java.util.List;

import br.com.businfo.model.Linha;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LinhaDetalhe")
public record LinhaDetalheResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "101") String numero,
        @Schema(example = "Centro - Bairro São José") String nome,
        @Schema(example = "Terminal Central") String origem,
        @Schema(example = "Bairro São José") String destino,
        List<AvisoResumoResponse> avisos) {

    public static LinhaDetalheResponse de(Linha linha, List<AvisoResumoResponse> avisos) {
        return new LinhaDetalheResponse(linha.getId(), linha.getNumero(), linha.getNome(),
                linha.getOrigem(), linha.getDestino(), avisos);
    }
}
