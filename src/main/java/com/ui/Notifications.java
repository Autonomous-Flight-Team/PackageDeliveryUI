package com.ui;

import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Notifications {
    // --------------
    // VARIABLES
    // --------------

    private Stage stage;
    private IntegerProperty visibleNotificationCount = new SimpleIntegerProperty();
    private final ExecutorService executor = Executors.newFixedThreadPool(8);
    private Popup masterPopup;
    private VBox notificationStack;

    // --------------
    // GENERAL
    // --------------

    public Notifications(Stage stage) {
        this.stage = stage;
        masterPopup = new Popup();
        notificationStack = new VBox(10);
        masterPopup.getContent().add(notificationStack);
    }

    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();

                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    // Log shutdown failure.
                    Thread.currentThread().interrupt();
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    // --------------
    // PUBLIC NOTIFICATIONS
    // --------------

    public void noteError(String label) {
        showNotification(label, 2, "Images/error.png");
    }

    public void noteCaution(String label) {
        showNotification(label, 2, "Images/caution.png");
    }

    public void noteInfo(String label) {
        showNotification(label, 2, "Images/info.png");
    }

    public void noteError(String label, double duration) {
        showNotification(label, duration, "Images/error.png");
    }

    public void noteCaution(String label, double duration) {
        showNotification(label, duration, "Images/caution.png");
    }

    public void noteInfo(String label, double duration) {
        showNotification(label, duration, "Images/info.png");
    }

    // --------------
    // NOTIFICATION LOADER
    // --------------

    private void showNotification(String label, double duration, String imagePath) {
        Task<Void> notificationTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                DoubleProperty linePosition = new SimpleDoubleProperty(-80);
                DoubleProperty transparency = new SimpleDoubleProperty(); 

                Platform.runLater(() -> {
                    Pane notification = createNotification(label, imagePath);
                    Line progress = createNotificationLine(linePosition);
                    Button closeButton = createNotificationCloseButton();

                    closeButton.setOnAction(e -> notificationStack.getChildren().remove(notification));

                    notification.getChildren().addAll(progress, closeButton);
                    notificationStack.getChildren().add(notification); 
                    notification.opacityProperty().bind(transparency);
                
                    executor.execute(() -> {
                        double elapsed = 0;
                        while(elapsed < duration) {
                            if(elapsed/duration < .1) {
                                final double t = (elapsed/duration)/.1;
                                Platform.runLater(() -> transparency.set(t));
                            } else if(elapsed/duration > .9) {
                                final double t = (1 - (elapsed/duration))/.1;
                                Platform.runLater(() -> transparency.set(t));
                            } else {
                                transparency.set(1.0);
                            }
                            // Update popup duration.
                            final double pos = elapsed / duration * 150 - 80;
                            Platform.runLater(() -> linePosition.set(pos));
                            elapsed += .01;

                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }

                        Platform.runLater(() -> {
                            try { 
                                notificationStack.getChildren().remove(notification);
                            } catch (Exception e) {
                                // Means the notification was already closed.
                            }
                        
                        });
                    });
                });
                return null;
            }
        };

        executor.submit(notificationTask);
    }
    

    // --------------
    // UI
    // --------------

    public Pane createNotification(String label, String imagePath) {
        Rectangle background = new Rectangle(180, 70, Color.web("#222222"));
        background.setArcWidth(20);
        background.setArcHeight(20);

        StackPane notification = new StackPane();

        ImageView icon = new ImageView(new Image(imagePath));
        icon.setTranslateX(-55);
        icon.setFitHeight(50);
        icon.setFitWidth(50);
        icon.setTranslateY(-3);

        Label content = new Label(label);
        content.setFont(new Font(15));
        content.setTextFill(Color.web("#d6d6d6"));
        content.setTranslateX(55);
        content.setWrapText(true);
        content.setMaxWidth(110);
        content.setTranslateY(-3);

        notification.setAlignment(content, Pos.CENTER_LEFT);
        
        notification.getChildren().addAll(background, icon, content);
        return notification;
    }

    public Line createNotificationLine(DoubleProperty lineProgress) {
        Line progress = new Line(-80, -50, -80, -50);
        progress.setTranslateY(25);
        progress.setStroke(Color.web("#dbdbdb"));
        progress.setStrokeWidth(5);
        progress.setStrokeLineCap(StrokeLineCap.ROUND);
        progress.endXProperty().bind(lineProgress);
        return progress;
    }

    public Button createNotificationCloseButton() {
        Button closeButton = new Button();
                    
        closeButton.setStyle(
            "-fx-min-width: 10px; " +
            "-fx-min-height: 10px; " +
            "-fx-max-width: 10px; " +
            "-fx-max-height: 10px;"
        );

        closeButton.setBackground(new Background(new BackgroundFill(Color.web("#c84f4f"), new CornerRadii(8), null)));

        closeButton.setTranslateX(-75.5);
        closeButton.setTranslateY(-23);

        return closeButton;
    }

    // --------------
    // FUNCTION
    // --------------

    public void enableNotifications() {
        Task<Void> notificationTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(10);
                Platform.runLater(() -> {
                    masterPopup.show(stage); 

                    InvalidationListener reposition = obs -> {
                        masterPopup.setAnchorX(stage.getX() + stage.getWidth() - 196);
                        masterPopup.setAnchorY(stage.getY() + 48);
                    };

                    stage.xProperty().addListener(reposition);
                    stage.yProperty().addListener(reposition);
                    stage.widthProperty().addListener(reposition);
                    masterPopup.widthProperty().addListener(reposition);

                    // Set initial position
                    Platform.runLater(() -> reposition.invalidated(null));

                });

                return null;
            }
        };

        executor.submit(notificationTask);
    }
}
