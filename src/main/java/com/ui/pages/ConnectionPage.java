package com.droneviewui;
import javafx.scene.layout.*;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.beans.binding.Bindings;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class ConnectionPage implements Page {
    // --------------
    // VARIABLES
    // --------------

    private BorderPane main;
    private Data data;

    // --------------
    // GENERAL
    // --------------

    public ConnectionPage(Data data) {
        this.data = data;
        main = new BorderPane();
        main.setLeft(connectionPanel());
        main.setCenter(healthPanel());
    }

    public Pane getPane() {
        return main;
    }

    // --------------
    // UI
    // --------------

    private Pane connectionPanel() {
        VBox leftBox = new VBox();

        Label connectionTitle = new Label("Connection Parameters");
        TextField addressField = new TextField("Enter the Drone Address");
        TextField portField = new TextField("Enter the Drone Port");
        Button connectButton = new Button("Connect");
        
        leftBox.getChildren().addAll(connectionTitle, addressField, portField, connectButton);
        leftBox.setPadding(new Insets(20));
        return leftBox;
    }

    private Pane healthPanel() {
        GridPane healthPanel = new GridPane();

        Label gpsTitle = new Label("GPS");
        Label gpsStatus = new Label();

        Label batteryTitle = new Label("BATT");
        Label batteryStatus = new Label();

        Label motorTitle = new Label("MOTOR");
        Label motorStatus = new Label();

        Label IMUTitle = new Label("IMU");
        Label IMUStatus = new Label();

        Label loggingTitle = new Label("LOG");
        Label loggingStatus = new Label();

        Label cameraTitle = new Label("CAM");
        Label cameraStatus = new Label();

        Label storageTitle = new Label("STRG");
        Label storageStatus = new Label();

        Label radioTitle = new Label("RADIO");
        Label radioStatus = new Label();

        Label pixhawkTitle = new Label("PHAWK");
        Label pixhawkStatus = new Label();

        gpsStatus.textProperty().bind(
            Bindings.when(data.healthGpsProperty())
            .then("GO")
            .otherwise("DEGO")
        );

        batteryStatus.textProperty().bind(
            Bindings.when(data.healthBatteryProperty())
            .then("GO")
            .otherwise("DEGO")
        );

        motorStatus.textProperty().bind(
            Bindings.when(data.healthMotorProperty())
            .then("GO")
            .otherwise("DEGO")
        );

        IMUStatus.textProperty().bind(
            Bindings.when(data.healthIMUProperty())
            .then("GO")
            .otherwise("DEGO")
        );

        loggingStatus.textProperty().bind(
            Bindings.when(data.healthLoggingProperty())
            .then("GO")
            .otherwise("DEGO")
        );

        cameraStatus.textProperty().bind(
            Bindings.when(data.healthCameraProperty())
            .then("GO")
            .otherwise("DEGO")
        );

        storageStatus.textProperty().bind(
            Bindings.when(data.healthStorageProperty())
            .then("GO")
            .otherwise("DEGO")
        );

        radioStatus.textProperty().bind(
            Bindings.when(data.healthRadioProperty())
            .then("GO")
            .otherwise("DEGO")
        );

        pixhawkStatus.textProperty().bind(
            Bindings.when(data.healthPixhawkProperty())
            .then("GO")
            .otherwise("DEGO")
        );

        healthPanel.add(healthItem(gpsTitle, gpsStatus), 0, 0);
        healthPanel.add(healthItem(batteryTitle, batteryStatus), 0, 1);
        healthPanel.add(healthItem(motorTitle, motorStatus), 0, 2);
        healthPanel.add(healthItem(IMUTitle, IMUStatus), 1, 0);
        healthPanel.add(healthItem(loggingTitle, loggingStatus), 1, 1);
        healthPanel.add(healthItem(cameraTitle, cameraStatus), 1, 2);
        healthPanel.add(healthItem(storageTitle, storageStatus), 2, 0);
        healthPanel.add(healthItem(radioTitle, radioStatus), 2, 1);
        healthPanel.add(healthItem(pixhawkTitle, pixhawkStatus), 2, 2);

        healthPanel.setPadding(new Insets(20));

        healthPanel.setHgap(7.5);
        healthPanel.setVgap(7.5);

        return healthPanel;

    }

    private StackPane healthItem(Label title, Label contents) {
        StackPane itemStack = new StackPane();
        Rectangle background = new Rectangle(120, 100, Color.STEELBLUE);

        background.setArcWidth(10);
        background.setArcHeight(10);

        title.setFont(new Font(20));
        contents.setFont(new Font(25));

        itemStack.setAlignment(title, Pos.TOP_CENTER);
        itemStack.setAlignment(contents, Pos.BOTTOM_CENTER);
        itemStack.setMargin(title, new Insets(20, 0, 0, 0));
        itemStack.setMargin(contents, new Insets(0, 0, 20, 0));

        itemStack.getChildren().addAll(background, title, contents);

        return itemStack;
    }

    private Pane configurationPanel() {
        VBox configPanel = new VBox();
        return configPanel;
    }
}
