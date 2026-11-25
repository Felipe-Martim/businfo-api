package br.com.businfo.config;

import java.util.List;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;

/** Dados gerais da especificação OpenAPI. Os detalhes de cada rota ficam nos controllers. */
@Configuration
public class OpenApiConfig {

    public static final String TAG_LINHAS = "Linhas";
    public static final String TAG_HORARIOS = "Horários";
    public static final String TAG_ITINERARIOS = "Itinerários e Pontos";
    public static final String TAG_AVISOS = "Avisos e Status";

    @Bean
    public OpenAPI busInfoOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("BusInfo API – Horários e Itinerários de Ônibus")
                        .version("1.1.0")
                        .description("API RESTful para consulta pública de linhas de ônibus, horários de saída, "
                                + "itinerários, pontos de parada e avisos operacionais no transporte coletivo urbano. "
                                + "Inclui cache em memória (X-Cache), telemetria de desempenho (X-Response-Time) "
                                + "e tratamento padronizado de erros HTTP (400, 404, 409 e 500).")
                        .contact(new Contact().name("Equipe BusInfo (Felipe Martim, Gabriel Oliveira, "
                                + "João Carula, Marcelo Motta, Paulo Henrique, Cauan Gutierre)")))
                .tags(List.of(
                        new Tag().name(TAG_LINHAS)
                                .description("Consulta, pesquisa e cadastro de linhas (UC01, UC04, RF01, RF05, RF07)"),
                        new Tag().name(TAG_HORARIOS)
                                .description("Horários de saída e filtro por dia da semana (UC02, RF02, RF06)"),
                        new Tag().name(TAG_ITINERARIOS)
                                .description("Trajetos ordenados e pontos de parada (UC03, RF03, RF04)"),
                        new Tag().name(TAG_AVISOS)
                                .description("Alterações operacionais e telemetria do sistema (RF08, RNF04)")));
    }

    /** O springdoc gera "3.0.1"; o projeto publica o contrato como OpenAPI 3.0.3. */
    @Bean
    public OpenApiCustomizer versaoOpenApi() {
        return openApi -> openApi.setOpenapi("3.0.3");
    }
}
