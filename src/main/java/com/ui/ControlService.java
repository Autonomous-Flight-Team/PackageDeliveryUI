package com.droneviewui;
import javafx.concurrent.Service;
import java.awt.image.BufferedImage;
import com.droneviewui.*;

public abstract class ControlService extends Service<Void> {
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

    public abstract String getTelemetryUpdate(); // Update void with command format? Or overload? Research.
    public abstract Flight[] getAvailableFlights();
    public abstract BufferedImage getFrontCameraView();
    public abstract BufferedImage getBottomCameraView();

    // --------------
    // HEALTH
    // --------------

    public abstract boolean checkHeathGPS();
    public abstract boolean checkHeathIMU();
    public abstract boolean checkHeathRadio();
    public abstract boolean checkHeathMotors();
    public abstract boolean checkHeathBattery();
    public abstract boolean checkHeathStorage();
    public abstract boolean checkHeathLogging();
    public abstract boolean checkHeathPixhawk();
}
