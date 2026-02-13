package com.ui;
import org.json.simple.JSONObject;

import com.ui.lib.Position;
import com.ui.lib.Vector3;

import javafx.application.Platform;

public class TelemetryService {
    private final static int POLLING_RATE = 100; //ms
    Data data;
    Control control;
    private Thread telemetryThread;

    public TelemetryService(Data data, Control control) {
        this.data = data;
        this.control = control;
    }

    public void start() {
        System.out.println("Thread start call");
        telemetryThread = new Thread(() -> {
            boolean firstUpdate = true;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    
                    if (data.isConnectedToDrone()) {
                        if (firstUpdate) {
                            Platform.runLater(() -> data.setAvailableFlights(control.getAvailableFlights())); // Update flight info
                            firstUpdate = false;
                        }
                        
                        updateData(control.getTelemetryUpdate());
                        
                        // Platform.runLater(() -> {
                        //     data.setBottomFrame(control.getBottomCameraView());
                        //     data.setFrontFrame(control.getFrontCameraView());
                        // });
                    }
                    
                    Thread.sleep(POLLING_RATE);
                } catch (InterruptedException e) {
                    System.out.println("TELEM THREAD INTERUP");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        telemetryThread.start();
    }

    public void stop() {
        telemetryThread.interrupt();
    }

    private void updateData(JSONObject telemetryData) {
        // Data types
        JSONObject kinematics = (JSONObject) telemetryData.get("kinematics");
        JSONObject battery = (JSONObject) telemetryData.get("battery");
        JSONObject radio = (JSONObject) telemetryData.get("radio");
        JSONObject flight = (JSONObject) telemetryData.get("flight");
        JSONObject payload = (JSONObject) telemetryData.get("payload");

        // Kinematics
        JSONObject localPositionObject = (JSONObject) kinematics.get("positionRelative");
        Position localPosition = new Position((Double) localPositionObject.get("x"), (Double) localPositionObject.get("y"), (Double) localPositionObject.get("z"));
        
        JSONObject globalPositionObject = (JSONObject) kinematics.get("positionGlobal");
        Position globalPosition = new Position((Double) globalPositionObject.get("latitude"), (Double) globalPositionObject.get("longitude"), (Double) globalPositionObject.get("altitude"));
  
        JSONObject velocityObject = (JSONObject) kinematics.get("velocity");
        Vector3 velocity = new Vector3((Double) velocityObject.get("x"), (Double) velocityObject.get("y"), (Double) velocityObject.get("z"));

        JSONObject accelerationObject = (JSONObject) kinematics.get("acceleration");
        Vector3 acceleration = new Vector3((Double) accelerationObject.get("x"), (Double) accelerationObject.get("y"), (Double) accelerationObject.get("z"));


        Platform.runLater(() -> data.setDroneAltitude((Double) kinematics.get("altitude")));
        Platform.runLater(() -> data.setDroneSpeed((Double) kinematics.get("speed")));
        Platform.runLater(() -> data.setDronePositionRelative(localPosition));
        Platform.runLater(() -> data.setDronePositionGlobal(globalPosition));
        Platform.runLater(() -> data.setDroneVelocity(velocity));
        Platform.runLater(() -> data.setDroneAcceleration(acceleration));

        // Battery
        Platform.runLater(() -> data.setDroneBatMaxCapacity((Double) battery.get("maxCapacity")));
        Platform.runLater(() -> data.setDroneBatCapacity((Double) battery.get("currentCapacity")));
        Platform.runLater(() -> data.setDroneMotorVoltageDraw((Double) battery.get("motorVoltageDraw")));

        // Radio
        Platform.runLater(() -> data.setRadioConnectionBandwidth((Double) radio.get("connectionBandwidth")));
        
        // Flight
        Platform.runLater(() -> data.setFlightElapsedTimeMs((Double) flight.get("elapsedTimeMs")));
        Platform.runLater(() -> data.setFlightNextTargetDist((Double) flight.get("nextTargetDistance")));
        Platform.runLater(() -> data.setFlightName((String) flight.get("name")));
        Platform.runLater(() -> data.setFlightStatus((String) flight.get("status")));

        // Payload
        Platform.runLater(() -> data.setPayloadStatus((Boolean) payload.get("status")));
    }
};