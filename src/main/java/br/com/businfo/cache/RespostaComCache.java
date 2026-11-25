package br.com.businfo.cache;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import br.com.businfo.dto.RespostaPadrao;

/**
 * Monta a resposta das rotas de leitura: usa o cache, marca os cabeçalhos
 * X-Cache (HIT/MISS) e Cache-Control e aplica o envelope opcional
 * (?envelope=true). Se a consulta lançar erro (ex.: 404), nada é guardado.
 */
@Component
public class RespostaComCache {

    private final MemoryCache cache;

    public RespostaComCache(MemoryCache cache) {
        this.cache = cache;
    }

    public <T> ResponseEntity<Object> responder(String chave, String envelope, String mensagem,
                                                Supplier<T> consulta) {
        return responder(chave, envelope, dados -> mensagem, consulta);
    }

    public <T> ResponseEntity<Object> responder(String chave, String envelope, Function<T, String> mensagem,
                                                Supplier<T> consulta) {
        Optional<T> emCache = cache.buscar(chave);
        T dados = emCache.orElseGet(() -> {
            T consultado = consulta.get();
            cache.guardar(chave, consultado);
            return consultado;
        });

        Object corpo = "true".equals(envelope)
                ? new RespostaPadrao<>(200, mensagem.apply(dados), dados)
                : dados;

        return ResponseEntity.ok()
                .header("X-Cache", emCache.isPresent() ? "HIT" : "MISS")
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=60")
                .body(corpo);
    }
}
