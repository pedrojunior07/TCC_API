package com.vaticano.paroquia.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "imagens_actividades", indexes = {
    @Index(name = "idx_imagens_actividade_id", columnList = "actividade_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagemActividade {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;  // ULID com prefixo img_

    @Column(name = "actividade_id", nullable = false, length = 50)
    private String actividadeId;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "nome_ficheiro", nullable = false, length = 500)
    private String nomeFicheiro;

    @Column(name = "mime", nullable = false, length = 100)
    private String mime;

    @Column(name = "tamanho", nullable = false)
    private Long tamanho;  // bytes

    @Column(name = "url_local", nullable = false, length = 1000)
    private String urlLocal;

    @Column(name = "data_upload", nullable = false)
    private LocalDateTime dataUpload;
}
