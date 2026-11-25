package br.com.businfo;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Testes de integração: sobem a aplicação em uma porta aleatória e chamam os
 * endpoints por HTTP. Usam um banco próprio (target/), recriado a cada execução,
 * para não alterar o banco da demonstração.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"businfo.database.path=target/businfo-test.db", "businfo.database.reset=true"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("BusInfo API v1.1 - Endpoints, erros, desempenho e OpenAPI")
class BusInfoApiTest {

    private final HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    private final JsonMapper json = JsonMapper.builder().build();

    @Value("${local.server.port}")
    private int porta;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @Order(1)
    @DisplayName("1. Banco de dados: tabelas populadas, 6 índices e modo WAL")
    void cargaInicialEIndices() {
        assertThat(contar("SELECT COUNT(*) FROM linhas")).isEqualTo(6);
        assertThat(contar("SELECT COUNT(*) FROM horarios")).isEqualTo(75);
        assertThat(contar("SELECT COUNT(*) FROM pontos_parada")).isEqualTo(27);
        assertThat(contar("SELECT COUNT(*) FROM avisos")).isEqualTo(6);
        assertThat(contar("SELECT COUNT(*) FROM sqlite_master WHERE type = 'index' AND name LIKE 'idx_%'"))
                .isEqualTo(6);
        assertThat(jdbc.queryForObject("PRAGMA journal_mode", String.class)).isEqualTo("wal");
    }

    @Test
    @Order(2)
    @DisplayName("2. UC01 / RF01 / RNF01: GET /api/v1/linhas responde em menos de 2s com X-Response-Time")
    void listarLinhas() throws Exception {
        long inicio = System.nanoTime();
        HttpResponse<String> res = get("/api/v1/linhas");
        Duration tempo = Duration.ofNanos(System.nanoTime() - inicio);

        assertThat(res.statusCode()).isEqualTo(200);
        assertThat(cabecalho(res, "content-type")).startsWith("application/json");
        assertThat(cabecalho(res, "x-response-time")).matches("\\d+\\.\\d{2}ms");
        assertThat(tempo).isLessThan(Duration.ofSeconds(2));
        JsonNode linhas = corpo(res);
        assertThat(linhas.isArray()).isTrue();
        assertThat(linhas.get(0).get("numero").asString()).isEqualTo("101");
        assertThat(linhas.get(0).get("nome").asString()).isEqualTo("Centro - Bairro São José");
    }

    @Test
    @Order(3)
    @DisplayName("3. Desempenho / Cache: segunda consulta em GET /api/v1/linhas devolve X-Cache: HIT")
    void leituraEmCache() throws Exception {
        HttpResponse<String> res = get("/api/v1/linhas");

        assertThat(res.statusCode()).isEqualTo(200);
        assertThat(cabecalho(res, "x-cache")).isEqualTo("HIT");
    }

    @Test
    @Order(4)
    @DisplayName("4. RF01 / RF07 / RF08: GET /api/v1/linhas/1 traz origem, destino e avisos")
    void detalharLinha() throws Exception {
        HttpResponse<String> res = get("/api/v1/linhas/1");
        JsonNode linha = corpo(res);

        assertThat(res.statusCode()).isEqualTo(200);
        assertThat(linha.get("id").asInt()).isEqualTo(1);
        assertThat(linha.get("numero").asString()).isEqualTo("101");
        assertThat(linha.get("origem").asString()).isEqualTo("Terminal Central");
        assertThat(linha.get("destino").asString()).isEqualTo("Bairro São José");
        assertThat(linha.get("avisos").isArray()).isTrue();
    }

    @Test
    @Order(5)
    @DisplayName("5. UC02 / RF02: GET /api/v1/linhas/1/horarios devolve os horários da linha 101")
    void consultarHorarios() throws Exception {
        HttpResponse<String> res = get("/api/v1/linhas/1/horarios");
        JsonNode corpo = corpo(res);

        assertThat(res.statusCode()).isEqualTo(200);
        assertThat(corpo.get("linha").asString()).isEqualTo("101");
        assertThat(corpo.get("horarios").valueStream().map(JsonNode::asString))
                .contains("06:00", "07:00", "08:00", "09:00");
    }

    @Test
    @Order(6)
    @DisplayName("6. RF06: filtro ?dia_semana=Sábado devolve apenas horários de sábado")
    void filtrarHorariosPorDia() throws Exception {
        HttpResponse<String> res = get("/api/v1/linhas/1/horarios?dia_semana="
                + URLEncoder.encode("Sábado", StandardCharsets.UTF_8));
        JsonNode corpo = corpo(res);

        assertThat(res.statusCode()).isEqualTo(200);
        assertThat(corpo.get("detalhes").size()).isEqualTo(5);
        assertThat(corpo.get("detalhes").valueStream().map(h -> h.get("dia_semana").asString()))
                .containsOnly("Sábado");
    }

    @Test
    @Order(7)
    @DisplayName("7. UC03 / RF03 / RF04: GET /api/v1/linhas/1/itinerario devolve os pontos em ordem")
    void consultarItinerario() throws Exception {
        HttpResponse<String> res = get("/api/v1/linhas/1/itinerario");
        JsonNode corpo = corpo(res);

        assertThat(res.statusCode()).isEqualTo(200);
        assertThat(corpo.get("pontos").valueStream().map(JsonNode::asString))
                .containsExactly("Terminal Central", "Praça da Matriz", "Escola Municipal", "Bairro São José");
        assertThat(corpo.get("pontos_parada").size()).isEqualTo(4);
    }

    @Test
    @Order(8)
    @DisplayName("8. RF04: GET /api/v1/linhas/1/pontos devolve os pontos com endereço")
    void consultarPontos() throws Exception {
        HttpResponse<String> res = get("/api/v1/linhas/1/pontos");
        JsonNode corpo = corpo(res);

        assertThat(res.statusCode()).isEqualTo(200);
        assertThat(corpo.get("linha").asString()).isEqualTo("101");
        assertThat(corpo.get("pontos_parada").size()).isEqualTo(4);
        assertThat(corpo.get("pontos_parada").get(0).get("endereco").asString()).isNotBlank();
    }

    @Test
    @Order(9)
    @DisplayName("9. UC04 / RF05: GET /api/v1/linhas/busca?termo=101 pesquisa por número ou nome")
    void pesquisarLinha() throws Exception {
        HttpResponse<String> res = get("/api/v1/linhas/busca?termo=101");
        JsonNode corpo = corpo(res);

        assertThat(res.statusCode()).isEqualTo(200);
        assertThat(corpo.size()).isEqualTo(1);
        assertThat(corpo.get(0).get("nome").asString()).isEqualTo("Centro - Bairro São José");
    }

    @Test
    @Order(10)
    @DisplayName("10. RF08: GET /api/v1/avisos lista alterações e interrupções")
    void listarAvisos() throws Exception {
        HttpResponse<String> res = get("/api/v1/avisos");
        JsonNode corpo = corpo(res);

        assertThat(res.statusCode()).isEqualTo(200);
        assertThat(corpo.size()).isEqualTo(6);
        assertThat(corpo.get(0).get("tipo").asString()).isNotBlank();
        assertThat(corpo.get(0).get("linha_numero").asString()).isEqualTo("101");
    }

    @Test
    @Order(11)
    @DisplayName("11. RNF04: GET /api/v1/status devolve métricas do banco e do cache")
    void consultarStatus() throws Exception {
        HttpResponse<String> res = get("/api/v1/status");
        JsonNode corpo = corpo(res);

        assertThat(res.statusCode()).isEqualTo(200);
        assertThat(corpo.get("status").asInt()).isEqualTo(200);
        assertThat(corpo.get("data").get("cache").get("ttl_seconds").asInt()).isEqualTo(60);
        assertThat(corpo.get("data").get("totais").get("linhas").asInt()).isEqualTo(6);
    }

    @Test
    @Order(12)
    @DisplayName("12. Persistência: POST /api/v1/linhas cadastra (201) e invalida o cache")
    void cadastrarLinha() throws Exception {
        HttpResponse<String> res = post("""
                {"numero":"702","nome":"Terminal Central - Polo Tecnológico",
                 "origem":"Terminal Central","destino":"Polo Tecnológico"}""");

        assertThat(res.statusCode()).isEqualTo(201);
        assertThat(corpo(res).get("data").get("numero").asString()).isEqualTo("702");

        HttpResponse<String> lista = get("/api/v1/linhas");
        assertThat(cabecalho(lista, "x-cache")).isEqualTo("MISS");
        assertThat(corpo(lista).valueStream().map(l -> l.get("numero").asString())).contains("702");
    }

    @Test
    @Order(13)
    @DisplayName("13. Erro 409: número de linha duplicado")
    void impedirLinhaDuplicada() throws Exception {
        HttpResponse<String> res = post("""
                {"numero":"101","nome":"Duplicada","origem":"A","destino":"B"}""");

        assertThat(res.statusCode()).isEqualTo(409);
        assertThat(corpo(res).get("status").asInt()).isEqualTo(409);
        assertThat(corpo(res).get("timestamp").asString()).isNotBlank();
    }

    @Test
    @Order(14)
    @DisplayName("14. Erro 400: ID inválido, busca sem termo, JSON malformado e campos em branco")
    void requisicoesInvalidas() throws Exception {
        for (HttpResponse<String> res : new HttpResponse[] {
                get("/api/v1/linhas/abc"),
                get("/api/v1/linhas/0"),
                get("/api/v1/linhas/busca"),
                post("{\"numero\": \"999\", invalido}"),
                post("{\"numero\":\"998\"}"),
                post("{\"numero\":\"  \",\"nome\":\"  \",\"origem\":\"  \",\"destino\":\"  \"}")}) {
            assertThat(res.statusCode()).as(res.request().method() + " " + res.uri()).isEqualTo(400);
            assertThat(corpo(res).get("status").asInt()).isEqualTo(400);
        }
    }

    @Test
    @Order(15)
    @DisplayName("15. Erro 404: linha ou endpoint inexistente")
    void recursoInexistente() throws Exception {
        HttpResponse<String> res = get("/api/v1/linhas/999");
        JsonNode corpo = corpo(res);

        assertThat(res.statusCode()).isEqualTo(404);
        assertThat(corpo.get("message").asString()).isEqualTo("Linha ou recurso solicitado não encontrado.");
        assertThat(corpo.get("data").isNull()).isTrue();
        assertThat(corpo.get("timestamp").asString()).isNotBlank();

        HttpResponse<String> rota = get("/api/v1/rota-inexistente");
        assertThat(rota.statusCode()).isEqualTo(404);
        assertThat(corpo(rota).get("message").asString())
                .isEqualTo("Endpoint não encontrado: GET /api/v1/rota-inexistente");
    }

    @Test
    @Order(16)
    @DisplayName("16. RNF05: especificação OpenAPI 3.0.3 e Swagger UI publicados")
    void documentacaoPublicada() throws Exception {
        for (String caminho : new String[] {"/api/v1/openapi.json", "/openapi.json"}) {
            HttpResponse<String> res = get(caminho);
            JsonNode spec = corpo(res);

            assertThat(res.statusCode()).isEqualTo(200);
            assertThat(spec.get("openapi").asString()).isEqualTo("3.0.3");
            assertThat(spec.get("components").get("schemas").has("Linha")).isTrue();
            assertThat(spec.get("components").get("schemas").has("ErroPadrao")).isTrue();
            assertThat(spec.get("paths").propertyNames()).contains(
                    "/api/v1/linhas", "/api/v1/linhas/{id}/horarios", "/api/v1/linhas/{id}/itinerario");
        }

        HttpResponse<String> ui = get("/api-docs");
        assertThat(ui.statusCode()).isEqualTo(200);
        assertThat(cabecalho(ui, "content-type")).startsWith("text/html");
    }

    private int contar(String sql) {
        return jdbc.queryForObject(sql, Integer.class);
    }

    private HttpResponse<String> get(String caminho) throws IOException, InterruptedException {
        return enviar(HttpRequest.newBuilder(uri(caminho)).GET());
    }

    private HttpResponse<String> post(String corpoJson) throws IOException, InterruptedException {
        return enviar(HttpRequest.newBuilder(uri("/api/v1/linhas"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(corpoJson, StandardCharsets.UTF_8)));
    }

    private HttpResponse<String> enviar(HttpRequest.Builder requisicao) throws IOException, InterruptedException {
        return client.send(requisicao.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private URI uri(String caminho) {
        return URI.create("http://localhost:" + porta + caminho);
    }

    private String cabecalho(HttpResponse<String> res, String nome) {
        return res.headers().firstValue(nome).orElse("");
    }

    private JsonNode corpo(HttpResponse<String> res) {
        return json.readTree(res.body());
    }
}
