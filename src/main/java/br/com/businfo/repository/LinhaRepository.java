package br.com.businfo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.businfo.model.Linha;

public interface LinhaRepository extends JpaRepository<Linha, Long> {

    List<Linha> findAllByOrderByNumeroAsc();

    boolean existsByNumero(String numero);

    /** Pesquisa por número, nome, origem ou destino (RF05). */
    @Query("""
            SELECT l FROM Linha l
            WHERE l.numero LIKE %:termo% OR l.nome LIKE %:termo%
               OR l.origem LIKE %:termo% OR l.destino LIKE %:termo%
            ORDER BY l.numero ASC
            """)
    List<Linha> buscarPorTermo(@Param("termo") String termo);
}
