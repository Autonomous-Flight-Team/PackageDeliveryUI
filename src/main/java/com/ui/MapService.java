package com.ui;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ui.lib.*;

public class MapService {
    Data data;
    Control control;
    Settings settings;
    Logging logging;

    // Map reference parameters
    private ObjectProperty<Image> mapImage = new SimpleObjectProperty<Image>();

    private IntegerProperty droneX = new SimpleIntegerProperty(); // Need a scalable way of doing this. Probably an array of positions, for drone/home/target/waypoint1/../waypointN.
    private IntegerProperty droneY = new SimpleIntegerProperty(); // Then another for lines. Generate them later? I//
    private IntegerProperty targetX = new SimpleIntegerProperty();
    private IntegerProperty targetY = new SimpleIntegerProperty();
    private IntegerProperty homeX = new SimpleIntegerProperty();
    private IntegerProperty homeY = new SimpleIntegerProperty();

    private double bboxBottom;
    private double bboxTop;
    private double bboxRight;
    private double bboxLeft;

    private Thread mapThread;
    String baseURL;

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    private static final String ACCESS_TOKEN = "pk.eyJ1IjoiYWNvbnRpbnV1bSIsImEiOiJjbWxyaHNxajkwMmQxM2dweXR5ZXJtcW9jIn0.TWmv9387dLBhFMUGuHz12g";

    // --------------
    // GENERAL
    // --------------

    public MapService(Data data, Control control, Settings settings, Logging logging) {
        this.data = data;
        this.control = control;
        this.settings = settings;
        this.logging = logging;
        
        baseURL = ("https://api.mapbox.com/styles/v1/mapbox/satellite-v9/static/");
    }

    // --------------
    // GETTERS
    // --------------

    public ObjectProperty<Image> getMapImageObjectProperty() { return mapImage; }

    public IntegerProperty getDroneXProperty() { return droneX; }
    public IntegerProperty getDroneYProperty() { return droneY; }
    public IntegerProperty getHomeXProperty() { return homeX; }
    public IntegerProperty getHomeYProperty() { return homeY; }
    public IntegerProperty getTargetXProperty() { return targetX; }
    public IntegerProperty getTargetYProperty() { return targetY; }
    
    // --------------
    // FUNCTION
    // --------------

    public void start() {
        mapThread = new Thread(() -> {
            String lastBbox = "";
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (data.isConnectedToDrone()) {
                        String bbox = "";
                        
                        if(data.getAvailableFlights() != null) {
                            // Generate URL
                            bbox = computeBoundingBox(data.getAvailableFlights()[data.getSelectedFlight()], data.getDronePositionGlobal());
                        }

                        if(!bbox.equals(lastBbox)) {
                            MapData mapInfo = retrieveMapData(data.getAvailableFlights()[data.getSelectedFlight()], data.getDronePositionGlobal());
                            
                            if(mapInfo.getMap() != null) {
                                Platform.runLater(() -> mapImage.set(SwingFXUtils.toFXImage(mapInfo.getMap(), null)));

                                droneX.set((int) mapInfo.getDroneOffset().getX());
                                droneY.set((int) mapInfo.getDroneOffset().getY());

                                homeX.set((int) mapInfo.getHomeOffset().getX());
                                homeY.set((int) mapInfo.getHomeOffset().getY());

                                targetX.set((int) mapInfo.getTargetOffset().getX());
                                targetY.set((int) mapInfo.getTargetOffset().getY());
                            }

                            lastBbox = bbox;
                        }
                        Thread.sleep(settings.getMapPollRate());
                    }
                }catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                try{ 
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        mapThread.start();
    }

    public void stop() {
        mapThread.interrupt();
    }

    public MapData retrieveMapData(Flight flight, Position dronePosition) {
        MapData data = new MapData();

        // PRE COMPUTATION
        String bbox = computeBoundingBox(flight, dronePosition);

        // IMAGE FETCHING
        String newURL = baseURL + "[" + bbox + "]/" + settings.getMapResX() + "x" + settings.getMapResY() + "?access_token=" + ACCESS_TOKEN; 

        int responseCode = -1;

        try {
            HttpURLConnection mapConnection = (HttpURLConnection) new URL(newURL).openConnection();
            mapConnection.setRequestMethod("GET");
            mapConnection.setConnectTimeout(1000);
            mapConnection.setReadTimeout(1000);
            responseCode = mapConnection.getResponseCode();

            if(responseCode == 200) {
                BufferedImage image = ImageIO.read(mapConnection.getInputStream());
                logging.logInfo("Retrieved map imagery");
                data.setMap(image);
            } else {
                logging.logError("Failed to retrieve map data - HTTP " + responseCode);
            }

             mapConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            logging.logError("Failed to open map connection.");
        } 

        // DATA FORMATTING
        computeIconPositioning(data, flight, null);

        return data;
    }

    private void computeIconPositioning(MapData data, Flight flight, Position2 dronePosition) {
        Position homePosition = flight.getHome();
        Position payloadPosition = flight.getHome();
        Position targetPosition = flight.getTarget();

        Position2 droneOffset = new Position2();
        Position2 homeOffset = new Position2();
        Position2 payloadOffset = new Position2();
        Position2 targetOffset = new Position2();

        if(dronePosition != null) {
            droneOffset.setX(((dronePosition.getX() - bboxLeft)/(bboxRight - bboxLeft) * settings.getMapResX()) - settings.getMapResX()/2);
            droneOffset.setY((settings.getMapResY()/2 - ((dronePosition.getY() - bboxBottom)/(bboxTop - bboxBottom) * settings.getMapResY())));
        }
        
        homeOffset.setX(((homePosition.getX() - bboxLeft)/(bboxRight - bboxLeft) * settings.getMapResX()) - settings.getMapResX()/2);
        homeOffset.setY(settings.getMapResY()/2 - ((homePosition.getY() - bboxBottom)/(bboxTop - bboxBottom) * settings.getMapResY()));

        payloadOffset.setX(((payloadPosition.getX() - bboxLeft)/(bboxRight - bboxLeft) * settings.getMapResX()) - settings.getMapResX()/2);
        payloadOffset.setY(settings.getMapResY()/2 - ((payloadPosition.getY() - bboxBottom)/(bboxTop - bboxBottom) * settings.getMapResY()));

        targetOffset.setX(((targetPosition.getX() - bboxLeft)/(bboxRight - bboxLeft) * settings.getMapResX()) - settings.getMapResX()/2);
        targetOffset.setY(settings.getMapResY()/2 - ((targetPosition.getY() - bboxBottom)/(bboxTop - bboxBottom) * settings.getMapResY()));

        data.setDroneOffset(droneOffset);
        data.setHomeOffset(homeOffset);
        data.setPayloadOffset(payloadOffset);
        data.setTargetOffset(targetOffset);
    }

    private String computeBoundingBox(Flight currentFlight, Position dronePosition) {
        double top = currentFlight.getHome().getY();
        double right = currentFlight.getHome().getX();
        double bottom = currentFlight.getHome().getY();
        double left = currentFlight.getHome().getX();
        
        Set<Position> posSet = new HashSet<>(Arrays.asList(currentFlight.getWaypoints()));
        posSet.addAll(List.of(currentFlight.getPayload(), currentFlight.getTarget()));

        if(dronePosition != null) {
            posSet.add(dronePosition);
        }

        for(Position p : posSet) {
            if(p.getY() > top) {
                top = p.getY();
            }

            if(p.getY() < bottom) {
                bottom = p.getY();
            }

            if(p.getX() > right) {
                right = p.getX();
            }

            if(p.getX() < left) {
                left = p.getX();
            }
        }

        double width = right - left;
        double height = top - bottom;

        bboxBottom = (bottom - height/5);
        bboxLeft = (left - width/5);
        bboxTop =  (top + height/5);
        bboxRight = (right + width/5);

        return bboxBottom + "," + bboxLeft + "," + bboxTop + "," + bboxRight;
    }
}
