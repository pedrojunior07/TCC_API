package com.vaticano.paroquia.controller;

import com.vaticano.paroquia.dto.request.MemberRequest;
import com.vaticano.paroquia.dto.response.MemberResponse;
import com.vaticano.paroquia.dto.response.MessageResponse;
import com.vaticano.paroquia.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Membros", description = "Gestão de membros paroquiais")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    @Operation(summary = "Listar membros", description = "Lista todos os membros com paginação e busca opcional")
    public ResponseEntity<Page<MemberResponse>> listMembers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nomeCompleto") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<MemberResponse> members = memberService.listMembers(search, pageable);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{memberKey}")
    @Operation(summary = "Buscar membro por chave", description = "Retorna detalhes de um membro específico")
    public ResponseEntity<MemberResponse> getMemberByKey(@PathVariable String memberKey) {
        MemberResponse member = memberService.getMemberByKey(memberKey);
        return ResponseEntity.ok(member);
    }

    @PostMapping
    @Operation(summary = "Criar membro", description = "Cria um novo membro no sistema")
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberRequest request) {
        MemberResponse member = memberService.createMember(request);
        return ResponseEntity.status(201).body(member);
    }

    @PutMapping("/{memberKey}")
    @Operation(summary = "Atualizar membro", description = "Atualiza dados de um membro existente")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable String memberKey,
            @Valid @RequestBody MemberRequest request
    ) {
        MemberResponse member = memberService.updateMember(memberKey, request);
        return ResponseEntity.ok(member);
    }

    @DeleteMapping("/{memberKey}")
    @Operation(summary = "Deletar membro", description = "Soft delete de um membro")
    public ResponseEntity<MessageResponse> deleteMember(@PathVariable String memberKey) {
        MessageResponse response = memberService.deleteMember(memberKey);
        return ResponseEntity.ok(response);
    }
}
