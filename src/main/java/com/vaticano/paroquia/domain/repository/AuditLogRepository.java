package com.vaticano.paroquia.domain.repository;

import com.vaticano.paroquia.domain.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {

    Page<AuditLog> findByTipo(String tipo, Pageable pageable);

    Page<AuditLog> findByUserId(String userId, Pageable pageable);

    List<AuditLog> findByTimestampBetween(LocalDateTime inicio, LocalDateTime fim);

    Page<AuditLog> findByTipoAndUserId(String tipo, String userId, Pageable pageable);
}
