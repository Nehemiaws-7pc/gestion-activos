package com.stressservice.service;

import com.stressservice.dto.AuthRequest;
import com.stressservice.dto.AuthResponse;
import com.stressservice.dto.EmpleadoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class BackendClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${backend.url}")
    private String backendUrl;

    @Value("${backend.username}")
    private String username;

    @Value("${backend.password}")
    private String password;

    private String token;

    public String getToken() {
        if (token == null) {
            authenticate();
        }
        return token;
    }

    public void authenticate() {
        AuthRequest req = new AuthRequest(username, password);
        ResponseEntity<AuthResponse> resp = restTemplate.postForEntity(
                backendUrl + "/auth/login", req, AuthResponse.class);
        this.token = resp.getBody().token();
    }

    public EmpleadoDTO crearEmpleado(EmpleadoDTO dto) {
        HttpHeaders headers = buildHeaders();
        HttpEntity<EmpleadoDTO> entity = new HttpEntity<>(dto, headers);
        ResponseEntity<EmpleadoDTO> resp = restTemplate.postForEntity(
                backendUrl + "/empleados", entity, EmpleadoDTO.class);
        return resp.getBody();
    }

    public List<EmpleadoDTO> crearBatch(List<EmpleadoDTO> dtos) {
        HttpHeaders headers = buildHeaders();
        HttpEntity<List<EmpleadoDTO>> entity = new HttpEntity<>(dtos, headers);
        ResponseEntity<List<EmpleadoDTO>> resp = restTemplate.exchange(
                backendUrl + "/empleados/batch",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {});
        return resp.getBody();
    }

    public List<EmpleadoDTO> listarEmpleados() {
        HttpHeaders headers = buildHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List<EmpleadoDTO>> resp = restTemplate.exchange(
                backendUrl + "/empleados",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {});
        return resp.getBody();
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getToken());
        return headers;
    }
}
