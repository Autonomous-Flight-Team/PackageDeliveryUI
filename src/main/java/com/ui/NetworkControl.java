package com.ui;

import com.ui.lib.*;
import com.proto.*;
import com.proto.CommandOuterClass.Command;
import com.proto.ContextCommandOuterClass.ContextCommand;

import java.awt.image.BufferedImage;

import org.json.simple.JSONObject;

public class NetworkControl extends Control {
    private String controlType;
    private NetworkHandler networkHandler;

    public NetworkControl(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }
    
    public String getControlType() {
        return controlType;
    }

    // --------------
    // CONNECTION
    // --------------

    public void tryConnectToDrone() {
        networkHandler.HandleEvent(NetworkEvent.TRY_CONNECT);
    }

    public void tryDisconnectFromDrone() {
        networkHandler.HandleEvent(NetworkEvent.TRY_DISCONNECT);
    }

    // --------------
    // COMMANDS
    // --------------

    public void tryArmDrone() {
        Command command = Command.newBuilder()
            .setCommandIndex(0)
            .build();
        networkHandler.HandleTXPacket(new Packet("COMMAND", 0, command.toByteArray()));
    }

    public void tryDisarmDrone() {
        Command command = Command.newBuilder()
            .setCommandIndex(1)
            .build();
        networkHandler.HandleTXPacket(new Packet("COMMAND", 0, command.toByteArray()));
    }
    
    public void tryStartFlight() {
        Command command = Command.newBuilder()
            .setCommandIndex(2)
            .build();
        networkHandler.HandleTXPacket(new Packet("COMMAND", 0, command.toByteArray()));
    }

    public void tryReturnToHome() {
        Command command = Command.newBuilder()
            .setCommandIndex(3)
            .build();
        networkHandler.HandleTXPacket(new Packet("COMMAND", 0, command.toByteArray()));
    }

    public void tryLand() {
        Command command = Command.newBuilder()
            .setCommandIndex(4)
            .build();
        networkHandler.HandleTXPacket(new Packet("COMMAND", 0, command.toByteArray()));
    }

    public void tryKill() {
        Command command = Command.newBuilder()
            .setCommandIndex(5)
            .build();
        networkHandler.HandleTXPacket(new Packet("COMMAND", 0, command.toByteArray()));
    }

    // --------------
    // TELEMETRY
    // --------------

    public void getAvailableFlights() {
        Command command = Command.newBuilder()
            .setCommandIndex(6)
            .build();
        networkHandler.HandleTXPacket(new Packet("COMMAND", 0, command.toByteArray()));
    }

    // --------------
    // HEALTH
    // --------------

    public void checkHeathGPS() {
        ContextCommand checkHealth = ContextCommand.newBuilder()
            .setCommandIndex(0)
            .setCommandContext(0)
            .build();
        networkHandler.HandleTXPacket(new Packet("CONTEXT_COMMAND", 0, checkHealth.toByteArray()));
    }

    public void checkHeathIMU() {
        ContextCommand checkHealth = ContextCommand.newBuilder()
            .setCommandIndex(0)
            .setCommandContext(1)
            .build();
        networkHandler.HandleTXPacket(new Packet("CONTEXT_COMMAND", 0, checkHealth.toByteArray()));
    }

    public void checkHeathRadio() {
        ContextCommand checkHealth = ContextCommand.newBuilder()
            .setCommandIndex(0)
            .setCommandContext(2)
            .build();
        networkHandler.HandleTXPacket(new Packet("CONTEXT_COMMAND", 0, checkHealth.toByteArray()));
    }

    public void checkHeathMotors() {
        ContextCommand checkHealth = ContextCommand.newBuilder()
            .setCommandIndex(0)
            .setCommandContext(3)
            .build();
        networkHandler.HandleTXPacket(new Packet("CONTEXT_COMMAND", 0, checkHealth.toByteArray()));
    }

    public void checkHeathBattery() {
        ContextCommand checkHealth = ContextCommand.newBuilder()
            .setCommandIndex(0)
            .setCommandContext(4)
            .build();
        networkHandler.HandleTXPacket(new Packet("CONTEXT_COMMAND", 0, checkHealth.toByteArray()));
    }

    public void checkHeathStorage() {
        ContextCommand checkHealth = ContextCommand.newBuilder()
            .setCommandIndex(0)
            .setCommandContext(5)
            .build();
        networkHandler.HandleTXPacket(new Packet("CONTEXT_COMMAND", 0, checkHealth.toByteArray()));
    }

    public void checkHeathLogging() {
        ContextCommand checkHealth = ContextCommand.newBuilder()
            .setCommandIndex(0)
            .setCommandContext(6)
            .build();
        networkHandler.HandleTXPacket(new Packet("CONTEXT_COMMAND", 0, checkHealth.toByteArray()));
    }

    public void checkHeathPixhawk() {
        ContextCommand checkHealth = ContextCommand.newBuilder()
            .setCommandIndex(0)
            .setCommandContext(7)
            .build();
        networkHandler.HandleTXPacket(new Packet("CONTEXT_COMMAND", 0, checkHealth.toByteArray()));
    }

    public void checkHeathCameras() {
        ContextCommand checkHealth = ContextCommand.newBuilder()
            .setCommandIndex(0)
            .setCommandContext(8)
            .build();
        networkHandler.HandleTXPacket(new Packet("CONTEXT_COMMAND", 0, checkHealth.toByteArray()));
    }

    public void checkHealthAll() {
        ContextCommand checkHealth = ContextCommand.newBuilder()
            .setCommandIndex(0)
            .setCommandContext(9)
            .build();
        networkHandler.HandleTXPacket(new Packet("CONTEXT_COMMAND", 0, checkHealth.toByteArray()));
    }
    // --------------
    // TESTS
    // --------------
    public void runTestMotorSpinAll() {
        ContextCommand runTest = ContextCommand.newBuilder()
            .setCommandIndex(1)
            .setCommandContext(0)
            .build();
        networkHandler.HandleTXPacket(new Packet("CONTEXT_COMMAND", 0, runTest.toByteArray()));
    }

    public void runTestMotorSpinSequence() {
        ContextCommand runTest = ContextCommand.newBuilder()
            .setCommandIndex(1)
            .setCommandContext(1)
            .build();
        networkHandler.HandleTXPacket(new Packet("CONTEXT_COMMAND", 0, runTest.toByteArray()));
    }

    public void runTestPayloadActuate() {
        ContextCommand runTest = ContextCommand.newBuilder()
            .setCommandIndex(1)
            .setCommandContext(2)
            .build();
        networkHandler.HandleTXPacket(new Packet("CONTEXT_COMMAND", 0, runTest.toByteArray()));
    }
}
