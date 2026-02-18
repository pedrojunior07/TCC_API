package com.vaticano.paroquia.domain.repository;

import com.vaticano.paroquia.domain.entity.Contribuicao;
import com.vaticano.paroquia.domain.enums.TipoContribuicao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContribuicaoRepository extends JpaRepository<Contribuicao, String> {

    Page<Contribuicao> findByNucleoId(String nucleoId, Pageable pageable);

    Page<Contribuicao> findByNucleoIdAndQuitado(String nucleoId, Boolean quitado, Pageable pageable);

    List<Contribuicao> findByNucleoIdAndDataBetween(String nucleoId, LocalDate inicio, LocalDate fim);

    @Query("SELECT SUM(c.valor) FROM Contribuicao c WHERE c.nucleoId = :nucleoId AND c.quitado = true")
    BigDecimal sumValorByNucleoIdAndQuitadoTrue(@Param("nucleoId") String nucleoId);

    List<Contribuicao> findByTipo(TipoContribuicao tipo);
}
