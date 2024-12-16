package org.nure.atark.autoinsure.controller;

import org.nure.atark.autoinsure.entity.BackupData;
import org.nure.atark.autoinsure.entity.SystemSettings;
import org.nure.atark.autoinsure.service.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @PreAuthorize("hasAuthority('settings_admin')")
    @GetMapping("/backup")
    public ResponseEntity<String> createBackup() {
        settingsService.createBackup();
        return ResponseEntity.ok("Backup created successfully");
    }

    @PreAuthorize("hasAuthority('settings_admin')")
    @PostMapping("/restore")
    public ResponseEntity<String> restoreSettings(@RequestBody BackupData backupData) {
        settingsService.restoreSettings(backupData);
        return ResponseEntity.ok("Settings restored successfully");
    }

    @PreAuthorize("hasAuthority('settings_admin')")
    @GetMapping("/view")
    public ResponseEntity<SystemSettings> viewSettings() {
        SystemSettings settings = settingsService.getSettings();
        return ResponseEntity.ok(settings);
    }

    @PreAuthorize("hasAuthority('settings_admin')")
    @PostMapping("/addOrUpdate")
    public ResponseEntity<String> addOrUpdateSetting(@RequestBody SystemSettings systemSettings) {
        settingsService.addOrUpdateSetting(systemSettings.getSettingKey(), systemSettings.getSettingValue());
        return ResponseEntity.ok("Setting added or updated successfully");
    }

}

