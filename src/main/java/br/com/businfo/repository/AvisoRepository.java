package br.com.businfo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.businfo.model.Aviso;

public interface AvisoRepository extends JpaRepository<Aviso, Long> {

    List<Aviso> findByLinhaIdAndAtivoOrderByIdAsc(Long linhaId, Integer ativo);

    /** Avisos ativos já com os dados da linha, em uma única consulta. */
    @Query("SELECT a FROM Aviso a JOIN FETCH a.linha WHERE a.ativo = 1 ORDER BY a.id ASC")
    List<Aviso> listarAtivos();

    long countByAtivo(Integer ativo);
}
