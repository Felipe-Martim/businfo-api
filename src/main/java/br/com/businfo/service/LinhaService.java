package br.com.businfo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.businfo.dto.AvisoResumoResponse;
import br.com.businfo.dto.HorarioResponse;
import br.com.businfo.dto.HorariosLinhaResponse;
import br.com.businfo.dto.ItinerarioResponse;
import br.com.businfo.dto.LinhaDetalheResponse;
import br.com.businfo.dto.LinhaResponse;
import br.com.businfo.dto.NovaLinhaRequest;
import br.com.businfo.dto.PontoParadaResponse;
import br.com.businfo.dto.PontosLinhaResponse;
import br.com.businfo.exception.ApiException;
import br.com.businfo.model.Linha;
import br.com.businfo.repository.AvisoRepository;
import br.com.businfo.repository.HorarioRepository;
import br.com.businfo.repository.LinhaRepository;
import br.com.businfo.repository.PontoParadaRepository;

@Service
public class LinhaService {

    private final LinhaRepository linhas;
    private final HorarioRepository horarios;
    private final PontoParadaRepository pontos;
    private final AvisoRepository avisos;

    public LinhaService(LinhaRepository linhas, HorarioRepository horarios,
                        PontoParadaRepository pontos, AvisoRepository avisos) {
        this.linhas = linhas;
        this.horarios = horarios;
        this.pontos = pontos;
        this.avisos = avisos;
    }

    @Transactional(readOnly = true)
    public List<LinhaResponse> listar() {
        return linhas.findAllByOrderByNumeroAsc().stream().map(LinhaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<LinhaResponse> buscar(String termo) {
        return linhas.buscarPorTermo(termo).stream().map(LinhaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public LinhaDetalheResponse detalhar(Long id) {
        Linha linha = obter(id);
        List<AvisoResumoResponse> ativos = avisos.findByLinhaIdAndAtivoOrderByIdAsc(id, 1).stream()
                .map(AvisoResumoResponse::de)
                .toList();
        return LinhaDetalheResponse.de(linha, ativos);
    }

    @Transactional(readOnly = true)
    public HorariosLinhaResponse horarios(Long id, String dia) {
        Linha linha = obter(id);
        List<HorarioResponse> detalhes = (dia.isEmpty()
                ? horarios.listarPorLinha(id)
                : horarios.listarPorLinhaEDia(id, dia))
                .stream().map(HorarioResponse::de).toList();
        return new HorariosLinhaResponse(linha.getNumero(), linha.getNome(),
                dia.isEmpty() ? "Todos" : dia,
                detalhes.stream().map(HorarioResponse::horario).toList(),
                detalhes);
    }

    @Transactional(readOnly = true)
    public ItinerarioResponse itinerario(Long id) {
        Linha linha = obter(id);
        List<PontoParadaResponse> paradas = paradasDa(id);
        return new ItinerarioResponse(linha.getNumero(), linha.getNome(), linha.getOrigem(),
                linha.getDestino(),
                paradas.stream().map(PontoParadaResponse::nome).toList(),
                paradas);
    }

    @Transactional(readOnly = true)
    public PontosLinhaResponse pontos(Long id) {
        Linha linha = obter(id);
        return new PontosLinhaResponse(linha.getNumero(), linha.getNome(), paradasDa(id));
    }

    /**
     * synchronized: a verificação de duplicidade e a inclusão acontecem sem que outra
     * requisição entre no meio, o que garante o 409 em vez de um erro do banco.
     */
    public synchronized LinhaResponse cadastrar(NovaLinhaRequest nova) {
        String numero = nova.numero().strip();
        if (linhas.existsByNumero(numero)) {
            throw ApiException.conflito("Já existe uma linha cadastrada com o número \"" + nova.numero() + "\".");
        }
        Linha salva = linhas.save(new Linha(numero, nova.nome().strip(), nova.origem().strip(),
                nova.destino().strip()));
        return LinhaResponse.de(salva);
    }

    private Linha obter(Long id) {
        return linhas.findById(id).orElseThrow(ApiException::naoEncontrado);
    }

    private List<PontoParadaResponse> paradasDa(Long linhaId) {
        return pontos.findByLinhaIdOrderByOrdemAscIdAsc(linhaId).stream()
                .map(PontoParadaResponse::de)
                .toList();
    }
}
