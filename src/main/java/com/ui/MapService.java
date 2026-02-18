package com.ui;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;

import javafx.scene.image.Image;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.ui.lib.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.*;
import java.util.*;

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
                            // Compute relative icon positioning
                            Platform.runLater(() -> computeIconPositioning());
                        }

                        if(!bbox.equals(lastBbox)) {
                            String newURL = baseURL + "[" + bbox + "]/" + settings.getMapResX() + "x" + settings.getMapResY() + "?access_token=" + ACCESS_TOKEN; 

                            // Perform map updates
                            HttpURLConnection mapConnection = (HttpURLConnection) new URL(newURL).openConnection();

                            mapConnection.setRequestMethod("GET");
                            mapConnection.setConnectTimeout(1000);
                            mapConnection.setReadTimeout(1000);

                            int responseCode = mapConnection.getResponseCode();
                            
                            if(responseCode == 200) {
                                BufferedImage image = ImageIO.read(mapConnection.getInputStream());
                                Platform.runLater(() -> mapImage.set(SwingFXUtils.toFXImage(image, null)));

                                logging.logInfo("Retrieved map imagery");
                            } else {
                                logging.logError("Failed to retrieve map data - HTTP " + responseCode);
                            }

                            mapConnection.disconnect();
                            lastBbox = bbox;
                        }
                        Thread.sleep(settings.getMapPollRate());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
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

    private void computeIconPositioning() {
        Position dronePosition = data.getDronePositionGlobal();
        Position homePosition = data.getAvailableFlights()[data.getSelectedFlight()].getHome();
        Position targetPosition = data.getAvailableFlights()[data.getSelectedFlight()].getTarget();

        int droneXOffset = (int) (((dronePosition.getX() - bboxLeft)/(bboxRight - bboxLeft) * settings.getMapResX()) - settings.getMapResX()/2);
        int droneYOffset = (int) (settings.getMapResY()/2 - ((dronePosition.getY() - bboxBottom)/(bboxTop - bboxBottom) * settings.getMapResY()) );

        int homeXOffset = (int) (((homePosition.getX() - bboxLeft)/(bboxRight - bboxLeft) * settings.getMapResX()) - settings.getMapResX()/2);
        int homeYOffset = (int) (settings.getMapResY()/2 - ((homePosition.getY() - bboxBottom)/(bboxTop - bboxBottom) * settings.getMapResY()));

        int targetXOffset = (int) (((targetPosition.getX() - bboxLeft)/(bboxRight - bboxLeft) * settings.getMapResX()) - settings.getMapResX()/2);
        int targetYOffset = (int) (settings.getMapResY()/2 - ((targetPosition.getY() - bboxBottom)/(bboxTop - bboxBottom) * settings.getMapResY()));

        System.out.println("Drone X/Y = " + droneXOffset + " / " + droneYOffset);
        System.out.println("Home X/Y = " + homeXOffset + " / " + homeYOffset);
        System.out.println("Target X/Y = " + targetXOffset + " / " + targetYOffset);

        droneX.set(droneXOffset);
        droneY.set(droneYOffset);

        homeX.set(homeXOffset);
        homeY.set(homeYOffset);

        targetX.set(targetXOffset);
        targetY.set(targetYOffset);
    }

    private String computeBoundingBox(Flight currentFlight, Position dronePosition) {
        double top = dronePosition.getY();
        double right = dronePosition.getX();
        double bottom = dronePosition.getY();
        double left = dronePosition.getX();
        
        Set<Position> posSet = new HashSet<>(Arrays.asList(currentFlight.getWaypoints()));
        posSet.addAll(List.of(currentFlight.getHome(), currentFlight.getTarget()));

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

        bboxBottom = bottom;//(bottom - height/5);
        bboxLeft = left;//(left - width/5);
        bboxTop =  top;//(top + height/5);
        bboxRight = right;//(right + width/5);

        return bboxBottom + "," + bboxLeft + "," + bboxTop + "," + bboxRight;
    }
}
