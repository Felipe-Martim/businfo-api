package br.com.businfo.controller;

import static br.com.businfo.config.OpenApiConfig.TAG_HORARIOS;
import static br.com.businfo.config.OpenApiConfig.TAG_ITINERARIOS;
import static br.com.businfo.config.OpenApiConfig.TAG_LINHAS;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.businfo.cache.MemoryCache;
import br.com.businfo.cache.RespostaComCache;
import br.com.businfo.dto.ErroResponse;
import br.com.businfo.dto.HorariosLinhaResponse;
import br.com.businfo.dto.ItinerarioResponse;
import br.com.businfo.dto.LinhaDetalheResponse;
import br.com.businfo.dto.LinhaResponse;
import br.com.businfo.dto.NovaLinhaRequest;
import br.com.businfo.dto.PontosLinhaResponse;
import br.com.businfo.dto.RespostaPadrao;
import br.com.businfo.exception.ApiException;
import br.com.businfo.service.LinhaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/linhas")
public class LinhaController {

    private static final String ENVELOPE = "Se true, encapsula a resposta no formato { status, message, data }";

    private final LinhaService service;
    private final RespostaComCache respostas;
    private final MemoryCache cache;

    public LinhaController(LinhaService service, RespostaComCache respostas, MemoryCache cache) {
        this.service = service;
        this.respostas = respostas;
        this.cache = cache;
    }

    @Tag(name = TAG_LINHAS)
    @Operation(summary = "Listar todas as linhas de ônibus disponíveis (RF01 / UC01)")
    @ApiResponse(responseCode = "200", description = "Lista de linhas retornada com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LinhaResponse.class))))
    @GetMapping
    public ResponseEntity<Object> listar(
            @Parameter(description = ENVELOPE, schema = @Schema(type = "boolean"))
            @RequestParam(name = "envelope", required = false) String envelope) {
        return respostas.responder("linhas:all", envelope, "Lista de linhas consultada com sucesso.",
                service::listar);
    }

    @Tag(name = TAG_LINHAS)
    @Operation(summary = "Pesquisar linhas pelo número, nome, origem ou destino (RF05 / UC04)")
    @ApiResponse(responseCode = "200", description = "Linhas correspondentes ao termo pesquisado",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LinhaResponse.class))))
    @ApiResponse(responseCode = "400", description = "Parâmetro termo não informado",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @GetMapping("/busca")
    public ResponseEntity<Object> buscar(
            @Parameter(required = true, description = "Número, nome, origem ou destino da linha", example = "101")
            @RequestParam(name = "termo", required = false) String termo,
            @Parameter(description = ENVELOPE, schema = @Schema(type = "boolean"))
            @RequestParam(name = "envelope", required = false) String envelope) {
        String texto = termo == null ? "" : termo.strip();
        if (texto.isEmpty()) {
            throw ApiException.requisicaoInvalida(
                    "O parâmetro de consulta \"termo\" é obrigatório. Exemplo: /api/v1/linhas/busca?termo=101");
        }
        return respostas.<List<LinhaResponse>>responder(
                "linhas:busca:" + texto.toLowerCase(Locale.ROOT), envelope,
                achadas -> "Foram encontradas " + achadas.size() + " linha(s) para o termo \"" + texto + "\".",
                () -> service.buscar(texto));
    }

    @Tag(name = TAG_LINHAS)
    @Operation(summary = "Buscar linha por ID com início, término e avisos ativos (RF01 / RF07 / RF08)")
    @ApiResponse(responseCode = "200", description = "Detalhes da linha encontrados com sucesso",
            content = @Content(schema = @Schema(implementation = LinhaDetalheResponse.class)))
    @ApiResponse(responseCode = "400", description = "ID inválido (não numérico ou menor/igual a zero)",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "404", description = "Linha não encontrada",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<Object> detalhar(
            @Parameter(description = "Identificador da linha (inteiro positivo)", example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = ENVELOPE, schema = @Schema(type = "boolean"))
            @RequestParam(name = "envelope", required = false) String envelope) {
        validarId(id);
        return respostas.responder("linhas:id:" + id, envelope, "Linha encontrada com sucesso.",
                () -> service.detalhar(id));
    }

    @Tag(name = TAG_HORARIOS)
    @Operation(summary = "Consultar os horários de uma linha (RF02 / RF06 / UC02)")
    @ApiResponse(responseCode = "200", description = "Horários da linha retornados com sucesso",
            content = @Content(schema = @Schema(implementation = HorariosLinhaResponse.class)))
    @ApiResponse(responseCode = "400", description = "ID da linha inválido",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "404", description = "Linha não encontrada",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @GetMapping("/{id}/horarios")
    public ResponseEntity<Object> horarios(
            @Parameter(example = "1") @PathVariable("id") Long id,
            @Parameter(name = "dia_semana", description = "Filtra os horários por dia da semana (RF06)",
                    schema = @Schema(type = "string", allowableValues = {"Dias Úteis", "Sábado", "Domingo"}))
            @RequestParam(name = "dia_semana", required = false) String diaSemana,
            @Parameter(hidden = true) @RequestParam(name = "dia", required = false) String dia,
            @Parameter(description = ENVELOPE, schema = @Schema(type = "boolean"))
            @RequestParam(name = "envelope", required = false) String envelope) {
        validarId(id);
        // "dia" é um atalho aceito para "dia_semana".
        String informado = diaSemana != null && !diaSemana.isEmpty() ? diaSemana : dia;
        String filtro = informado == null ? "" : informado.strip();
        return respostas.responder("linhas:horarios:" + id + ":" + filtro.toLowerCase(Locale.ROOT), envelope,
                "Horários da linha consultados com sucesso.",
                () -> service.horarios(id, filtro));
    }

    @Tag(name = TAG_ITINERARIOS)
    @Operation(summary = "Visualizar o itinerário ordenado de uma linha (RF03 / RF04 / UC03)")
    @ApiResponse(responseCode = "200", description = "Itinerário da linha retornado com sucesso",
            content = @Content(schema = @Schema(implementation = ItinerarioResponse.class)))
    @ApiResponse(responseCode = "400", description = "ID da linha inválido",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "404", description = "Linha não encontrada",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @GetMapping("/{id}/itinerario")
    public ResponseEntity<Object> itinerario(
            @Parameter(example = "1") @PathVariable("id") Long id,
            @Parameter(description = ENVELOPE, schema = @Schema(type = "boolean"))
            @RequestParam(name = "envelope", required = false) String envelope) {
        validarId(id);
        return respostas.responder("linhas:itinerario:" + id, envelope,
                "Itinerário da linha consultado com sucesso.",
                () -> service.itinerario(id));
    }

    @Tag(name = TAG_ITINERARIOS)
    @Operation(summary = "Consultar pontos de parada detalhados com endereço (RF04)")
    @ApiResponse(responseCode = "200", description = "Lista detalhada de pontos de parada da linha",
            content = @Content(schema = @Schema(implementation = PontosLinhaResponse.class)))
    @ApiResponse(responseCode = "400", description = "ID da linha inválido",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "404", description = "Linha não encontrada",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @GetMapping("/{id}/pontos")
    public ResponseEntity<Object> pontos(
            @Parameter(example = "1") @PathVariable("id") Long id,
            @Parameter(description = ENVELOPE, schema = @Schema(type = "boolean"))
            @RequestParam(name = "envelope", required = false) String envelope) {
        validarId(id);
        return respostas.responder("linhas:pontos:" + id, envelope, "Pontos de parada consultados com sucesso.",
                () -> service.pontos(id));
    }

    @Tag(name = TAG_LINHAS)
    @Operation(summary = "Cadastrar uma nova linha de ônibus (invalida o cache automaticamente)")
    @ApiResponse(responseCode = "201", description = "Linha cadastrada com sucesso")
    @ApiResponse(responseCode = "400", description = "Campos obrigatórios ausentes ou JSON inválido",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "409", description = "Conflito: já existe linha com o mesmo número",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            content = @Content(schema = @Schema(implementation = NovaLinhaRequest.class)))
    @PostMapping
    public ResponseEntity<RespostaPadrao<LinhaResponse>> cadastrar(
            @Valid @RequestBody(required = false) NovaLinhaRequest nova) {
        if (nova == null) {
            throw ApiException.requisicaoInvalida(ApiException.CAMPOS_OBRIGATORIOS);
        }
        LinhaResponse criada = service.cadastrar(nova);
        cache.limpar();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RespostaPadrao<>(201, "Linha cadastrada com sucesso.", criada));
    }

    private void validarId(Long id) {
        if (id <= 0) {
            throw ApiException.requisicaoInvalida(ApiException.ID_INVALIDO);
        }
    }
}
