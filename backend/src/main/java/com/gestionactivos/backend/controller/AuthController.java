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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.username());

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.password(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        RefreshToken refreshToken = crearRefreshToken(user);
        return ResponseEntity.ok(new AuthResponse(token, user.getRole().name(), refreshToken.getToken()));
    }

    private RefreshToken crearRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user); // invalida el anterior
        RefreshToken rt = new RefreshToken();
        rt.setToken(UUID.randomUUID().toString());
        rt.setUser(user);
        rt.setExpiracion(Instant.now().plusMillis(REFRESH_EXPIRATION_MS));
        return refreshTokenRepository.save(rt);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody java.util.Map<String, String> body) {
        String tokenStr = body.get("refreshToken");
        if (tokenStr == null)
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
    public ResponseEntity<Void> logout(@RequestBody java.util.Map<String, String> body) {
        String tokenStr = body.get("refreshToken");
        if (tokenStr != null) {
            refreshTokenRepository.findByToken(tokenStr)
                    .ifPresent(refreshTokenRepository::delete);
        }
        return ResponseEntity.ok().build();
    }
}
