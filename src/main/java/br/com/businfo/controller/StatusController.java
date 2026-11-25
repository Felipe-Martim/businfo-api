package br.com.businfo.controller;

import static br.com.businfo.config.OpenApiConfig.TAG_AVISOS;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.businfo.dto.RespostaPadrao;
import br.com.businfo.dto.StatusResponse;
import br.com.businfo.service.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/status")
@Tag(name = TAG_AVISOS)
public class StatusController {

    private final StatusService service;

    public StatusController(StatusService service) {
        this.service = service;
    }

    @Operation(summary = "Disponibilidade da API, estatísticas de cache e resumo do banco (RNF04)")
    @ApiResponse(responseCode = "200", description = "Status operacional e métricas da API",
            useReturnTypeSchema = true)
    @GetMapping
    public RespostaPadrao<StatusResponse> status() {
        return new RespostaPadrao<>(200, "API BusInfo operacional.", service.consultar());
    }
}
