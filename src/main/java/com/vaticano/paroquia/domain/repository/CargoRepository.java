package com.vaticano.paroquia.domain.repository;

import com.vaticano.paroquia.domain.entity.Cargo;
import com.vaticano.paroquia.domain.enums.EstadoCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, String> {

    List<Cargo> findByNucleoId(String nucleoId);

    List<Cargo> findByNucleoIdAndEstado(String nucleoId, EstadoCargo estado);
}
