package com.ui;
import java.awt.image.BufferedImage;

import org.json.simple.JSONObject;

public abstract class Control {
    private String controlType;
    
    public String getControlType() {
        return controlType;
    }

    // --------------
    // CONNECTION
    // --------------

    protected abstract String dispatchCommand(); // Send the command to the drone, and recieve a string response.
    // Should implement custom connection logic as defined by the class implementation and setup through the constructor and updates.

    public abstract boolean tryConnectToDrone();

    // --------------
    // COMMANDS
    // --------------

    public abstract boolean tryArmDrone(); 
    public abstract boolean tryDisarmDrone();
    public abstract boolean tryStartFlight();
    public abstract boolean tryReturnToHome();
    public abstract boolean tryLand();
    public abstract boolean tryKill();

    // --------------
    // TELEMETRY
    // --------------

    public abstract JSONObject getTelemetryUpdate(); // Update void with command format? Or overload? Research.
    public abstract Flight[] getAvailableFlights();
    public abstract BufferedImage getFrontCameraView();
    public abstract BufferedImage getBottomCameraView();

    // --------------
    // HEALTH
    // --------------

    public abstract void checkHeathGPS();
    public abstract void checkHeathIMU();
    public abstract void checkHeathRadio();
    public abstract void checkHeathMotors();
    public abstract void checkHeathBattery();
    public abstract void checkHeathStorage();
    public abstract void checkHeathLogging();
    public abstract void checkHeathPixhawk();
    public abstract void checkHeathCameras();
    public abstract void checkHealthAll();

    // --------------
    // TESTS
    // --------------
    public abstract void runTestMotorSpinAll();
    public abstract void runTestMotorSpinSequence();
    public abstract void runTestPayloadActuate();
}
