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

    public abstract void tryConnectToDrone();
    public abstract void tryDisconnectFromDrone();

    // --------------
    // COMMANDS
    // --------------

    public abstract void tryArmDrone(); 
    public abstract void tryDisarmDrone();
    public abstract void tryStartFlight();
    public abstract void tryReturnToHome();
    public abstract void tryLand();
    public abstract void tryKill();

    // --------------
    // TELEMETRY
    // --------------

    public abstract void getAvailableFlights();

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
