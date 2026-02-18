package com.vaticano.paroquia.service;

import com.vaticano.paroquia.domain.entity.User;
import com.vaticano.paroquia.domain.enums.Role;
import com.vaticano.paroquia.domain.repository.RefreshTokenRepository;
import com.vaticano.paroquia.domain.repository.UserRepository;
import com.vaticano.paroquia.dto.request.CreateUserRequest;
import com.vaticano.paroquia.dto.request.UpdateUserRequest;
import com.vaticano.paroquia.dto.request.ResetPasswordRequest;
import com.vaticano.paroquia.dto.response.MessageResponse;
import com.vaticano.paroquia.dto.response.UserResponse;
import com.vaticano.paroquia.exception.BadRequestException;
import com.vaticano.paroquia.exception.DuplicateResourceException;
import com.vaticano.paroquia.exception.ResourceNotFoundException;
import com.vaticano.paroquia.security.SecurityUtils;
import com.vaticano.paroquia.util.NormalizeUtil;
import com.vaticano.paroquia.util.UlidGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UlidGenerator ulidGenerator;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    /**
     * Lista usuários com paginação e busca opcional.
     */
    public Page<UserResponse> listUsers(String search, Pageable pageable) {
        securityUtils.requireRole(Role.SUPER_ADMIN);

        Page<User> users;
        if (search != null && !search.isBlank()) {
            String searchNorm = NormalizeUtil.normalizeForKey(search);
            users = userRepository.findByUsernameNormContainingOrNameContainingIgnoreCase(searchNorm, search, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(this::toUserResponse);
    }

    /**
     * Busca usuário por ID.
     */
    public UserResponse getUserById(String userId) {
        securityUtils.requireRole(Role.SUPER_ADMIN);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return toUserResponse(user);
    }

    /**
     * Cria novo usuário.
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        securityUtils.requireRole(Role.SUPER_ADMIN);

        // Valida username único
        String usernameNorm = NormalizeUtil.normalizeForKey(request.getUsername());
        if (userRepository.existsByUsernameNorm(usernameNorm)) {
            throw new DuplicateResourceException("Username já existe: " + request.getUsername());
        }

        // Valida role
        Role role;
        try {
            role = Role.fromValue(request.getRole());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Role inválida: " + request.getRole());
        }

        // Gera hash de senha
        String salt = generateSalt();
        String passwordHash = hashPassword(request.getPassword(), salt);

        User user = User.builder()
                .userId(ulidGenerator.generateUserId())
                .username(request.getUsername().trim())
                .usernameNorm(usernameNorm)
                .name(request.getName().trim())
                .role(role)
                .active(true)
                .passwordHash(passwordHash)
                .salt(salt)
                .build();

        user = userRepository.save(user);

        auditService.log("user_created", "Usuário criado: " + user.getUsername(), null, user.getUserId());

        log.info("Usuário criado: {} ({})", user.getUsername(), user.getRole());

        return toUserResponse(user);
    }

    /**
     * Atualiza usuário existente.
     */
    @Transactional
    public UserResponse updateUser(String userId, UpdateUserRequest request) {
        securityUtils.requireRole(Role.SUPER_ADMIN);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // Atualiza nome
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName().trim());
        }

        // Atualiza role
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                Role role = Role.fromValue(request.getRole());
                user.setRole(role);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Role inválida: " + request.getRole());
            }
        }

        // Atualiza status ativo
        if (request.getActive() != null) {
            user.setActive(request.getActive());

            // Se desativou o usuário, revoga todos os refresh tokens
            if (!request.getActive()) {
                refreshTokenRepository.revokeAllByUserId(userId, LocalDateTime.now());
                log.info("Refresh tokens revogados para usuário desativado: {}", user.getUsername());
            }
        }

        user = userRepository.save(user);

        auditService.log("user_updated", "Usuário atualizado: " + user.getUsername(), null, userId);

        log.info("Usuário atualizado: {} ({})", user.getUsername(), user.getRole());

        return toUserResponse(user);
    }

    /**
     * Soft delete de usuário.
     */
    @Transactional
    public MessageResponse deleteUser(String userId) {
        securityUtils.requireRole(Role.SUPER_ADMIN);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        String currentUserId = securityUtils.getCurrentUserId();
        if (currentUserId.equals(userId)) {
            throw new BadRequestException("Não é possível deletar o próprio usuário");
        }

        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(currentUserId);
        userRepository.save(user);

        // Revoga todos os refresh tokens
        refreshTokenRepository.revokeAllByUserId(userId, LocalDateTime.now());

        auditService.log("user_deleted", "Usuário deletado: " + user.getUsername(), null, userId);

        log.info("Usuário deletado: {}", user.getUsername());

        return new MessageResponse("Usuário deletado com sucesso");
    }

    /**
     * Reset de senha por super_admin.
     */
    @Transactional
    public MessageResponse resetPassword(String userId, ResetPasswordRequest request) {
        securityUtils.requireRole(Role.SUPER_ADMIN);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        String newSalt = generateSalt();
        String newPasswordHash = hashPassword(request.getNewPassword(), newSalt);

        user.setSalt(newSalt);
        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);

        // Revoga todos os refresh tokens
        refreshTokenRepository.revokeAllByUserId(userId, LocalDateTime.now());

        auditService.log("user_password_reset", "Senha resetada para usuário: " + user.getUsername(), null, userId);

        log.info("Senha resetada para usuário: {}", user.getUsername());

        return new MessageResponse("Senha resetada com sucesso. Todos os tokens foram revogados.");
    }

    // ========== Helper Methods ==========

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[64];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash de senha", e);
        }
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().getValue())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
