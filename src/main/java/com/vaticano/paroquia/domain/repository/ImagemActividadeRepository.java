package com.vaticano.paroquia.domain.repository;

import com.vaticano.paroquia.domain.entity.ImagemActividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagemActividadeRepository extends JpaRepository<ImagemActividade, String> {

    List<ImagemActividade> findByActividadeId(String actividadeId);
}
