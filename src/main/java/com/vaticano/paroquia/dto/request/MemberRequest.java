package com.vaticano.paroquia.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {

    @Size(max = 50, message = "Ord. original não pode exceder 50 caracteres")
    private String ordOriginal;

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 300, message = "Nome completo não pode exceder 300 caracteres")
    private String nomeCompleto;

    @Size(max = 100, message = "Comunidade não pode exceder 100 caracteres")
    private String comunidade;

    @Size(max = 100, message = "Data de baptismo não pode exceder 100 caracteres")
    private String dataBaptismo;

    @Size(max = 100, message = "Data de nascimento não pode exceder 100 caracteres")
    private String dataNascimento;

    @Size(max = 200, message = "Naturalidade não pode exceder 200 caracteres")
    private String naturalidade;

    @Size(max = 200, message = "Nome do pai não pode exceder 200 caracteres")
    private String nomePai;

    @Size(max = 200, message = "Naturalidade do pai não pode exceder 200 caracteres")
    private String naturalidadePai;

    @Size(max = 100, message = "Estado civil não pode exceder 100 caracteres")
    private String estadoCivil;

    @Size(max = 200, message = "Profissão não pode exceder 200 caracteres")
    private String profissao;

    @Size(max = 200, message = "Nome da mãe não pode exceder 200 caracteres")
    private String nomeMae;

    @Size(max = 300, message = "Avós paternos não pode exceder 300 caracteres")
    private String avosPaternos;

    @Size(max = 300, message = "Avós maternos não pode exceder 300 caracteres")
    private String avosMaternos;

    @Size(max = 200, message = "Nome do padrinho não pode exceder 200 caracteres")
    private String nomePadrinho;

    @Size(max = 100, message = "Estado civil do padrinho não pode exceder 100 caracteres")
    private String estadoCivilPadrinho;

    @Size(max = 200, message = "Profissão do padrinho não pode exceder 200 caracteres")
    private String profissaoPadrinho;

    @Size(max = 300, message = "Residência do padrinho não pode exceder 300 caracteres")
    private String residenciaPadrinho;

    @Size(max = 200, message = "Nome da madrinha não pode exceder 200 caracteres")
    private String nomeMadrinha;

    @Size(max = 100, message = "Estado civil da madrinha não pode exceder 100 caracteres")
    private String estadoCivilMadrinha;

    @Size(max = 200, message = "Profissão da madrinha não pode exceder 200 caracteres")
    private String profissaoMadrinha;

    @Size(max = 300, message = "Residência da madrinha não pode exceder 300 caracteres")
    private String residenciaMadrinha;

    @Size(max = 100, message = "Data do crisma não pode exceder 100 caracteres")
    private String dataCrisma;

    @Size(max = 100, message = "Data do casamento não pode exceder 100 caracteres")
    private String dataCasamento;

    @Size(max = 100, message = "Número do assento não pode exceder 100 caracteres")
    private String numeroAssento;

    @Size(max = 2000, message = "Observações não pode exceder 2000 caracteres")
    private String observacoes;
}
