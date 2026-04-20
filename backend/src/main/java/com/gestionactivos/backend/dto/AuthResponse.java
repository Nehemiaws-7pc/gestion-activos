package com.gestionactivos.backend.dto;

public record AuthResponse(String token, String role, String refreshToken) {
}