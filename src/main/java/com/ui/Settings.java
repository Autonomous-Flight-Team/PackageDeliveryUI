package com.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.nio.file.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Settings {
    private FileManager fileManager;

    public int pollRate;
    public String connectionAddress;
    public int connectionPort;
    public int cameraResX;
    public int cameraResY;

    public int mapResX;
    public boolean testMode;

    public Settings(FileManager fileManager) {
        this.fileManager = fileManager;
        loadSettings();
    }

    private void loadSettings() {
        JSONParser parser = new JSONParser();
        JSONArray settings = new JSONArray();
        InputStream inputStream;
        InputStreamReader reader;

        // Load the default settings
        inputStream = getClass().getResourceAsStream("/DefaultSettings.json");
        reader = new InputStreamReader(inputStream);
        
        // Try to load the full user settings
        try {
            inputStream = Files.newInputStream(fileManager.getSettingsDir().resolve("UserSettings.json"));
            reader = new InputStreamReader(inputStream);
        } catch (FileNotFoundException e) { 
            System.out.print("Failed to load settings file");
        } catch (IOException e) {
            System.out.println("Failed to read settings file.");
        }

        try{ 
            settings = (JSONArray) parser.parse(reader); 
        } catch (org.json.simple.parser.ParseException e) {
            System.out.println("Failed to parse settings file.");
        } catch (IOException e) {
            System.out.println("Failed to read settings file.");
        }

        JSONObject telemetry = (JSONObject) settings.get(0);
        JSONObject system = (JSONObject) settings.get(1);

        pollRate = ((Long) telemetry.get("pollRate")).intValue();
        connectionAddress = (String) telemetry.get("address");
        connectionPort = ((Long) telemetry.get("port")).intValue();
        cameraResX = ((Long) telemetry.get("cameraResX")).intValue();
        cameraResY = ((Long) telemetry.get("cameraResY")).intValue();

        mapResX = ((Long) system.get("mapResX")).intValue();
        testMode = (boolean) system.get("testMode");
        System.out.println(testMode);
    }

    private void saveSettings() {
        // DEBUG
        System.out.println("Writing save settings to " + fileManager.getSettingsDir() + "/UserSettings.json");
        JSONArray settings = new JSONArray();
        JSONObject telemetry = new JSONObject();
        JSONObject system = new JSONObject();

        telemetry.put("pollRate", pollRate);
        telemetry.put("address", connectionAddress);
        telemetry.put("port", connectionPort);
        telemetry.put("cameraResX", cameraResX);
        telemetry.put("cameraResY", cameraResY);

        system.put("mapResX", mapResX);
        system.put("testMode", testMode);

        settings.add(telemetry);
        settings.add(system);

        try {
            FileWriter writer = new FileWriter(fileManager.getSettingsDir() + "/UserSettings.json");
            writer.write(settings.toJSONString());
            writer.flush();
        } catch (IOException e) {
            //TODO: Logging
            System.out.println("Failed to save settings");
        }
    }

    public void stop() {
        saveSettings();
    }
}
