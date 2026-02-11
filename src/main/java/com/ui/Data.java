package com.ui;
import javafx.beans.property.*;
import java.awt.image.BufferedImage;

// Class for storing telemetry and drone data for other classes to access
public class Data {
    // --------------
    // STATE
    // --------------
    private BooleanProperty DRONE_ARMED = new SimpleBooleanProperty();
    private BooleanProperty CONNECTED_TO_DRONE = new SimpleBooleanProperty();
    private BooleanProperty DRONE_IN_FLIGHT = new SimpleBooleanProperty();
    private IntegerProperty SELECTED_FLIGHT = new SimpleIntegerProperty(); // Stores the selected flight as it is on the drone. Commands to change flight go through the drone and back into system through a state input.
    private Flight[] AVAILABLE_FLIGHTS;

    // --------------
    // VARIABLES
    // --------------

    // General Properties
    private StringProperty DRONE_NAME = new SimpleStringProperty();

    // Health Properies
    private BooleanProperty HEALTH_GPS = new SimpleBooleanProperty();
    private BooleanProperty HEALTH_BATTERY = new SimpleBooleanProperty();
    private BooleanProperty HEALTH_MOTOR = new SimpleBooleanProperty();
    private BooleanProperty HEALTH_IMU = new SimpleBooleanProperty();
    private BooleanProperty HEALTH_LOGGING = new SimpleBooleanProperty(); 
    private BooleanProperty HEALTH_CAMERA = new SimpleBooleanProperty();
    private BooleanProperty HEALTH_STORAGE = new SimpleBooleanProperty(); 
    private BooleanProperty HEALTH_RADIO = new SimpleBooleanProperty(); 
    private BooleanProperty HEALTH_PIXHAWK = new SimpleBooleanProperty(); 

    // Drone Kinematics
    private DoubleProperty DRONE_ALTITUDE = new SimpleDoubleProperty();
    private DoubleProperty DRONE_SPEED = new SimpleDoubleProperty();
    private Position DRONE_POSITION_RELATIVE = new Position();
    private Position DRONE_POSITION_GLOBAL = new Position();
    private Vector3 DRONE_ATTITUDE = new Vector3();
    private Vector3 DRONE_VELOCITY = new Vector3();
    private Vector3 DRONE_ACCELERATION = new Vector3();

    // Drone Battery
    private DoubleProperty DRONE_BAT_MAX_CAPACITY = new SimpleDoubleProperty();
    private DoubleProperty DRONE_BAT_CAPACITY = new SimpleDoubleProperty();
    private DoubleProperty DRONE_MOTOR_VOLTAGE_DRAW = new SimpleDoubleProperty();

    // Radio
    private DoubleProperty RADIO_CONNECTION_BANDWIDTH = new SimpleDoubleProperty();
    
    // Flight
    private DoubleProperty FLIGHT_ELAPSED_TIME_MS = new SimpleDoubleProperty();
    private StringProperty FLIGHT_ELAPSED_TIME_STRING_REP = new SimpleStringProperty();
    private DoubleProperty FLIGHT_NEXT_TARGET_DIST = new SimpleDoubleProperty();
    private StringProperty FLIGHT_NAME = new SimpleStringProperty();
    private StringProperty FLIGHT_STATUS = new SimpleStringProperty();

    // Camera
    private BufferedImage BOTTOM_CAMERA_FRAME;
    private BufferedImage FRONT_CAMERA_FRAME;
    
    // Misc 
    private BooleanProperty PAYLOAD_STATUS = new SimpleBooleanProperty();

    // --------------
    // GETTERS/SETTERS
    // --------------

    // State Information
    public boolean isConnectedToDrone() { return CONNECTED_TO_DRONE.get(); }
    public void setConnectedToDrone(boolean value) { CONNECTED_TO_DRONE.set(value); }
    public BooleanProperty connectedToDroneProperty() { return CONNECTED_TO_DRONE; }

    public boolean isDroneArmed() { return DRONE_ARMED.get(); }
    public void setDroneArmed(boolean value) { DRONE_ARMED.set(value); }
    public BooleanProperty droneArmedProperty() { return DRONE_ARMED; }

    public boolean isDroneInFlight() { return DRONE_IN_FLIGHT.get(); }
    public void setDroneInFlight(boolean value) { DRONE_IN_FLIGHT.set(value); }
    public BooleanProperty droneInFlightProperty() { return DRONE_IN_FLIGHT; }

    public int getSelectedFlight() { return SELECTED_FLIGHT.get(); }
    public void setSelectedFlight(int value) { SELECTED_FLIGHT.set(value); }
    public IntegerProperty selectedFlightProperty() { return SELECTED_FLIGHT; }

    public Flight[] getAvailableFlights() { return AVAILABLE_FLIGHTS; }
    public void setAvailableFlights(Flight[] flights) { AVAILABLE_FLIGHTS = flights; }

    // Health Checks
    public boolean isHealthGps() { return HEALTH_GPS.get(); }
    public void setHealthGps(boolean value) { HEALTH_GPS.set(value); }
    public BooleanProperty healthGpsProperty() { return HEALTH_GPS; }

    public boolean isHealthBattery() { return HEALTH_BATTERY.get(); }
    public void setHealthBattery(boolean value) { HEALTH_BATTERY.set(value); }
    public BooleanProperty healthBatteryProperty() { return HEALTH_BATTERY; }

    public boolean isHealthMotor() { return HEALTH_MOTOR.get(); }
    public void setHealthMotor(boolean value) { HEALTH_MOTOR.set(value); }
    public BooleanProperty healthMotorProperty() { return HEALTH_MOTOR; }

    public boolean isHealthIMU() { return HEALTH_IMU.get(); }
    public void setHealthIMU(boolean value) { HEALTH_IMU.set(value); }
    public BooleanProperty healthIMUProperty() { return HEALTH_IMU; }

    // HEALTH_LOGGING
    public boolean isHealthLogging() { return HEALTH_LOGGING.get(); }
    public void setHealthLogging(boolean value) { HEALTH_LOGGING.set(value); }
    public BooleanProperty healthLoggingProperty() { return HEALTH_LOGGING; }

    // HEALTH_CAMERA
    public boolean isHealthCamera() { return HEALTH_CAMERA.get(); }
    public void setHealthCamera(boolean value) { HEALTH_CAMERA.set(value); }
    public BooleanProperty healthCameraProperty() { return HEALTH_CAMERA; }

    // HEALTH_STORAGE
    public boolean isHealthStorage() { return HEALTH_STORAGE.get(); }
    public void setHealthStorage(boolean value) { HEALTH_STORAGE.set(value); }
    public BooleanProperty healthStorageProperty() { return HEALTH_STORAGE; }

    // HEALTH_RADIO
    public boolean isHealthRadio() { return HEALTH_RADIO.get(); }
    public void setHealthRadio(boolean value) { HEALTH_RADIO.set(value); }
    public BooleanProperty healthRadioProperty() { return HEALTH_RADIO; }

    // HEALTH_PIXHAWK
    public boolean isHealthPixhawk() { return HEALTH_PIXHAWK.get(); }
    public void setHealthPixhawk(boolean value) { HEALTH_PIXHAWK.set(value); }
public BooleanProperty healthPixhawkProperty() { return HEALTH_PIXHAWK; }

    // General Properties
    public String getDroneName() { return DRONE_NAME.get(); }
    public void setDroneName(String value) { DRONE_NAME.set(value); }
    public StringProperty droneNameProperty() { return DRONE_NAME; }

    // Drone Kinematics
    public double getDroneAltitude() { return DRONE_ALTITUDE.get(); }
    public void setDroneAltitude(double value) { DRONE_ALTITUDE.set(value); }
    public DoubleProperty droneAltitudeProperty() { return DRONE_ALTITUDE; }

    public double getDroneSpeed() { return DRONE_SPEED.get(); }
    public void setDroneSpeed(double value) { DRONE_SPEED.set(value); }
    public DoubleProperty droneSpeedProperty() { return DRONE_SPEED; }

    public Position getDronePositionRelative() { return DRONE_POSITION_RELATIVE; }
    public void setDronePositionRelative(Position value) { DRONE_POSITION_RELATIVE = value; }

    public Position getDronePositionGlobal() { return DRONE_POSITION_GLOBAL; }
    public void setDronePositionGlobal(Position value) { DRONE_POSITION_GLOBAL = value; }

    public Vector3 getDroneAttitude() { return DRONE_ATTITUDE; }
    public void setDroneAttitude(Vector3 value) { DRONE_ATTITUDE = value; }

    public Vector3 getDroneVelocity() { return DRONE_VELOCITY; }
    public void setDroneVelocity(Vector3 value) { DRONE_VELOCITY = value; }

    public Vector3 getDroneAcceleration() { return DRONE_ACCELERATION; }
    public void setDroneAcceleration(Vector3 value) { DRONE_ACCELERATION = value; }

    // Drone Battery
    public double getDroneBatMaxCapacity() { return DRONE_BAT_MAX_CAPACITY.get(); }
    public void setDroneBatMaxCapacity(double value) { DRONE_BAT_MAX_CAPACITY.set(value); }
    public DoubleProperty droneBatMaxCapacityProperty() { return DRONE_BAT_MAX_CAPACITY; }

    public double getDroneBatCapacity() { return DRONE_BAT_CAPACITY.get(); }
    public void setDroneBatCapacity(double value) { DRONE_BAT_CAPACITY.set(value); }
    public DoubleProperty droneBatCapacityProperty() { return DRONE_BAT_CAPACITY; }

    public double getDroneMotorVoltageDraw() { return DRONE_MOTOR_VOLTAGE_DRAW.get(); }
    public void setDroneMotorVoltageDraw(double value) { DRONE_MOTOR_VOLTAGE_DRAW.set(value); }
    public DoubleProperty droneMotorVoltageDrawProperty() { return DRONE_MOTOR_VOLTAGE_DRAW; }

    // Radio
    public double getRadioConnectionBandwidth() { return RADIO_CONNECTION_BANDWIDTH.get(); }
    public void setRadioConnectionBandwidth(double value) { RADIO_CONNECTION_BANDWIDTH.set(value); }
    public DoubleProperty radioConnectionBandwidthProperty() { return RADIO_CONNECTION_BANDWIDTH; }

    // Flight
    public double getFlightElapsedTimeMs() { return FLIGHT_ELAPSED_TIME_MS.get(); }
    public void setFlightElapsedTimeMs(double value) { FLIGHT_ELAPSED_TIME_MS.set(value); }
    public DoubleProperty flightElapsedTimeMsProperty() { return FLIGHT_ELAPSED_TIME_MS; }

    public String getFlightElapsedTimeStringRep() { return FLIGHT_ELAPSED_TIME_STRING_REP.get(); }
    public void setFlightElapsedTimeStringRep(String value) { FLIGHT_ELAPSED_TIME_STRING_REP.set(value); }
    public StringProperty flightElapsedTimeStringRepProperty() { return FLIGHT_ELAPSED_TIME_STRING_REP; }

    public double getFlightNextTargetDist() { return FLIGHT_NEXT_TARGET_DIST.get(); }
    public void setFlightNextTargetDist(double value) { FLIGHT_NEXT_TARGET_DIST.set(value); }
    public DoubleProperty flightNextTargetDistProperty() { return FLIGHT_NEXT_TARGET_DIST; }

    public String getFlightName() { return FLIGHT_NAME.get(); }
    public void setFlightName(String value) { FLIGHT_NAME.set(value); }
    public StringProperty flightNameProperty() { return FLIGHT_NAME; }

    public String getFlightStatus() { return FLIGHT_STATUS.get(); }
    public void setFlightStatus(String value) { FLIGHT_STATUS.set(value); }
    public StringProperty flightStatusProperty() { return FLIGHT_STATUS; }

    // Camera
    public BufferedImage getBottomFrame() { return BOTTOM_CAMERA_FRAME; }
    public void setBottomFrame(BufferedImage frame) { BOTTOM_CAMERA_FRAME = frame; }

    public BufferedImage getFrontFrame() { return FRONT_CAMERA_FRAME; }
    public void setFrontFrame(BufferedImage frame) { FRONT_CAMERA_FRAME = frame; }

    // Misc
    public boolean getPayloadStatus() { return PAYLOAD_STATUS.get(); }
    public void setPayloadStatus(boolean value) { PAYLOAD_STATUS.set(value); }
    public BooleanProperty payloadStatusProperty() { return PAYLOAD_STATUS; }

    // --------------
    // CONSTRUCTOR
    // --------------

    public Data() {
        updateData();
    }

    // --------------
    // FUNCTION
    // --------------

    public void updateData() {}

    // --------------
    // CONTROL
    // --------------

    public void armDrone() {}
    public void startFlight(int storedFlightIndex) {}
    public void hardKill() {}
    public void autoLand() {}
    public void returnToHome() {}
}
