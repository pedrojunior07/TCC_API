package com.vaticano.paroquia.domain.repository;

import com.vaticano.paroquia.domain.entity.CertificateRequest;
import com.vaticano.paroquia.domain.enums.EstadoCertificado;
import com.vaticano.paroquia.domain.enums.TipoCertificado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRequestRepository extends JpaRepository<CertificateRequest, String> {

    Page<CertificateRequest> findByEstado(EstadoCertificado estado, Pageable pageable);

    List<CertificateRequest> findByMemberKey(String memberKey);

    List<CertificateRequest> findByNucleoId(String nucleoId);

    Page<CertificateRequest> findByTipo(TipoCertificado tipo, Pageable pageable);

    List<CertificateRequest> findByRequestedByUserId(String userId);
}
