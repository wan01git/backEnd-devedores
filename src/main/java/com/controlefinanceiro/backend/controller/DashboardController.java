package com.controlefinanceiro.backend.controller;

import com.controlefinanceiro.backend.dto.DashboardResponse;
import com.controlefinanceiro.backend.security.UsuarioAutenticado;
import com.controlefinanceiro.backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> obter(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(dashboardService.obter(usuario.getId()));
    }
}
