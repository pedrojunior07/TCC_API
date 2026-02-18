package com.vaticano.paroquia.domain.repository;

import com.vaticano.paroquia.domain.entity.WhatsappConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WhatsappConfigRepository extends JpaRepository<WhatsappConfig, Long> {

    // Singleton: sempre ID = 1
    default Optional<WhatsappConfig> findConfig() {
        return findById(1L);
    }
}
