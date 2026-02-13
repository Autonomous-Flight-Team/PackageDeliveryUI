package com.ui;

import java.nio.file.*;

public class FileManager {
    private Path dataPath;

    public FileManager() {
        dataPath = getAppDataDir();
    }

    public static Path getAppDataDir() {
        System.out.println("Loading app data directory");
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");

        Path base;

        if (os.contains("win")) {
            base = Paths.get(System.getenv("APPDATA"), "DroneView");
        } else if (os.contains("mac")) {
            System.out.println("Mac Detected");
            base = Paths.get(home, "Library", "Application Support", "DroneView");
        } else {
            base = Paths.get(home, ".config", "droneview");
        }

        try {
            System.out.println("Creatign directory at" + base);
            Files.createDirectories(base);
            Files.createDirectories(base.resolve("logs"));
            Files.createDirectories(base.resolve("telemetry"));
            Files.createDirectories(base.resolve("settings"));
        } catch (Exception e) {
            System.out.println("Failed to create directory");
            throw new RuntimeException("Failed to create app data directory", e);
        }

        return base;
    }

    public Path getLogsDir() {
        return dataPath.resolve("logs");
    }

    public Path getSettingsDir() {
        return dataPath.resolve("settings");
    }

    public Path getTelemetryDir() {
        return dataPath.resolve("telemetry");
    }
}
