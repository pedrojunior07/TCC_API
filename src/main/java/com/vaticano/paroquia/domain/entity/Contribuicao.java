package com.vaticano.paroquia.domain.entity;

import com.vaticano.paroquia.domain.enums.MetodoPagamento;
import com.vaticano.paroquia.domain.enums.TipoContribuicao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contribuicoes", indexes = {
    @Index(name = "idx_contribuicoes_nucleo_id", columnList = "nucleo_id"),
    @Index(name = "idx_contribuicoes_data", columnList = "data"),
    @Index(name = "idx_contribuicoes_tipo", columnList = "tipo")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
public class Contribuicao {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;  // ULID com prefixo cont_

    @Column(name = "nucleo_id", nullable = false, length = 50)
    private String nucleoId;

    @Column(name = "actividade_id", length = 50)
    private String actividadeId;  // Opcional

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoContribuicao tipo;

    @Column(name = "valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(name = "moeda", nullable = false, length = 10)
    @Builder.Default
    private String moeda = "MZN";

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "pagador", nullable = false, length = 300)
    private String pagador;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo", nullable = false, length = 20)
    private MetodoPagamento metodo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "quitado", nullable = false)
    @Builder.Default
    private Boolean quitado = true;

    @Column(name = "comprovado", nullable = false)
    @Builder.Default
    private Boolean comprovado = false;

    // Dados do comprovativo (arquivo)
    @Column(name = "comprovativo_nome_ficheiro", length = 500)
    private String comprovativoNomeFicheiro;

    @Column(name = "comprovativo_mime", length = 100)
    private String comprovativoMime;

    @Column(name = "comprovativo_tamanho")
    private Long comprovativoTamanho;  // bytes

    @Column(name = "comprovativo_url_local", length = 1000)
    private String comprovativoUrlLocal;

    @Column(name = "comprovativo_data_upload")
    private LocalDateTime comprovativoDataUpload;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Soft delete
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", length = 50)
    private String deletedBy;
}
