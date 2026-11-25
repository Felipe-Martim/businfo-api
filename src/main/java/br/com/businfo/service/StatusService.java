package br.com.businfo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.businfo.cache.MemoryCache;
import br.com.businfo.dto.StatusResponse;
import br.com.businfo.repository.AvisoRepository;
import br.com.businfo.repository.HorarioRepository;
import br.com.businfo.repository.LinhaRepository;
import br.com.businfo.repository.PontoParadaRepository;

@Service
public class StatusService {

    private final LinhaRepository linhas;
    private final HorarioRepository horarios;
    private final PontoParadaRepository pontos;
    private final AvisoRepository avisos;
    private final MemoryCache cache;

    public StatusService(LinhaRepository linhas, HorarioRepository horarios,
                         PontoParadaRepository pontos, AvisoRepository avisos, MemoryCache cache) {
        this.linhas = linhas;
        this.horarios = horarios;
        this.pontos = pontos;
        this.avisos = avisos;
        this.cache = cache;
    }

    @Transactional(readOnly = true)
    public StatusResponse consultar() {
        return new StatusResponse(
                "BusInfo API",
                "1.1.0",
                "99.9%",
                "SQLite Conectado (WAL Mode + Índices Ativos)",
                cache.estatisticas(),
                new StatusResponse.Totais(linhas.count(), horarios.count(), pontos.count(),
                        avisos.countByAtivo(1)));
    }
}
