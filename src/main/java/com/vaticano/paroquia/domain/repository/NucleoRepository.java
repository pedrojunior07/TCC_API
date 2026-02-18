package com.vaticano.paroquia.domain.repository;

import com.vaticano.paroquia.domain.entity.Nucleo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NucleoRepository extends JpaRepository<Nucleo, String> {

    List<Nucleo> findByAtivoTrue();

    @Query("SELECT n FROM Nucleo n WHERE :userId MEMBER OF n.chefeUserIds")
    List<Nucleo> findByChefe(@Param("userId") String userId);

    @Query("SELECT n FROM Nucleo n WHERE " +
           "LOWER(n.nome) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(n.comunidade) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Nucleo> searchNucleos(@Param("search") String search, Pageable pageable);
}
