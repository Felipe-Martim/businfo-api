package br.com.businfo.cache;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.businfo.dto.CacheStats;

/**
 * Cache em memória com TTL (Time-To-Live) para as rotas de leitura (RNF01):
 * evita consultas repetidas ao SQLite.
 */
@Component
public class MemoryCache {

    private record Entrada(Object valor, long expiraEm) {
    }

    private final Map<String, Entrada> itens = new ConcurrentHashMap<>();
    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();
    private final Duration ttl;

    public MemoryCache(@Value("${businfo.cache.ttl:60s}") Duration ttl) {
        this.ttl = ttl;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> buscar(String chave) {
        Entrada entrada = itens.get(chave);
        if (entrada != null && System.currentTimeMillis() <= entrada.expiraEm()) {
            hits.incrementAndGet();
            return Optional.of((T) entrada.valor());
        }
        if (entrada != null) {
            itens.remove(chave);
        }
        misses.incrementAndGet();
        return Optional.empty();
    }

    public void guardar(String chave, Object valor) {
        itens.put(chave, new Entrada(valor, System.currentTimeMillis() + ttl.toMillis()));
    }

    /** Chamado a cada escrita, para a leitura seguinte já enxergar o dado novo. */
    public void limpar() {
        itens.clear();
        hits.set(0);
        misses.set(0);
    }

    public CacheStats estatisticas() {
        return new CacheStats(itens.size(), hits.get(), misses.get(), ttl.toSeconds());
    }
}
