package com.vaticano.paroquia.controller;

import com.vaticano.paroquia.dto.request.CreateUserRequest;
import com.vaticano.paroquia.dto.request.ResetPasswordRequest;
import com.vaticano.paroquia.dto.request.UpdateUserRequest;
import com.vaticano.paroquia.dto.response.MessageResponse;
import com.vaticano.paroquia.dto.response.UserResponse;
import com.vaticano.paroquia.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuários", description = "Gestão de usuários (super_admin only)")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Lista todos os usuários com paginação e busca opcional")
    public ResponseEntity<Page<UserResponse>> listUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<UserResponse> users = userService.listUsers(search, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna detalhes de um usuário específico")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(201).body(user);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza dados de um usuário existente")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UserResponse user = userService.updateUser(userId, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Deletar usuário", description = "Soft delete de um usuário")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable String userId) {
        MessageResponse response = userService.deleteUser(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/reset-password")
    @Operation(summary = "Resetar senha", description = "Reseta a senha de um usuário (super_admin only)")
    public ResponseEntity<MessageResponse> resetPassword(
            @PathVariable String userId,
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        MessageResponse response = userService.resetPassword(userId, request);
        return ResponseEntity.ok(response);
    }
}
