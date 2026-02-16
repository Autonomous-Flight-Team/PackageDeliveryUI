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
    // --------------
    // VARIABLES
    // --------------

    private FileManager fileManager;
    private Logging logging;

    private int pollRate;
    private String connectionAddress;
    private int connectionPort;
    private int cameraResX;
    private int cameraResY;

    private int mapResX;
    private boolean testMode;

    // --------------
    // GENERAL
    // --------------

    public Settings(FileManager fileManager, Logging logging) {
        this.fileManager = fileManager;
        this.logging = logging;
        loadSettings();
    }

    // --------------
    // GETTERS/SETTERS
    // --------------

    public int getPollRate() { return pollRate; }
    public void setPollRate(int pollRate) { this.pollRate = pollRate; }

    public String getConnectionAddress() { return connectionAddress; }
    public void setConnectionAddress(String connectionAddress) { this.connectionAddress = connectionAddress; }

    public int getConnectionPort() { return connectionPort; }
    public void setConnectionPort(int connectionPort) { this.connectionPort = connectionPort; }

    public int getCameraResX() { return cameraResX; }
    public void setCameraResX(int cameraResX) { this.cameraResX = cameraResX; }

    public int getCameraResY() { return cameraResY; }
    public void setCameraResY(int cameraResY) { this.cameraResY = cameraResY; }

    public int getMapResX() { return mapResX; }
    public void setMapResX(int mapResX) { this.mapResX = mapResX; }

    public boolean getTestMode() { return testMode; }
    public void setTestMode(boolean testMode) { this.testMode = testMode; }

    // --------------
    // FUNCTION
    // --------------

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
            logging.logError("Failed to load user settings, reverting to default (Likely due to a first time launch, no need to worry if so)");
        } catch (IOException e) {
            logging.logError("Failed to read settings file.");
        }

        try{ 
            settings = (JSONArray) parser.parse(reader); 
        } catch (org.json.simple.parser.ParseException e) {
            logging.logError("Failed to parse settings file.");
        } catch (IOException e) {
            logging.logError("Failed to read settings file.");
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
    }

    public void saveSettings() {
        // DEBUG
        logging.logInfo("Saving user settings");
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
            logging.logError("Failed to save settings");
        }
    }

    public void stop() {
        saveSettings();
    }
}
