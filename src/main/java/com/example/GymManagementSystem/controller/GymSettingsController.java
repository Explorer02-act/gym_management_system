package com.example.GymManagementSystem.controller;

import com.example.GymManagementSystem.dto.GymSettingsRequest;
import com.example.GymManagementSystem.dto.GymSettingsResponse;
import com.example.GymManagementSystem.service.GymSettingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/settings")
public class GymSettingsController {

    private final GymSettingsService gymSettingsService;

    public GymSettingsController(GymSettingsService gymSettingsService) {
        this.gymSettingsService = gymSettingsService;
    }

    @GetMapping
    public GymSettingsResponse getSettings() {
        return gymSettingsService.getSettings();
    }

    @PutMapping
    public GymSettingsResponse updateSettings(@RequestBody GymSettingsRequest request) {
        return gymSettingsService.updateSettings(request);
    }
}
