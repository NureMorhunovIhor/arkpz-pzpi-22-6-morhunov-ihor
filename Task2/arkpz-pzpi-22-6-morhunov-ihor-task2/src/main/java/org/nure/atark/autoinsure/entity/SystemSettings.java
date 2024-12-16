package org.nure.atark.autoinsure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

@Entity
@Table(name = "system_settings")
public class SystemSettings {
    @Id
    @Size(max = 100)
    @Column(name = "setting_key", nullable = false, length = 100)
    private String settingKey;

    @Size(max = 255)
    @NotNull
    @Column(name = "setting_value", nullable = false)
    private String settingValue;

    public SystemSettings(Map<String, String> settings) {
        if (settings != null && !settings.isEmpty()) {
            this.settingKey = settings.keySet().iterator().next();
            this.settingValue = settings.get(this.settingKey);
        }
    }

    public SystemSettings() {}

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }
}
