package com.vaticano.paroquia.service;

import com.vaticano.paroquia.domain.entity.RefreshToken;
import com.vaticano.paroquia.domain.entity.User;
import com.vaticano.paroquia.domain.enums.Role;
import com.vaticano.paroquia.domain.repository.RefreshTokenRepository;
import com.vaticano.paroquia.domain.repository.UserRepository;
import com.vaticano.paroquia.dto.request.ChangePasswordRequest;
import com.vaticano.paroquia.dto.request.LoginRequest;
import com.vaticano.paroquia.dto.request.RefreshTokenRequest;
import com.vaticano.paroquia.dto.response.LoginResponse;
import com.vaticano.paroquia.dto.response.MessageResponse;
import com.vaticano.paroquia.dto.response.RefreshTokenResponse;
import com.vaticano.paroquia.exception.BadRequestException;
import com.vaticano.paroquia.exception.UnauthorizedException;
import com.vaticano.paroquia.security.SecurityUtils;
import com.vaticano.paroquia.security.jwt.JwtService;
import com.vaticano.paroquia.util.NormalizeUtil;
import com.vaticano.paroquia.util.UlidGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UlidGenerator ulidGenerator;
    private final SecurityUtils securityUtils;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    /**
     * Bootstrap: Cria usuário admin inicial se não existir nenhum usuário.
     */
    @Transactional
    public MessageResponse bootstrap() {
        long userCount = userRepository.count();
        if (userCount > 0) {
            throw new BadRequestException("Sistema já possui usuários cadastrados. Bootstrap não é permitido.");
        }

        String salt = generateSalt();
        String passwordHash = hashPassword("admin123", salt);

        User admin = User.builder()
                .userId(ulidGenerator.generateUserId())
                .username("admin")
                .usernameNorm("admin")
                .name("Administrador")
                .role(Role.SUPER_ADMIN)
                .active(true)
                .passwordHash(passwordHash)
                .salt(salt)
                .build();

        userRepository.save(admin);

        log.info("Usuário admin inicial criado com sucesso");

        return new MessageResponse("Usuário administrador criado com sucesso. Username: admin, Password: admin123");
    }

    /**
     * Login: Autentica usuário e retorna access token + refresh token.
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String usernameNorm = NormalizeUtil.normalizeForKey(request.getUsername());

        User user = userRepository.findByUsernameNorm(usernameNorm)
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

        if (!user.getActive()) {
            throw new UnauthorizedException("Usuário desativado");
        }

        // Verifica senha
        String passwordHash = hashPassword(request.getPassword(), user.getSalt());
        if (!passwordHash.equals(user.getPasswordHash())) {
            throw new UnauthorizedException("Credenciais inválidas");
        }

        // Atualiza último login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Gera tokens
        String accessToken = jwtService.generateAccessToken(user.getUserId(), user.getUsername(), user.getRole());
        String refreshToken = generateRefreshToken(user.getUserId());

        log.info("Login realizado com sucesso: {} ({})", user.getUsername(), user.getRole());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(LoginResponse.UserResponse.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .name(user.getName())
                        .role(user.getRole().getValue())
                        .active(user.getActive())
                        .build())
                .build();
    }

    /**
     * Refresh: Renova access token usando refresh token válido.
     */
    @Transactional
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        String tokenHash = hashRefreshToken(request.getRefreshToken());

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido"));

        if (!refreshToken.isValid()) {
            throw new UnauthorizedException("Refresh token expirado ou revogado");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));

        if (!user.getActive()) {
            throw new UnauthorizedException("Usuário desativado");
        }

        String newAccessToken = jwtService.generateAccessToken(user.getUserId(), user.getUsername(), user.getRole());

        log.debug("Access token renovado para usuário: {}", user.getUsername());

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    /**
     * Logout: Revoga refresh token do usuário.
     */
    @Transactional
    public MessageResponse logout(RefreshTokenRequest request) {
        String tokenHash = hashRefreshToken(request.getRefreshToken());

        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            token.setRevoked(true);
            token.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(token);
            log.info("Refresh token revogado para usuário: {}", token.getUserId());
        });

        return new MessageResponse("Logout realizado com sucesso");
    }

    /**
     * Change Password: Altera senha do usuário logado.
     */
    @Transactional
    public MessageResponse changePassword(ChangePasswordRequest request) {
        String currentUserId = securityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new UnauthorizedException("Usuário não autenticado");
        }

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));

        // Verifica senha atual
        String currentPasswordHash = hashPassword(request.getCurrentPassword(), user.getSalt());
        if (!currentPasswordHash.equals(user.getPasswordHash())) {
            throw new UnauthorizedException("Senha atual incorreta");
        }

        // Gera nova senha hash
        String newSalt = generateSalt();
        String newPasswordHash = hashPassword(request.getNewPassword(), newSalt);

        user.setSalt(newSalt);
        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);

        // Revoga todos os refresh tokens existentes
        refreshTokenRepository.revokeAllByUserId(currentUserId, LocalDateTime.now());

        log.info("Senha alterada com sucesso para usuário: {}", user.getUsername());

        return new MessageResponse("Senha alterada com sucesso. Faça login novamente com a nova senha.");
    }

    // ========== Helper Methods ==========

    private String generateRefreshToken(String userId) {
        String rawToken = ulidGenerator.generateRefreshTokenId();
        String tokenHash = hashRefreshToken(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .tokenId(ulidGenerator.generateRefreshTokenId())
                .userId(userId)
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000))
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

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

    private String hashRefreshToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash de refresh token", e);
        }
    }
}
