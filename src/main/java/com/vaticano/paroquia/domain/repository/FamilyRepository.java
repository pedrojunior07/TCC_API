package com.vaticano.paroquia.domain.repository;

import com.vaticano.paroquia.domain.entity.Family;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyRepository extends JpaRepository<Family, String> {

    @Query("SELECT f FROM Family f WHERE " +
           "LOWER(f.nome) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(f.residencia) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Family> searchFamilies(@Param("search") String search, Pageable pageable);
}
