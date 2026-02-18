package com.vaticano.paroquia.service;

import com.vaticano.paroquia.domain.entity.Member;
import com.vaticano.paroquia.domain.enums.Role;
import com.vaticano.paroquia.domain.repository.MemberRepository;
import com.vaticano.paroquia.dto.request.MemberRequest;
import com.vaticano.paroquia.dto.response.MemberResponse;
import com.vaticano.paroquia.dto.response.MessageResponse;
import com.vaticano.paroquia.exception.DuplicateResourceException;
import com.vaticano.paroquia.exception.ResourceNotFoundException;
import com.vaticano.paroquia.security.SecurityUtils;
import com.vaticano.paroquia.util.MemberKeyGenerator;
import com.vaticano.paroquia.util.NormalizeUtil;
import com.vaticano.paroquia.util.UlidGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final UlidGenerator ulidGenerator;
    private final MemberKeyGenerator memberKeyGenerator;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    /**
     * Lista membros com paginação e busca opcional.
     */
    public Page<MemberResponse> listMembers(String search, Pageable pageable) {
        // super_admin e secretario podem ver todos os membros
        securityUtils.requireAnyRole(Role.SUPER_ADMIN, Role.SECRETARIO);

        Page<Member> members;
        if (search != null && !search.isBlank()) {
            members = memberRepository.searchMembers(search, pageable);
        } else {
            members = memberRepository.findAll(pageable);
        }

        return members.map(this::toMemberResponse);
    }

    /**
     * Busca membro por memberKey.
     */
    public MemberResponse getMemberByKey(String memberKey) {
        securityUtils.requireAnyRole(Role.SUPER_ADMIN, Role.SECRETARIO);

        Member member = memberRepository.findById(memberKey)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));

        return toMemberResponse(member);
    }

    /**
     * Cria novo membro.
     */
    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        securityUtils.requireAnyRole(Role.SUPER_ADMIN, Role.SECRETARIO);

        // Gera memberKey derivado
        String memberKey = memberKeyGenerator.generateMemberKey(
                request.getNomeCompleto(),
                request.getDataNascimento(),
                request.getNomePai(),
                request.getNomeMae()
        );

        // Valida unicidade
        if (memberRepository.existsById(memberKey)) {
            throw new DuplicateResourceException("Membro já existe (duplicado detectado por nome + data nascimento + pais)");
        }

        Member member = buildMemberFromRequest(memberKey, request);
        member = memberRepository.save(member);

        auditService.log("member_created", "Membro criado: " + member.getNomeCompleto(), null, member.getMemberKey());

        log.info("Membro criado: {} ({})", member.getNomeCompleto(), member.getMemberKey());

        return toMemberResponse(member);
    }

    /**
     * Atualiza membro existente.
     */
    @Transactional
    public MemberResponse updateMember(String memberKey, MemberRequest request) {
        securityUtils.requireAnyRole(Role.SUPER_ADMIN, Role.SECRETARIO);

        Member member = memberRepository.findById(memberKey)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));

        // Recalcula memberKey (pode ter mudado se dados base mudaram)
        String newMemberKey = memberKeyGenerator.generateMemberKey(
                request.getNomeCompleto(),
                request.getDataNascimento(),
                request.getNomePai(),
                request.getNomeMae()
        );

        // Se memberKey mudou, verifica se não cria duplicado
        if (!memberKey.equals(newMemberKey)) {
            if (memberRepository.existsById(newMemberKey)) {
                throw new DuplicateResourceException("Atualização criaria membro duplicado");
            }
            // Deleta registro antigo e cria novo (JPA não suporta update de @Id)
            memberRepository.delete(member);
            memberRepository.flush();
            member = buildMemberFromRequest(newMemberKey, request);
        } else {
            // Atualiza campos do membro existente
            updateMemberFields(member, request);
        }

        member = memberRepository.save(member);

        auditService.log("member_updated", "Membro atualizado: " + member.getNomeCompleto(), null, member.getMemberKey());

        log.info("Membro atualizado: {} ({})", member.getNomeCompleto(), member.getMemberKey());

        return toMemberResponse(member);
    }

    /**
     * Soft delete de membro.
     */
    @Transactional
    public MessageResponse deleteMember(String memberKey) {
        securityUtils.requireAnyRole(Role.SUPER_ADMIN, Role.SECRETARIO);

        Member member = memberRepository.findById(memberKey)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));

        String currentUserId = securityUtils.getCurrentUserId();
        member.setDeletedAt(LocalDateTime.now());
        member.setDeletedBy(currentUserId);
        memberRepository.save(member);

        auditService.log("member_deleted", "Membro deletado: " + member.getNomeCompleto(), null, memberKey);

        log.info("Membro deletado: {}", member.getNomeCompleto());

        return new MessageResponse("Membro deletado com sucesso");
    }

    // ========== Helper Methods ==========

    private Member buildMemberFromRequest(String memberKey, MemberRequest request) {
        Boolean batizado = memberKeyGenerator.deriveBatizado(request.getDataBaptismo());
        Boolean crismado = memberKeyGenerator.deriveCrismado(request.getDataCrisma());
        Boolean casado = memberKeyGenerator.deriveCasado(request.getDataCasamento());

        return Member.builder()
                .memberKey(memberKey)
                .memberId(ulidGenerator.generateMemberId())
                .ordOriginal(request.getOrdOriginal())
                .nomeCompleto(NormalizeUtil.normalizeValue(request.getNomeCompleto()))
                .comunidade(NormalizeUtil.normalizeValue(request.getComunidade()))
                .dataBaptismo(NormalizeUtil.normalizeValue(request.getDataBaptismo()))
                .dataNascimento(NormalizeUtil.normalizeValue(request.getDataNascimento()))
                .naturalidade(NormalizeUtil.normalizeValue(request.getNaturalidade()))
                .nomePai(NormalizeUtil.normalizeValue(request.getNomePai()))
                .naturalidadePai(NormalizeUtil.normalizeValue(request.getNaturalidadePai()))
                .estadoCivil(NormalizeUtil.normalizeValue(request.getEstadoCivil()))
                .profissao(NormalizeUtil.normalizeValue(request.getProfissao()))
                .nomeMae(NormalizeUtil.normalizeValue(request.getNomeMae()))
                .avosPaternos(NormalizeUtil.normalizeValue(request.getAvosPaternos()))
                .avosMaternos(NormalizeUtil.normalizeValue(request.getAvosMaternos()))
                .nomePadrinho(NormalizeUtil.normalizeValue(request.getNomePadrinho()))
                .estadoCivilPadrinho(NormalizeUtil.normalizeValue(request.getEstadoCivilPadrinho()))
                .profissaoPadrinho(NormalizeUtil.normalizeValue(request.getProfissaoPadrinho()))
                .residenciaPadrinho(NormalizeUtil.normalizeValue(request.getResidenciaPadrinho()))
                .nomeMadrinha(NormalizeUtil.normalizeValue(request.getNomeMadrinha()))
                .estadoCivilMadrinha(NormalizeUtil.normalizeValue(request.getEstadoCivilMadrinha()))
                .profissaoMadrinha(NormalizeUtil.normalizeValue(request.getProfissaoMadrinha()))
                .residenciaMadrinha(NormalizeUtil.normalizeValue(request.getResidenciaMadrinha()))
                .dataCrisma(NormalizeUtil.normalizeValue(request.getDataCrisma()))
                .dataCasamento(NormalizeUtil.normalizeValue(request.getDataCasamento()))
                .numeroAssento(NormalizeUtil.normalizeValue(request.getNumeroAssento()))
                .observacoes(NormalizeUtil.normalizeValue(request.getObservacoes()))
                .batizado(batizado)
                .crismado(crismado)
                .casado(casado)
                .build();
    }

    private void updateMemberFields(Member member, MemberRequest request) {
        Boolean batizado = memberKeyGenerator.deriveBatizado(request.getDataBaptismo());
        Boolean crismado = memberKeyGenerator.deriveCrismado(request.getDataCrisma());
        Boolean casado = memberKeyGenerator.deriveCasado(request.getDataCasamento());

        member.setOrdOriginal(request.getOrdOriginal());
        member.setNomeCompleto(NormalizeUtil.normalizeValue(request.getNomeCompleto()));
        member.setComunidade(NormalizeUtil.normalizeValue(request.getComunidade()));
        member.setDataBaptismo(NormalizeUtil.normalizeValue(request.getDataBaptismo()));
        member.setDataNascimento(NormalizeUtil.normalizeValue(request.getDataNascimento()));
        member.setNaturalidade(NormalizeUtil.normalizeValue(request.getNaturalidade()));
        member.setNomePai(NormalizeUtil.normalizeValue(request.getNomePai()));
        member.setNaturalidadePai(NormalizeUtil.normalizeValue(request.getNaturalidadePai()));
        member.setEstadoCivil(NormalizeUtil.normalizeValue(request.getEstadoCivil()));
        member.setProfissao(NormalizeUtil.normalizeValue(request.getProfissao()));
        member.setNomeMae(NormalizeUtil.normalizeValue(request.getNomeMae()));
        member.setAvosPaternos(NormalizeUtil.normalizeValue(request.getAvosPaternos()));
        member.setAvosMaternos(NormalizeUtil.normalizeValue(request.getAvosMaternos()));
        member.setNomePadrinho(NormalizeUtil.normalizeValue(request.getNomePadrinho()));
        member.setEstadoCivilPadrinho(NormalizeUtil.normalizeValue(request.getEstadoCivilPadrinho()));
        member.setProfissaoPadrinho(NormalizeUtil.normalizeValue(request.getProfissaoPadrinho()));
        member.setResidenciaPadrinho(NormalizeUtil.normalizeValue(request.getResidenciaPadrinho()));
        member.setNomeMadrinha(NormalizeUtil.normalizeValue(request.getNomeMadrinha()));
        member.setEstadoCivilMadrinha(NormalizeUtil.normalizeValue(request.getEstadoCivilMadrinha()));
        member.setProfissaoMadrinha(NormalizeUtil.normalizeValue(request.getProfissaoMadrinha()));
        member.setResidenciaMadrinha(NormalizeUtil.normalizeValue(request.getResidenciaMadrinha()));
        member.setDataCrisma(NormalizeUtil.normalizeValue(request.getDataCrisma()));
        member.setDataCasamento(NormalizeUtil.normalizeValue(request.getDataCasamento()));
        member.setNumeroAssento(NormalizeUtil.normalizeValue(request.getNumeroAssento()));
        member.setObservacoes(NormalizeUtil.normalizeValue(request.getObservacoes()));
        member.setBatizado(batizado);
        member.setCrismado(crismado);
        member.setCasado(casado);
    }

    private MemberResponse toMemberResponse(Member member) {
        return MemberResponse.builder()
                .memberKey(member.getMemberKey())
                .memberId(member.getMemberId())
                .ordOriginal(member.getOrdOriginal())
                .nomeCompleto(member.getNomeCompleto())
                .comunidade(member.getComunidade())
                .dataBaptismo(member.getDataBaptismo())
                .dataNascimento(member.getDataNascimento())
                .naturalidade(member.getNaturalidade())
                .nomePai(member.getNomePai())
                .naturalidadePai(member.getNaturalidadePai())
                .estadoCivil(member.getEstadoCivil())
                .profissao(member.getProfissao())
                .nomeMae(member.getNomeMae())
                .avosPaternos(member.getAvosPaternos())
                .avosMaternos(member.getAvosMaternos())
                .nomePadrinho(member.getNomePadrinho())
                .estadoCivilPadrinho(member.getEstadoCivilPadrinho())
                .profissaoPadrinho(member.getProfissaoPadrinho())
                .residenciaPadrinho(member.getResidenciaPadrinho())
                .nomeMadrinha(member.getNomeMadrinha())
                .estadoCivilMadrinha(member.getEstadoCivilMadrinha())
                .profissaoMadrinha(member.getProfissaoMadrinha())
                .residenciaMadrinha(member.getResidenciaMadrinha())
                .dataCrisma(member.getDataCrisma())
                .dataCasamento(member.getDataCasamento())
                .numeroAssento(member.getNumeroAssento())
                .observacoes(member.getObservacoes())
                .batizado(member.getBatizado())
                .crismado(member.getCrismado())
                .casado(member.getCasado())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
