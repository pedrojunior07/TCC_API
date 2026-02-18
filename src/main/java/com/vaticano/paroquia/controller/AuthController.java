package com.vaticano.paroquia.controller;

import com.vaticano.paroquia.dto.request.ChangePasswordRequest;
import com.vaticano.paroquia.dto.request.LoginRequest;
import com.vaticano.paroquia.dto.request.RefreshTokenRequest;
import com.vaticano.paroquia.dto.response.LoginResponse;
import com.vaticano.paroquia.dto.response.MessageResponse;
import com.vaticano.paroquia.dto.response.RefreshTokenResponse;
import com.vaticano.paroquia.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de autenticação e gestão de tokens")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/bootstrap")
    @Operation(summary = "Bootstrap admin inicial", description = "Cria o primeiro usuário administrador. Só funciona se não houver usuários cadastrados.")
    public ResponseEntity<MessageResponse> bootstrap() {
        MessageResponse response = authService.bootstrap();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica um usuário e retorna access token e refresh token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar access token", description = "Renova o access token usando um refresh token válido")
    public ResponseEntity<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revoga o refresh token do usuário")
    public ResponseEntity<MessageResponse> logout(@Valid @RequestBody RefreshTokenRequest request) {
        MessageResponse response = authService.logout(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/password")
    @Operation(summary = "Alterar senha", description = "Permite ao usuário autenticado alterar sua própria senha")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        MessageResponse response = authService.changePassword(request);
        return ResponseEntity.ok(response);
    }
}
