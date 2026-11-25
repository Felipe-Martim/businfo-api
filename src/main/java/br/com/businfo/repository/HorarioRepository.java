package br.com.businfo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.businfo.model.Horario;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

    /** Todos os horários, agrupados por Dias Úteis, Sábado e Domingo (RF02). */
    @Query("""
            SELECT h FROM Horario h
            WHERE h.linha.id = :linhaId
            ORDER BY CASE h.diaSemana
                       WHEN 'Dias Úteis' THEN 1
                       WHEN 'Sábado' THEN 2
                       WHEN 'Domingo' THEN 3
                       ELSE 4
                     END, h.horario ASC
            """)
    List<Horario> listarPorLinha(@Param("linhaId") Long linhaId);

    /** Filtro por dia da semana, aceitando parte do texto (RF06). */
    @Query("""
            SELECT h FROM Horario h
            WHERE h.linha.id = :linhaId AND LOWER(h.diaSemana) LIKE LOWER(CONCAT('%', :dia, '%'))
            ORDER BY h.horario ASC
            """)
    List<Horario> listarPorLinhaEDia(@Param("linhaId") Long linhaId, @Param("dia") String dia);
}
