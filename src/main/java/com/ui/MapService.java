package com.ui;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;

import javafx.scene.image.Image;
import javafx.beans.property.ObjectProperty;
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

    private ObjectProperty<Image> mapImage = new SimpleObjectProperty<Image>();
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
        
        baseURL = ("https://api.mapbox.com/styles/v1/mapbox/streets-v11/static/");
    }

    // --------------
    // GETTERS
    // --------------

    public ObjectProperty<Image> getMapImageObjectProperty() {
        return mapImage;
    }
    
    // --------------
    // FUNCTION
    // --------------

    public void start() {
        mapThread = new Thread(() -> {
            String lastBbox = "";
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (data.isConnectedToDrone()) {
                        // Generate URL
                        String bbox = computeBoundingBox(data.getAvailableFlights()[data.getSelectedFlight()], data.getDronePositionGlobal());
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try{ 
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        mapThread.start();
    }

    public void stop() {
        System.out.println("STOP CALL");
        mapThread.interrupt();
    }

    public String computeBoundingBox(Flight currentFlight, Position dronePosition) {
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

        return bottom + "," + left + "," + top + "," + right;
    }
}
