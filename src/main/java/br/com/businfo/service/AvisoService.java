package br.com.businfo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.businfo.dto.AvisoResponse;
import br.com.businfo.repository.AvisoRepository;

@Service
public class AvisoService {

    private final AvisoRepository avisos;

    public AvisoService(AvisoRepository avisos) {
        this.avisos = avisos;
    }

    @Transactional(readOnly = true)
    public List<AvisoResponse> listarAtivos() {
        return avisos.listarAtivos().stream().map(AvisoResponse::de).toList();
    }
}
