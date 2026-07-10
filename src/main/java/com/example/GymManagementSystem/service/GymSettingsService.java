package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.GymSettingsRequest;
import com.example.GymManagementSystem.dto.GymSettingsResponse;
import com.example.GymManagementSystem.model.GymSettings;
import com.example.GymManagementSystem.repository.GymSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class GymSettingsService {

    private static final String DEFAULT_GYM_NAME = "MUSCLE MONSTERZ";
    private static final String DEFAULT_GPAY_NUMBER = "7708190175@uboi";
    private static final String DEFAULT_QR_IMAGE_URL =
            "https://api.qrserver.com/v1/create-qr-code/?size=260x260&data=upi%3A%2F%2Fpay%3Fpa%3D7708190175%40uboi%26pn%3DNaveen%2520Kumar%2520Dhandapani%26cu%3DINR";

    private final GymSettingsRepository gymSettingsRepository;

    public GymSettingsService(GymSettingsRepository gymSettingsRepository) {
        this.gymSettingsRepository = gymSettingsRepository;
    }

    public GymSettingsResponse getSettings() {
        return GymSettingsResponse.from(getOrCreateSettings());
    }

    public GymSettingsResponse updateSettings(GymSettingsRequest request) {
        GymSettings settings = getOrCreateSettings();
        settings.setGymName(request.getGymName());
        settings.setGymPhone(request.getGymPhone());
        settings.setGpayNumber(request.getGpayNumber());
        settings.setQrImageUrl(request.getQrImageUrl());
        return GymSettingsResponse.from(gymSettingsRepository.save(settings));
    }

    private GymSettings getOrCreateSettings() {
        GymSettings settings = gymSettingsRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(GymSettings::new);

        boolean changed = false;
        if (isBlank(settings.getGymName())) {
            settings.setGymName(DEFAULT_GYM_NAME);
            changed = true;
        }
        if (isBlank(settings.getGpayNumber())) {
            settings.setGpayNumber(DEFAULT_GPAY_NUMBER);
            changed = true;
        }
        if (isBlank(settings.getQrImageUrl())) {
            settings.setQrImageUrl(DEFAULT_QR_IMAGE_URL);
            changed = true;
        }

        return changed ? gymSettingsRepository.save(settings) : settings;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
