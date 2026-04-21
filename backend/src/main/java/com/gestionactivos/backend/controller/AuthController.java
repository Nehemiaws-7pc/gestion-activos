package com.gestionactivos.backend.controller;

import com.gestionactivos.backend.dto.RefreshResponse;
import com.gestionactivos.backend.model.RefreshToken;
import com.gestionactivos.backend.repository.RefreshTokenRepository;
import java.time.Instant;
import java.util.UUID;
import com.gestionactivos.backend.dto.AuthRequest;
import com.gestionactivos.backend.dto.AuthResponse;
import com.gestionactivos.backend.model.User;
import com.gestionactivos.backend.repository.UserRepository;
import com.gestionactivos.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private static final long REFRESH_EXPIRATION_MS = 30L * 24 * 60 * 60 * 1000;

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        String username = request.username();
        log.info("Login attempt for username='{}'", username);

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            log.warn("Login failed: username='{}' not found in database", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userOpt.get();
        boolean matches = passwordEncoder.matches(request.password(), user.getPassword());
        if (!matches) {
            log.warn("Login failed: password mismatch for username='{}'", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        limpiarRefreshTokensExpirados();

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        RefreshToken refreshToken = crearRefreshToken(user);
        log.info("Login success for username='{}' role='{}'", username, user.getRole());
        return ResponseEntity.ok(new AuthResponse(token, user.getRole().name(), refreshToken.getToken()));
    }

    private RefreshToken crearRefreshToken(User user) {
        RefreshToken rt = new RefreshToken();
        rt.setToken(UUID.randomUUID().toString());
        rt.setUser(user);
        rt.setExpiracion(Instant.now().plusMillis(REFRESH_EXPIRATION_MS));
        return refreshTokenRepository.save(rt);
    }

    private void limpiarRefreshTokensExpirados() {
        try {
            refreshTokenRepository.deleteByExpiracionBefore(Instant.now());
        } catch (Exception e) {
            log.warn("No se pudieron limpiar refresh tokens expirados: {}", e.getMessage());
        }
    }

    @PostMapping("/refresh")
    @Transactional
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String tokenStr = body != null ? body.get("refreshToken") : null;
        if (tokenStr == null || tokenStr.isBlank())
            return ResponseEntity.badRequest().build();

        return refreshTokenRepository.findByToken(tokenStr)
                .map(rt -> {
                    if (rt.estaExpirado()) {
                        refreshTokenRepository.delete(rt);
                        return ResponseEntity.status(401).build();
                    }
                    User user = rt.getUser();
                    String nuevoAccess = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
                    return ResponseEntity.ok(new RefreshResponse(nuevoAccess, user.getRole().name()));
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<Void> logout(@RequestBody(required = false) Map<String, String> body) {
        String tokenStr = body != null ? body.get("refreshToken") : null;
        if (tokenStr != null && !tokenStr.isBlank()) {
            try {
                refreshTokenRepository.findByToken(tokenStr)
                        .ifPresent(refreshTokenRepository::delete);
            } catch (Exception e) {
                log.warn("Fallo al eliminar refresh token en logout: {}", e.getMessage());
            }
        }
        return ResponseEntity.ok().build();
    }
}
