# 🚌 BusInfo – Horários e Itinerários de Ônibus (API v1.1)

**Entrega Final – Validação, Tratamento de Erros, Desempenho e Documentação Publicada**

**Integrantes:**
- Felipe Martim
- Gabriel Oliveira
- João Carula
- Marcelo Motta
- Paulo Henrique
- Cauan Gutierre

---

## 📌 Sobre o Projeto

O **BusInfo** é uma API REST para consulta de informações do transporte coletivo urbano: **linhas de ônibus**, **horários de saída**, **itinerários**, **pontos de parada** e **avisos de alteração nas linhas**.

**Tecnologias:** Java 17+ · Spring Boot 4.1 · Spring Web MVC · Spring Data JPA (Hibernate) · Bean Validation · SQLite · springdoc-openapi (Swagger UI) · JUnit · Maven.

---

## ▶ Como executar no IntelliJ IDEA

1. **File → Open** e selecione a pasta `businfo-api` (a que contém o `pom.xml`). Se perguntar, escolha **Trust Project**.
2. Aguarde o IntelliJ baixar as dependências do Maven (só na primeira vez, precisa de internet).
3. Em **File → Project Structure → Project → SDK**, escolha um JDK **17 ou superior**. Se não houver nenhum: **Add SDK → Download JDK** (por exemplo, *Eclipse Temurin 21*).
4. No canto superior direito, selecione a configuração **BusInfo API** e clique em ▶.
   (Alternativa: abrir `BusInfoApplication.java` e clicar no ▶ ao lado do método `main`.)
5. Quando o console mostrar `BusInfo API v1.1 pronta`, abra <http://localhost:3000>.

Para rodar os testes: botão direito em `src/test/java` → **Run 'All Tests'**.

## ▶ Como executar pelo terminal (PowerShell)

Exige o JDK 17+ instalado (variável `JAVA_HOME` ou `java` no PATH). O Maven não precisa ser instalado.

```powershell
.\mvnw.cmd test              # os 16 testes
.\mvnw.cmd spring-boot:run   # sobe a API na porta 3000
```

| O que | Endereço |
| :--- | :--- |
| Demonstração funcional (interface web + console da API) | <http://localhost:3000> |
| Swagger UI (documentação publicada) | <http://localhost:3000/api-docs> |
| Especificação OpenAPI 3.0.3 (JSON) | <http://localhost:3000/api/v1/openapi.json> ou `/openapi.json` |

O banco fica em `database/businfo.db`. Se o arquivo não existir, é criado com `schema.sql` + `seed.sql`.
Para **apagar e recriar** os dados: `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--businfo.database.reset=true"`.
Porta ocupada? Defina a variável de ambiente `PORT`.

---

## 🔗 Endpoints (`/api/v1`)

| Método | Endpoint | Descrição | Códigos HTTP | Requisitos |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/linhas` | Lista todas as linhas | `200` | `RF01`, `UC01` |
| `GET` | `/api/v1/linhas/{id}` | Detalhes da linha: origem, destino e avisos ativos | `200`, `400`, `404` | `RF01`, `RF07`, `RF08` |
| `GET` | `/api/v1/linhas/{id}/horarios` | Horários de saída (filtro `?dia_semana=Dias Úteis`, `Sábado`, `Domingo`) | `200`, `400`, `404` | `RF02`, `RF06`, `UC02` |
| `GET` | `/api/v1/linhas/{id}/itinerario` | Itinerário ordenado e pontos de parada | `200`, `400`, `404` | `RF03`, `RF04`, `UC03` |
| `GET` | `/api/v1/linhas/{id}/pontos` | Pontos de parada com endereço | `200`, `400`, `404` | `RF04` |
| `GET` | `/api/v1/linhas/busca?termo={termo}` | Pesquisa por número, nome, origem ou destino | `200`, `400` | `RF05`, `UC04` |
| `GET` | `/api/v1/avisos` | Avisos ativos de alteração ou interrupção | `200` | `RF08` |
| `POST` | `/api/v1/linhas` | Cadastra uma linha e invalida o cache | `201`, `400`, `409` | Persistência |
| `GET` | `/api/v1/status` | Disponibilidade, totais do banco e estatísticas do cache | `200` | `RNF04` |

Nas rotas `GET`, `?envelope=true` devolve a resposta no formato `{ status, message, data }`.

Erros seguem sempre a mesma estrutura:

```json
{ "status": 404, "message": "Linha ou recurso solicitado não encontrado.", "data": null, "timestamp": "2026-11-25T23:17:19.835Z" }
```

---

## 🧱 Estrutura

```text
src/main/java/br/com/businfo
├── controller/   rotas REST e anotações do Swagger
├── service/      regras de negócio
├── repository/   consultas Spring Data JPA
├── model/        entidades: Linha, Horario, PontoParada, Aviso
├── dto/          formato das requisições e respostas
├── exception/    tratamento centralizado de erros
├── cache/        cache em memória (TTL 60s, X-Cache HIT/MISS)
├── web/          cabeçalho X-Response-Time
└── config/       banco de dados, OpenAPI e CORS
src/main/resources
├── application.properties
├── db/           schema.sql e seed.sql
└── static/       interface web de demonstração
src/test/java     16 testes de integração
```

---

## ⚡ Desempenho

- Cache em memória com TTL de 60s nas rotas de leitura, com cabeçalhos `X-Cache` e `Cache-Control`; é limpo a cada cadastro.
- SQLite em modo **WAL**, com **6 índices**.
- Cabeçalho `X-Response-Time` nas respostas da API.

Medido em 25/11/2026 em uma máquina Windows, em 3 inicializações do zero (valores de `X-Response-Time`):

| Situação | Mediana | 95% abaixo de | Máximo |
| :--- | :--- | :--- | :--- |
| Consulta ao SQLite (cache MISS) | 3 ms | 14 ms | 54 ms |
| Resposta pelo cache (HIT) | 0,6 ms | 1 ms | 2,5 ms |
| 1ª requisição após ligar o servidor (aquecimento da JVM) | — | — | 207 a 221 ms |

Todos abaixo da meta do `RNF01` (2000 ms).
