package com.vaticano.paroquia.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private String memberKey;
    private String memberId;
    private String ordOriginal;
    private String nomeCompleto;
    private String comunidade;
    private String dataBaptismo;
    private String dataNascimento;
    private String naturalidade;
    private String nomePai;
    private String naturalidadePai;
    private String estadoCivil;
    private String profissao;
    private String nomeMae;
    private String avosPaternos;
    private String avosMaternos;
    private String nomePadrinho;
    private String estadoCivilPadrinho;
    private String profissaoPadrinho;
    private String residenciaPadrinho;
    private String nomeMadrinha;
    private String estadoCivilMadrinha;
    private String profissaoMadrinha;
    private String residenciaMadrinha;
    private String dataCrisma;
    private String dataCasamento;
    private String numeroAssento;
    private String observacoes;
    private Boolean batizado;
    private Boolean crismado;
    private Boolean casado;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
