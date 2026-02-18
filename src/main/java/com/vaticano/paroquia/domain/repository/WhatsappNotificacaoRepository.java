package com.vaticano.paroquia.domain.repository;

import com.vaticano.paroquia.domain.entity.WhatsappNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WhatsappNotificacaoRepository extends JpaRepository<WhatsappNotificacao, String> {

    List<WhatsappNotificacao> findByNucleoId(String nucleoId);

    List<WhatsappNotificacao> findByEnabledTrue();
}
