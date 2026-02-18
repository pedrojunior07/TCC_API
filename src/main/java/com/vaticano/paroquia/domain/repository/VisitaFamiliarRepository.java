package com.vaticano.paroquia.domain.repository;

import com.vaticano.paroquia.domain.entity.VisitaFamiliar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitaFamiliarRepository extends JpaRepository<VisitaFamiliar, String> {

    List<VisitaFamiliar> findByNucleoId(String nucleoId);

    List<VisitaFamiliar> findByNucleoIdAndSemanaRef(String nucleoId, String semanaRef);

    Optional<VisitaFamiliar> findByNucleoIdAndSemanaRefAndFamilyId(String nucleoId, String semanaRef, String familyId);

    boolean existsByNucleoIdAndSemanaRef(String nucleoId, String semanaRef);
}
