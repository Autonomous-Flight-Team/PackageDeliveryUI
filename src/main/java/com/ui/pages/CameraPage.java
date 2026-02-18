package com.ui.pages;
import com.ui.*;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.*;
import javafx.application.Platform;
import java.awt.image.BufferedImage;
import javafx.beans.binding.Bindings;

public class CameraPage implements Page {
    // --------------
    // VARABLES
    // --------------
    private BorderPane main;

    private WritableImage bottomImage;
    private ImageView bottomImageView;
    
    private WritableImage frontImage;
    private ImageView frontImageView;

    private Data data;
    private CommandService command;

    // --------------
    // GENERAL
    // --------------

    public CameraPage(Data data, CommandService command) {
        this.data = data;
        this.command = command;
        main = new BorderPane();
        bottomImageView = new ImageView(new Image("Live/cameraFront.png"));
        frontImageView = new ImageView(new Image("Live/cameraFront.png"));
        main.setLeft(leftImagePanel());
        main.setCenter(optionPanel());
        main.setRight(rightImagePanel());
        main.setStyle("-fx-background-color: #d7d7d3;");
    }

    public Pane getPane() {
        return main;
    }


    // --------------
    // LOGIC
    // --------------

    public void updateFrames() {
        BufferedImage nextBottomFrame = data.getBottomFrame();
        BufferedImage nextFrontFrame = data.getFrontFrame();

        SwingFXUtils.toFXImage(nextBottomFrame, bottomImage);
        SwingFXUtils.toFXImage(nextFrontFrame, frontImage);

        Platform.runLater(() -> bottomImageView.setImage(null));
        Platform.runLater(() -> bottomImageView.setImage(bottomImage));

        Platform.runLater(() -> frontImageView.setImage(null));
        Platform.runLater(() -> frontImageView.setImage(frontImage));
    }

    // --------------
    // UI
    // --------------

    Pane leftImagePanel() {
        StackPane leftPanel = new StackPane();

        Rectangle background = new Rectangle(400, 450, Color.web("#e7e7e7"));
        background.setArcHeight(30);
        background.setArcWidth(30);

        VBox leftBox = new VBox(10);
        leftBox.setAlignment(Pos.CENTER);
        

        Label title = titleText("Bottom View");

        bottomImageView.setFitWidth(370);
        bottomImageView.setFitHeight(370);

        leftBox.getChildren().addAll(title, bottomImageView);

        leftPanel.getChildren().addAll(background, leftBox);
        leftPanel.setPadding(new Insets(20));
        
        return leftPanel;
    }

        private Pane optionPanel() {
        StackPane optionPanel = new StackPane();
        Rectangle background = new Rectangle(190, 395, Color.web("#e7e7e7"));
        background.setArcHeight(30);
        background.setArcWidth(30);

        VBox optionPane = new VBox(10);
        optionPane.setAlignment(Pos.CENTER);

        // Create buttons and logic
        Button armButton = new Button("ARM");
        armButton.setOnAction(e -> command.tryArmDroneAsync());
        Button flyButton = new Button("FLY");
        flyButton.setOnAction(e -> command.startFlightAsync());
        Button returnButton = new Button("RTH");
        returnButton.setOnAction(e -> command.returnHomeAsync());
        Button landButton = new Button("LAND");
        landButton.setOnAction(e -> command.landAsync());
        Button killButton = new Button("KILL");
        killButton.setOnAction(e -> command.killAsync());
        
        armButton.backgroundProperty().bind(
            Bindings.when(data.connectedToDroneProperty())
                    .then(new Background(new BackgroundFill(
                    Color.web("#8aaae5"), 
                    new CornerRadii(8), null)))
                    .otherwise(new Background(new BackgroundFill(
                    Color.web("#232323"), 
                    new CornerRadii(8), null))));

        armButton.styleProperty().bind(
            Bindings.when(data.connectedToDroneProperty())
                    .then("-fx-text-fill: #232323")
                    .otherwise("-fx-text-fill: #f3f3f3"));

        flyButton.backgroundProperty().bind(
            Bindings.when(data.droneArmedProperty())
                    .then(new Background(new BackgroundFill(
                    Color.web("#c998ee"), 
                    new CornerRadii(8), null)))
                    .otherwise(new Background(new BackgroundFill(
                    Color.web("#232323"), 
                    new CornerRadii(8), null))));

        flyButton.styleProperty().bind(
            Bindings.when(data.droneArmedProperty())
                    .then("-fx-text-fill: #232323")
                    .otherwise("-fx-text-fill: #f3f3f3"));

        returnButton.backgroundProperty().bind(
            Bindings.when(data.droneInFlightProperty())
                    .then(new Background(new BackgroundFill(
                    Color.web("#ffff6f"), 
                    new CornerRadii(8), null)))
                    .otherwise(new Background(new BackgroundFill(
                    Color.web("#232323"), 
                    new CornerRadii(8), null))));

        returnButton.styleProperty().bind(
            Bindings.when(data.droneInFlightProperty())
                    .then("-fx-text-fill: #232323")
                    .otherwise("-fx-text-fill: #f3f3f3"));

        landButton.backgroundProperty().bind(
            Bindings.when(data.droneInFlightProperty())
                    .then(new Background(new BackgroundFill(
                    Color.web("#dcb064"), 
                    new CornerRadii(8), null)))
                    .otherwise(new Background(new BackgroundFill(
                    Color.web("#232323"), 
                    new CornerRadii(8), null))));

        landButton.styleProperty().bind(
            Bindings.when(data.droneInFlightProperty())
                    .then("-fx-text-fill: #232323")
                    .otherwise("-fx-text-fill: #f3f3f3"));

        killButton.backgroundProperty().bind(
            Bindings.when(data.connectedToDroneProperty())
                    .then(new Background(new BackgroundFill(
                    Color.web("#e67b73"), 
                    new CornerRadii(8), null)))
                    .otherwise(new Background(new BackgroundFill(
                    Color.web("#232323"), 
                    new CornerRadii(8), null))));

        killButton.styleProperty().bind(
            Bindings.when(data.connectedToDroneProperty())
                    .then("-fx-text-fill: #232323")
                    .otherwise("-fx-text-fill: #f3f3f3"));

                
        // Style buttons
        armButton.getStyleClass().addAll("flight-option-button", "flight-option-button-unavailable");
        flyButton.getStyleClass().addAll("flight-option-button", "flight-option-button-unavailable");
        returnButton.getStyleClass().addAll("flight-option-button", "flight-option-button-unavailable");
        landButton.getStyleClass().addAll("flight-option-button", "flight-option-button-unavailable");
        killButton.getStyleClass().addAll("flight-option-button", "flight-option-button-unavailable");
        
        optionPane.getChildren().addAll(armButton, flyButton, returnButton, landButton, killButton);
        optionPanel.getChildren().addAll(background, optionPane);
        return optionPanel;
    }

    Pane rightImagePanel() {
        StackPane rightPanel = new StackPane();
        VBox rightBox = new VBox(10);
        Rectangle background = new Rectangle(400, 450, Color.web("#e7e7e7"));
        background.setArcHeight(30);
        background.setArcWidth(30);
        rightBox.setAlignment(Pos.CENTER);
        

        Label title = titleText("Front View");

        frontImageView.setFitWidth(370);
        frontImageView.setFitHeight(370);

        rightBox.getChildren().addAll(title, frontImageView);
        rightPanel.setPadding(new Insets(20));
        rightPanel.getChildren().addAll(background, rightBox);
        return rightPanel;
    }

    private Label titleText(String text) {
        Label titleLabel = new Label(text);

        titleLabel.setFont(new Font(24));
        titleLabel.setTextFill(Color.BLACK);

        return titleLabel;
    }
 }
