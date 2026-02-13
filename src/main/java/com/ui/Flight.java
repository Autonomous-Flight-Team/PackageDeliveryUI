package com.ui;

import com.ui.lib.Position;

public class Flight {
    private String flightName;
    private String flightDescription;
    private Position home;
    private Position target; 
    private Position[] waypoints;

    public Flight(String flightName, String flightDescription, Position home, Position target, Position[] waypoints) {
        setFlightName(flightName);
        setFlightDescription(flightDescription);
        setHome(home);
        setTarget(target);
        setWaypoints(waypoints);
    }

    public void setFlightName(String flightName) {
        this.flightName = flightName;
    }

    public void setFlightDescription(String flightDescription) {
        this.flightDescription = flightDescription;
    }

    public void setHome(Position home) {
        this.home = home;
    }

    public void setTarget(Position target) {
        this.target = target;
    }

    public void setWaypoints(Position[] waypoints) {
        this.waypoints = waypoints;
    }

    public String getFlightName() {
        return flightName;
    }

    public String getFlightDescription() {
        return flightDescription;
    }

    public Position getHome() {
        return home;
    }

    public Position getTarget() {
        return target;
    }

    public Position[] getWaypoints() {
        return waypoints;
    }
}
