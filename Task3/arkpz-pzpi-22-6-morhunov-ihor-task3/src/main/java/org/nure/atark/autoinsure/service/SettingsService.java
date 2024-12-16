package org.nure.atark.autoinsure.service;

import jakarta.annotation.PostConstruct;
import org.nure.atark.autoinsure.entity.BackupData;
import org.nure.atark.autoinsure.entity.SystemSettings;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class SettingsService {

    private final DataSource dataSource;

    public SettingsService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void initializeSettings() {
        addOrUpdateSetting("defaultKey", "defaultValue");
    }

    public void createBackup() {
        String backupQuery = "BACKUP DATABASE AutoInsure TO DISK = 'C:\\backups\\AutoInsure_Backup.bak'";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(backupQuery);
            System.out.println("Backup created successfully");
        } catch (SQLException e) {
            throw new RuntimeException("Error creating backup", e);
        }
    }

    public void restoreSettings(BackupData backupData) {
        String setSingleUserQuery = "ALTER DATABASE AutoInsure SET SINGLE_USER WITH ROLLBACK IMMEDIATE;";
        String restoreQuery = "RESTORE DATABASE AutoInsure FROM DISK = 'C:\\backups\\AutoInsure_Backup.bak' WITH REPLACE";
        String setMultiUserQuery = "ALTER DATABASE AutoInsure SET MULTI_USER;";
        String switchToMasterQuery = "USE master;";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(switchToMasterQuery);
            statement.execute(setSingleUserQuery);
            System.out.println("Database switched to SINGLE_USER mode.");
            statement.execute(restoreQuery);
            System.out.println("Database restored successfully.");
            statement.execute(setMultiUserQuery);
            System.out.println("Database switched to MULTI_USER mode.");

        } catch (SQLException e) {
            throw new RuntimeException("Error restoring database", e);
        }
    }

    public SystemSettings getSettings() {
        String query = "SELECT setting_key, setting_value FROM system_settings";
        Map<String, String> settings = new HashMap<>();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                settings.put(resultSet.getString("setting_key"), resultSet.getString("setting_value"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching settings", e);
        }

        return new SystemSettings(settings);
    }

    public void addOrUpdateSetting(String key, String value) {
        updateSetting(key, value);
    }

    public void updateSetting(String key, String value) {
        String query = "MERGE system_settings AS target " +
                "USING (SELECT ? AS setting_key, ? AS setting_value) AS source " +
                "ON target.setting_key = source.setting_key " +
                "WHEN MATCHED THEN UPDATE SET setting_value = source.setting_value " +
                "WHEN NOT MATCHED THEN INSERT (setting_key, setting_value) VALUES (source.setting_key, source.setting_value);";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, value);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating setting", e);
        }
    }

}
