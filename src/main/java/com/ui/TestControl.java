package com.ui;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.ui.lib.Position;

import javafx.application.Platform;

public class TestControl extends Control {
    private Data data;
    private String controlType = "Test";

    public String getControlType() {
        return controlType;
    }

    TestControl(Data data) {
        this.data = data;
    }

    // --------------
    // CONNECTION
    // --------------

    protected String dispatchCommand() { return ""; }; // Send the command to the drone, and recieve a string response.
    // Should implement custom connection logic as defined by the class implementation and setup through the constructor and updates.

    public boolean tryConnectToDrone() {
        checkHealthAll();
        Platform.runLater(() -> data.setConnectedToDrone(true));
        return true;
    }

    // --------------
    // COMMANDS
    // --------------

    public boolean tryArmDrone() {
        Platform.runLater(() -> data.setDroneArmed(true));
        Platform.runLater(() -> data.setFlightStatus("Armed"));
        return true;
    }
    public boolean tryDisarmDrone() {
        Platform.runLater(() -> data.setFlightStatus("Unarmed"));
        Platform.runLater(() -> data.setDroneArmed(false));
        Platform.runLater(() -> data.setDroneInFlight(false));
        return true;
    }
    public boolean tryStartFlight() {
        Platform.runLater(() -> data.setFlightStatus("Taking Off"));
        Platform.runLater(() -> data.setDroneInFlight(true));

        return true;
    }
    public boolean tryReturnToHome() {
        Platform.runLater(() -> data.setFlightStatus("Returning to Home"));
        Platform.runLater(() -> data.setDroneInFlight(false));
        return true;
    }
    public boolean tryLand() {
        Platform.runLater(() -> data.setFlightStatus("Landing"));
        Platform.runLater(() -> data.setDroneInFlight(false));
        return true;
    }
    public boolean tryKill() {
        Platform.runLater(() -> data.setFlightStatus("Unarmed"));
        Platform.runLater(() -> data.setDroneArmed(false));
        Platform.runLater(() -> data.setDroneInFlight(false));
        return true;
    }

    // --------------
    // TELEMETRY
    // --------------

    public JSONObject getTelemetryUpdate() {
        JSONParser parser = new JSONParser();
        JSONObject obj = new JSONObject();
        
        try {
            InputStream inputStream = getClass().getResourceAsStream("/TelemetryStandard.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            
            try{ 
                obj = (JSONObject) parser.parse(reader); 
            } catch (org.json.simple.parser.ParseException e) {
                System.out.println("Failed to parse JSON file.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Failed to load JSON file.");
        } catch (IOException e) {
            System.out.println("Failed to parse JSON file.");
        }

        return obj;
    }

    public Flight[] getAvailableFlights() {
        Flight[] flights = new Flight[1];
        Position[] waypoints = new Position[1];
        waypoints[0] = new Position(-10,-10, 0);
        flights[0] = new Flight("Example Flight", "The only available flight", new Position(10,10, 0), new Position(-10,10, 0), waypoints);
        return flights;
    }

    public BufferedImage getFrontCameraView() {
        return new BufferedImage(0, 0, 0, null);
    }

    public BufferedImage getBottomCameraView() {
        return new BufferedImage(0, 0, 0, null);
    }

    // --------------
    // HEALTH
    // --------------

    public void checkHeathGPS() {
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
        }
        
        Platform.runLater(() -> data.setHealthGps(true));
    }

    public void checkHeathIMU() {
        try {
            Thread.sleep(240);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
        }
        
        Platform.runLater(() -> data.setHealthIMU(true));
    }

    public void checkHeathRadio() {
        try {
            Thread.sleep(220);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
        }
        
        Platform.runLater(() -> data.setHealthRadio(true));
    }

    public void checkHeathMotors() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
        }
        
        Platform.runLater(() -> data.setHealthMotor(true));
    }

    public void checkHeathBattery() {
        try {
            Thread.sleep(180);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
        }
        
        Platform.runLater(() -> data.setHealthBattery(true));
    }

    public void checkHeathStorage() {
        try {
            Thread.sleep(160);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
        }
        
        Platform.runLater(() -> data.setHealthStorage(true));
    }

    public void checkHeathLogging() {
        try {
            Thread.sleep(140);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
        }
        
        Platform.runLater(() -> data.setHealthLogging(true));
    }

    public void checkHeathPixhawk() {
        try {
            Thread.sleep(120);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
        }
        
        Platform.runLater(() -> data.setHealthPixhawk(true));
    }

    public void checkHeathCameras() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
        }
        
        Platform.runLater(() -> data.setHealthCamera(true));
    }

    public void checkHealthAll() {
        checkHeathBattery();
        checkHeathGPS();
        checkHeathIMU();
        checkHeathLogging();
        checkHeathMotors();
        checkHeathPixhawk();
        checkHeathRadio();
        checkHeathStorage();
        checkHeathCameras();
    }
}
