package br.com.businfo.controller;

import static br.com.businfo.config.OpenApiConfig.TAG_AVISOS;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.businfo.cache.RespostaComCache;
import br.com.businfo.dto.AvisoResponse;
import br.com.businfo.service.AvisoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/avisos")
@Tag(name = TAG_AVISOS)
public class AvisoController {

    private final AvisoService service;
    private final RespostaComCache respostas;

    public AvisoController(AvisoService service, RespostaComCache respostas) {
        this.service = service;
        this.respostas = respostas;
    }

    @Operation(summary = "Listar alterações ou interrupções ativas nas linhas (RF08)")
    @ApiResponse(responseCode = "200", description = "Lista de avisos operacionais ativos",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AvisoResponse.class))))
    @GetMapping
    public ResponseEntity<Object> listar(
            @Parameter(schema = @Schema(type = "boolean")) @RequestParam(name = "envelope", required = false) String envelope) {
        return respostas.responder("avisos:ativos", envelope, "Avisos e alterações consultados com sucesso.",
                service::listarAtivos);
    }
}
