package com.ui.pages;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.beans.binding.Bindings;
import javafx.scene.shape.Rectangle;

import com.ui.*;

public class CalibrationPage implements Page {
    // --------------
    // VARIABLES
    // --------------

    private BorderPane main;
    private Data data;
    private CommandService command;
    private Notifications notifications;

    private Pane[] menuPages;
    IntegerProperty selectedMenuPage = new SimpleIntegerProperty(0);

    // --------------
    // GENERAL
    // --------------

    public CalibrationPage(Data data, CommandService command, Notifications notifications) {
        this.data = data;
        this.command = command;
        this.notifications = notifications;

        main = new BorderPane();
        main.setLeft(menuPanel());

        menuPages = new Pane[3];
        menuPages[0] = motorPanel();
        menuPages[1] = payloadPanel();
        menuPages[2] = sensorPanel();
        selectPage(0);
    }

    public Pane getPane() {
        return main;
    }

    // --------------
    // MENU
    // --------------

    private Pane menuPanel() {
        StackPane menuPanel = new StackPane();
        Rectangle background = new Rectangle(200, 420, Color.web("#e7e7e7"));
        background.setArcWidth(30);
        background.setArcHeight(30);

        VBox menuBox = new VBox(10);

        Button[] menuButtons = {
            new Button("Motors"),
            new Button("Payload"),
            new Button("Sensors")
        };

        for(int i = 0; i < menuButtons.length; i++) {
            final int index = i;
            menuButtons[index].setOnAction(e -> selectPage(index)); // Set button function
            menuButtons[index].getStyleClass().addAll("menu-button");
            menuButtons[index].backgroundProperty().bind(
                    Bindings.createObjectBinding(
                            () -> selectedMenuPage.get() == index 
                                ? new Background(new BackgroundFill(
                                Color.web("#7dd1e4"), 
                                new CornerRadii(8), null))
                                : new Background(new BackgroundFill(
                                Color.web("#313131"), 
                                new CornerRadii(8), null)),
                                selectedMenuPage ));

            menuButtons[index].styleProperty().bind(
                    Bindings.createObjectBinding(
                            () -> selectedMenuPage.get() == index 
                                ? "-fx-text-fill: #232323"
                                : "-fx-text-fill: #eeeeee",
                                selectedMenuPage));


            menuBox.getChildren().add(menuButtons[index]);
        }

        menuBox.setAlignment(Pos.CENTER);

        menuPanel.getChildren().addAll(background, menuBox);
        menuPanel.setPadding(new Insets(30, 0, 0, 30));
    
        return menuPanel;
    }

    // --------------
    // MOTOR
    // --------------

    private Pane motorPanel() {
        // Pane and background creation
        StackPane motorPanel = new StackPane();
        VBox motorBox = new VBox(10);
        GridPane tests = new GridPane(10, 10);

        Rectangle background = new Rectangle(760, 420, Color.web("#e7e7e7"));
        background.setArcWidth(30);
        background.setArcHeight(30);
        
        // Element creation

        Label titleLabel = new Label("Motor Tests");
        titleLabel.setFont(new Font(24));

        Button allMotorButton = testButton("All Motor Spinup", data.testStatusMotorSpinAllProperty());
        Button seqMotorButton = testButton("Sequential Motor Spinup", data.testStatusMotorSpinSeqProperty());

        // Element behaviour
        allMotorButton.setOnAction(e -> {
            if(data.isConnectedToDrone()) {
                notifications.noteInfo("Starting test");
                command.testMotorSpinAllAsync();
            } else {
                notifications.noteCaution("Not connected to drone");
            }
        });

        seqMotorButton.setOnAction(e -> {
            if(data.isConnectedToDrone()) {
                notifications.noteInfo("Starting test");
                command.testMotorSpinSeqAsync();
            } else {
                notifications.noteCaution("Not connected to drone");
            }
        });

        // Element adding and return

        tests.add(allMotorButton, 0, 0);
        tests.add(seqMotorButton, 0, 1);

        tests.setAlignment(Pos.CENTER);
        motorBox.setAlignment(Pos.CENTER);


        motorBox.getChildren().addAll(titleLabel, tests);

        motorPanel.getChildren().addAll(background, motorBox);

        return motorPanel;
    }

    // --------------
    // PAYLOAD
    // --------------

    private Pane payloadPanel() {
        // Pane and background creation
        StackPane payloadPanel = new StackPane();
        VBox payloadBox = new VBox(10);
        GridPane tests = new GridPane(10, 10);

        Rectangle background = new Rectangle(760, 420, Color.web("#e7e7e7"));
        background.setArcWidth(30);
        background.setArcHeight(30);
        
        // Element creation

        Label titleLabel = new Label("Payload Tests");
        titleLabel.setFont(new Font(24));

        Button payloadAcq = testButton("Payload Acquisition", data.testStatusPayloadAcqProperty());

        // Element behaviour
        payloadAcq.setOnAction(e -> {
            if(data.isConnectedToDrone()) {
                notifications.noteInfo("Starting test");
                command.testPayloadAcqAsync();
            } else {
                notifications.noteCaution("Not connected to drone");
            }
        });

        // Element adding and return

        tests.add(payloadAcq, 0, 0);

        tests.setAlignment(Pos.CENTER);
        payloadBox.setAlignment(Pos.CENTER);


        payloadBox.getChildren().addAll(titleLabel, tests);

        payloadPanel.getChildren().addAll(background, payloadBox);

        return payloadPanel;
    }

    // --------------
    // SENSORS
    // --------------

    private Pane sensorPanel() {
        // Pane and background creation
        StackPane sensorPanel = new StackPane();
        VBox sensorBox = new VBox(10);
        GridPane tests = new GridPane(10, 10);

        Rectangle background = new Rectangle(760, 420, Color.web("#e7e7e7"));
        background.setArcWidth(30);
        background.setArcHeight(30);
        
        // Element creation

        Label titleLabel = new Label("Sensor Tests");
        titleLabel.setFont(new Font(24));

        // Element adding and return

        tests.setAlignment(Pos.CENTER);
        sensorBox.setAlignment(Pos.CENTER);


        sensorBox.getChildren().addAll(titleLabel, tests);

        sensorPanel.getChildren().addAll(background, sensorBox);

        return sensorPanel;
    }

    // --------------
    // UI
    // --------------

    private Button testButton(String label, BooleanProperty testStatusProperty) {
        Button testButton = new Button(label);
        testButton.getStyleClass().addAll("test-button");

        testButton.backgroundProperty().bind(
            Bindings.when(testStatusProperty)
            .then(new Background(new BackgroundFill(
                                Color.web("#77d7e5"), 
                                new CornerRadii(8), null)))
            .otherwise(new Background(new BackgroundFill(
                                Color.web("#242424"), 
                                new CornerRadii(8), null)))
        );

        testButton.textFillProperty().bind(
            Bindings.when(testStatusProperty)
            .then(Color.web("#1b1b1b"))
            .otherwise(Color.web("#f0f0f0"))
        );

        return testButton;
    }

    // --------------
    // FUNCTION
    // --------------

    private void selectPage(int pageIndex) {
        selectedMenuPage.set(pageIndex);
        main.setRight(menuPages[selectedMenuPage.get()]);
    }

    
}
