package com.ui;

import java.awt.image.BufferedImage;

import com.ui.lib.*;

public class MapData {
    private BufferedImage mapImage;
    private Position2 droneOffset;
    private Position2 homeOffset;
    private Position2 payloadOffset;
    private Position2 targetOffset;
    private Position2[] waypoints;

    public MapData(BufferedImage mapImage, Position2 droneOffset, Position2 homeOffset, Position2 payloadOffset, Position2 targetOffset, Position2[] waypoints) {
        this.mapImage = mapImage;
        this.droneOffset = droneOffset;
        this.homeOffset = homeOffset;
        this.payloadOffset = payloadOffset;
        this.targetOffset = targetOffset;
        this.waypoints = waypoints;
    }

    public MapData() {
        this.mapImage = null;
        this.droneOffset = new Position2();
        this.homeOffset = new Position2();
        this.payloadOffset = new Position2();
        this.targetOffset = new Position2();
        this.waypoints = new Position2[0];
    }

    public BufferedImage getMap() {
        return mapImage;
    }

    public Position2 getDroneOffset() {
        return droneOffset;
    }

    public Position2 getHomeOffset() {
        return homeOffset;
    }

    public Position2 getPayloadOffset() {
        return payloadOffset;
    }

    public Position2 getTargetOffset() {
        return targetOffset;
    }

    public Position2[] getWaypoints() {
        return waypoints;
    }

    public void setMap(BufferedImage mapImage) {
        this.mapImage = mapImage;
    }

    public void setDroneOffset(Position2 droneOffset) {
        this.droneOffset = droneOffset;
    }

    public void setHomeOffset(Position2 homeOffset) {
        this.homeOffset = homeOffset;
    }

    public void setPayloadOffset(Position2 payloadOffset) {
        this.payloadOffset = payloadOffset;
    }

    public void setTargetOffset(Position2 targetOffset) {
        this.targetOffset = targetOffset;
    }

    public void setWaypoints(Position2[] waypoints) {
        this.waypoints = waypoints;
    }
}