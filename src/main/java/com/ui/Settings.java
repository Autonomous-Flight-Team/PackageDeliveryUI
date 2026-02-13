package com.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Settings {
    public int pollRate;
    public String connectionAddress;
    public int connectionPort;
    public String telemetrySaveLocation;
    public int cameraResX;
    public int cameraResY;

    public String logSaveLocation;
    public int mapResX;
    public boolean testMode;

    public Settings() {
        loadSettings();
    }

    private void loadSettings() {
        JSONParser parser = new JSONParser();
        JSONObject settings = new JSONObject();
        
        try {
            InputStream inputStream = getClass().getResourceAsStream("/UserSettings.json");
            if(inputStream == null) {
                inputStream = getClass().getResourceAsStream("/DefaultSettings.json");
            }

            InputStreamReader reader = new InputStreamReader(inputStream);
            
            try{ 
                settings = (JSONObject) parser.parse(reader); 
            } catch (org.json.simple.parser.ParseException e) {
                System.out.println("Failed to parse settings file.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Failed to load settings file.");
        } catch (IOException e) {
            System.out.println("Failed to read settings file.");
        }

        JSONObject telemetry = (JSONObject) settings.get("telemetry");
        JSONObject system = (JSONObject) settings.get("system");

        pollRate = ((Long) telemetry.get("pollRate")).intValue();
        connectionAddress = (String) telemetry.get("address");
        connectionPort = ((Long) telemetry.get("port")).intValue();
        telemetrySaveLocation = (String) telemetry.get("saveLocation");
        cameraResX = ((Long) telemetry.get("cameraResX")).intValue();
        cameraResY = ((Long) telemetry.get("cameraResY")).intValue();

        logSaveLocation = (String) system.get("logSaveLocation");
        mapResX = ((Long) system.get("mapResX")).intValue();
        testMode = (boolean) system.get("testMode");
    }

    private void saveSettings() {
        // DEBUG
        System.out.println("Writing save settings to " + telemetrySaveLocation + "UserSettings.json");
        JSONArray settings = new JSONArray();
        JSONObject telemetry = new JSONObject();
        JSONObject system = new JSONObject();

        telemetry.put("pollRate", pollRate);
        telemetry.put("address", connectionAddress);
        telemetry.put("port", connectionPort);
        telemetry.put("saveLocation", telemetrySaveLocation);
        telemetry.put("cameraResX", cameraResX);
        telemetry.put("cameraResY", cameraResY);

        system.put("logSaveLocation", logSaveLocation);
        system.put("mapResX", mapResX);
        system.put("testMode", testMode);

        settings.add(telemetry);
        settings.add(system);

        try {
            FileWriter writer = new FileWriter(telemetrySaveLocation + "UserSettings.json");
            writer.write(settings.toJSONString());
            writer.flush();
        } catch (IOException e) {
            //TODO: Logging
            System.out.println("Failed to save telemetry");
        }

        
    }

    public void stop() {
        saveSettings();
    }
}
