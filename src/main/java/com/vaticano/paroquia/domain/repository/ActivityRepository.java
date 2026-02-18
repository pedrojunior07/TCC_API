package com.vaticano.paroquia.domain.repository;

import com.vaticano.paroquia.domain.entity.Activity;
import com.vaticano.paroquia.domain.enums.EstadoActividade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {

    Page<Activity> findByNucleoId(String nucleoId, Pageable pageable);

    List<Activity> findByNucleoIdAndDataBetween(String nucleoId, LocalDate inicio, LocalDate fim);

    List<Activity> findByEstado(EstadoActividade estado);

    Page<Activity> findByNucleoIdAndEstado(String nucleoId, EstadoActividade estado, Pageable pageable);
}
