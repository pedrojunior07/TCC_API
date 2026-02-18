package com.vaticano.paroquia.security;

import com.vaticano.paroquia.domain.enums.Role;
import com.vaticano.paroquia.exception.ForbiddenException;
import com.vaticano.paroquia.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    /**
     * Retorna o userId do usuário logado no contexto de segurança.
     */
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Verifica se o usuário atual tem uma role específica.
     */
    public boolean hasRole(Role role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority ->
                        grantedAuthority.getAuthority().equals("ROLE_" + role.name()));
    }

    /**
     * Verifica se o usuário atual é super admin.
     */
    public boolean isSuperAdmin() {
        return hasRole(Role.SUPER_ADMIN);
    }

    /**
     * Verifica se o usuário atual é secretário.
     */
    public boolean isSecretario() {
        return hasRole(Role.SECRETARIO);
    }

    /**
     * Verifica se o usuário atual é chefe de núcleo.
     */
    public boolean isChefeNucleo() {
        return hasRole(Role.CHEFE_NUCLEO);
    }

    /**
     * Verifica se há um usuário autenticado.
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && getCurrentUserId() != null;
    }

    /**
     * Requer que o usuário tenha uma role específica. Lança exceção se não tiver.
     */
    public void requireRole(Role role) {
        if (!isAuthenticated()) {
            throw new UnauthorizedException("Usuário não autenticado");
        }
        if (!hasRole(role)) {
            throw new ForbiddenException("Permissão negada. Role necessária: " + role.getValue());
        }
    }

    /**
     * Requer que o usuário tenha pelo menos uma das roles especificadas. Lança exceção se não tiver nenhuma.
     */
    public void requireAnyRole(Role... roles) {
        if (!isAuthenticated()) {
            throw new UnauthorizedException("Usuário não autenticado");
        }
        for (Role role : roles) {
            if (hasRole(role)) {
                return;
            }
        }
        throw new ForbiddenException("Permissão negada. Roles necessárias: " + java.util.Arrays.toString(roles));
    }
}
