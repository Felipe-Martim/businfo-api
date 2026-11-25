package br.com.businfo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.businfo.model.PontoParada;

public interface PontoParadaRepository extends JpaRepository<PontoParada, Long> {

    List<PontoParada> findByLinhaIdOrderByOrdemAscIdAsc(Long linhaId);
}
