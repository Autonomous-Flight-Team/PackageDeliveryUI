package com.ui.pages;
import javafx.scene.layout.*;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import com.ui.*;
import javafx.geometry.Pos;

public class ConnectionPage implements Page {
    // --------------
    // VARIABLES
    // --------------

    private BorderPane main;
    private Data data;
    private Control control;
    private CommandService command;

    // --------------
    // GENERAL
    // --------------

    public ConnectionPage(Data data, Control control, CommandService command) {
        this.data = data;
        this.control = control;
        this.command = command;
        main = new BorderPane();
        main.setLeft(connectionPanel());
        main.setCenter(healthPanel());
        main.setRight(infoPanel());
        main.setStyle("-fx-background-color: #d7d7d3;");
    }

    public Pane getPane() {
        return main;
    }

    // --------------
    // CONNECTION
    // --------------

    private Pane connectionPanel() {
        StackPane connectionPanel = new StackPane();

        // Background
        Rectangle background = new Rectangle(270, 450, Color.web("#e7e7e7"));
        background.setArcWidth(30);
        background.setArcHeight(30);

        VBox leftBox = new VBox(10);

        Rectangle connectionTitleBg = new Rectangle(230, 50, Color.web("#373737"));
        connectionTitleBg.setArcWidth(30);
        connectionTitleBg.setArcHeight(30);
        Label connectionTitle = new Label("CONNECTION");
        connectionTitle.setTextFill(Color.WHITE);
        connectionTitle.setFont(new Font(20));
        StackPane connectionTitleStack = new StackPane(connectionTitleBg, connectionTitle);

        TextField addressField = new TextField("Address");
        addressField.getStyleClass().add("connect-field");
        addressField.setMaxWidth(200);

        TextField portField = new TextField("Port");
        portField.getStyleClass().add("connect-field");
        portField.setMaxWidth(200);

        Button connectButton = new Button("Connect");
        connectButton.setOnAction(e -> command.connectToDroneAsync());
        connectButton.setTextFill(Color.web("#1b1b1b"));
        connectButton.setStyle("-fx-background-color: #91e785");
        connectButton.setMinWidth(200);
        connectButton.setMinHeight(30);
        connectButton.setFont(new Font(20));

        Rectangle protocolTitleBg = new Rectangle(230, 50, Color.web("#373737"));
        protocolTitleBg.setArcWidth(30);
        protocolTitleBg.setArcHeight(30);
        Label protocolTitle = new Label("PROTOCOL");
        protocolTitle.setTextFill(Color.WHITE);
        protocolTitle.setFont(new Font(20));
        StackPane protocolTitleStack = new StackPane(protocolTitleBg, protocolTitle);

        Rectangle protocolBg = new Rectangle(200, 50, Color.web("#df9aed"));
        protocolBg.setArcWidth(30);
        protocolBg.setArcHeight(30);
        Label protocol = new Label(control.getControlType());
        protocol.setTextFill(Color.BLACK);
        protocol.setFont(new Font(20));
        StackPane protocolStack = new StackPane(protocolBg, protocol);

        Rectangle connectedBg = new Rectangle(200, 50);
        connectedBg.setArcWidth(30);
        connectedBg.setArcHeight(30);
        connectedBg.fillProperty().bind(
            Bindings.when(data.connectedToDroneProperty())
            .then(Color.web("#9aea7f"))
            .otherwise(Color.web("#ea7f7f"))
        );
        Label connected = new Label();
        connected.textProperty().bind(
            Bindings.when(data.connectedToDroneProperty())
            .then("Connected")
            .otherwise("Disconnected")
        );
        connected.setTextFill(Color.BLACK);
        connected.setFont(new Font(20));
        StackPane connectedStack = new StackPane(connectedBg, connected);
        

        leftBox.getChildren().addAll(connectionTitleStack, addressField, portField, connectButton,
            protocolTitleStack, protocolStack, connectedStack);
        leftBox.setAlignment(Pos.CENTER);

        connectionPanel.setPadding(new Insets(20));

        connectionPanel.getChildren().addAll(background, leftBox);
        return connectionPanel;
    }

    // --------------
    // HEALTH
    // --------------

    private Pane healthPanel() {
        VBox healthBox = new VBox(20);
        StackPane healthPanel = new StackPane();

        Rectangle background = new Rectangle(400, 350, Color.web("#e7e7e7"));
        background.setArcWidth(30);
        background.setArcHeight(30);

        GridPane healthGrid = new GridPane();

        healthGrid.add(healthItem("GPS", data.healthGpsProperty()), 0, 0);
        healthGrid.add(healthItem("BAT", data.healthBatteryProperty()), 0, 1);
        healthGrid.add(healthItem("MTR", data.healthMotorProperty()), 0, 2);
        healthGrid.add(healthItem("IMU", data.healthIMUProperty()), 1, 0);
        healthGrid.add(healthItem("LOG", data.healthLoggingProperty()), 1, 1);
        healthGrid.add(healthItem("CAM", data.healthCameraProperty()), 1, 2);
        healthGrid.add(healthItem("STR", data.healthStorageProperty()), 2, 0);
        healthGrid.add(healthItem("RDO", data.healthRadioProperty()), 2, 1);
        healthGrid.add(healthItem("PHK", data.healthRadioProperty()), 2, 2);

        healthGrid.setHgap(7.5);
        healthGrid.setVgap(7.5);

        healthGrid.setAlignment(Pos.CENTER);

        healthPanel.getChildren().addAll(background, healthGrid);
        healthPanel.setPadding(new Insets(0));

        // Rerun Tests

        Button testButton = new Button("Rerun Tests");
        testButton.setOnAction(e -> control.checkHealthAll());
        testButton.setTextFill(Color.web("#1b1b1b"));
        testButton.setStyle("-fx-background-color: #98afef");
        testButton.setMinWidth(300);
        testButton.setMinHeight(50);
        testButton.setFont(new Font(25));
        healthBox.setAlignment(Pos.CENTER);

        healthBox.getChildren().addAll(healthPanel, testButton);
        return healthBox;
    }

    // --------------
    // INFORMATION
    // --------------

    private Pane infoPanel() {
        // Load stack and background
        StackPane infoPanel = new StackPane();
        VBox infoBox = new VBox(10);
        
        // Info Panel Background
        Rectangle background = new Rectangle(250, 450, Color.web("#e7e7e7"));
        background.setArcWidth(30);
        background.setArcHeight(30);

        // INFO Header
        Rectangle infoTitleBg = new Rectangle(230, 50, Color.web("#373737"));
        infoTitleBg.setArcWidth(30);
        infoTitleBg.setArcHeight(30);
        Label infoTitle = new Label("INFO");
        infoTitle.setTextFill(Color.WHITE);
        infoTitle.setFont(new Font(20));
        StackPane infoTitleStack = new StackPane(infoTitleBg, infoTitle);

        // Drone Name Box
        Rectangle droneInfoBg = new Rectangle(200, 110, Color.web("#829D57"));
        droneInfoBg.setArcWidth(30);
        droneInfoBg.setArcHeight(30);
        Label droneInfoTitle = new Label("DRONE NAME");
        droneInfoTitle.setTextFill(Color.web("#ffffff"));
        droneInfoTitle.setFont(new Font(20));
        Label droneName = new Label();
        droneName.textProperty().bind(data.droneNameProperty());
        droneName.setTextFill(Color.web("#ffffff"));
        droneName.setFont(new Font(18));
        StackPane droneInfoStack = new StackPane(droneInfoBg, droneInfoTitle, droneName);
        droneInfoStack.setAlignment(droneInfoTitle, Pos.TOP_CENTER);
        droneInfoStack.setAlignment(droneName, Pos.BOTTOM_CENTER);
        droneInfoStack.setMargin(droneInfoTitle, new Insets(20, 0, 0, 0));
        droneInfoStack.setMargin(droneName, new Insets(0, 0, 20, 0));

        // Flight Name Box
        Rectangle flightNameBg = new Rectangle(200, 110, Color.web("#548CA8"));
        flightNameBg.setArcWidth(30);
        flightNameBg.setArcHeight(30);
        Label flightNameTitle = new Label("FLIGHT NAME");
        flightNameTitle.setTextFill(Color.web("#ffffff"));
        flightNameTitle.setFont(new Font(20));
        Label flightName = new Label();
        flightName.textProperty().bind(data.flightNameProperty());
        flightName.setTextFill(Color.web("#ffffff"));
        flightName.setFont(new Font(18));
        StackPane flightNameStack = new StackPane(flightNameBg, flightNameTitle, flightName);
        flightNameStack.setAlignment(flightNameTitle, Pos.TOP_CENTER);
        flightNameStack.setAlignment(flightName, Pos.BOTTOM_CENTER);
        flightNameStack.setMargin(flightNameTitle, new Insets(20, 0, 0, 0));
        flightNameStack.setMargin(flightName, new Insets(0, 0, 30, 0));

       // Available Flights Box
        Rectangle payloadBg = new Rectangle(200, 110, Color.web("#CB77B8"));
        payloadBg.setArcWidth(30);
        payloadBg.setArcHeight(30);
        Label payloadTitle = new Label("PAYLOAD");
        payloadTitle.setTextFill(Color.web("#ffffff"));
        payloadTitle.setFont(new Font(20));
        Label payloadLabel = new Label();
        payloadLabel.textProperty().bind(
            Bindings.when(data.payloadStatusProperty())
            .then("Loaded")
            .otherwise("Unloaded")
        );
        payloadLabel.setTextFill(Color.web("#ffffff"));
        payloadLabel.setFont(new Font(18));
        StackPane payloadStack = new StackPane(payloadBg, payloadTitle, payloadLabel);
        payloadStack.setAlignment(payloadTitle, Pos.TOP_CENTER);
        payloadStack.setAlignment(payloadLabel, Pos.BOTTOM_CENTER);
        payloadStack.setMargin(payloadTitle, new Insets(20, 0, 0, 0));
        payloadStack.setMargin(payloadLabel, new Insets(0, 0, 30, 0));

        // Item adding and return

        infoBox.getChildren().addAll(infoTitleStack, droneInfoStack, flightNameStack, payloadStack);
        infoBox.setAlignment(Pos.CENTER);
        infoPanel.getChildren().addAll(background, infoBox);
        infoPanel.setPadding(new Insets(20));
        return infoPanel;
    }

    private StackPane configItem(Label title, Label contents, Color fill) {
        StackPane itemStack = new StackPane();
        Rectangle background = new Rectangle(220, 100, fill);

        background.setArcWidth(10);
        background.setArcHeight(10);

        title.setFont(new Font(25));
        contents.setFont(new Font(20));

        itemStack.setAlignment(title, Pos.TOP_CENTER);
        itemStack.setAlignment(contents, Pos.BOTTOM_CENTER);
        itemStack.setMargin(title, new Insets(20, 0, 0, 0));
        itemStack.setMargin(contents, new Insets(0, 0, 30, 0));

        itemStack.getChildren().addAll(background, title, contents);

        return itemStack;
    }

    private StackPane healthItem(String title, BooleanProperty healthProperty) {
        StackPane itemStack = new StackPane();
        Rectangle background = new Rectangle(120, 100);

        background.setArcWidth(10);
        background.setArcHeight(10);

        Label titleLabel = new Label(title);
        Label statusLabel = new Label();

        titleLabel.setFont(new Font(20));
        statusLabel.setFont(new Font(25));

        background.fillProperty().bind(
            Bindings.when(healthProperty)
            .then(Color.web("#badfa0"))
            .otherwise(Color.web("#dfa0a0"))
        );

        statusLabel.textProperty().bind(
            Bindings.when(healthProperty)
            .then("GO")
            .otherwise("DEGO")
        );

        itemStack.setAlignment(titleLabel, Pos.TOP_CENTER);
        itemStack.setAlignment(statusLabel, Pos.BOTTOM_CENTER);
        itemStack.setMargin(titleLabel, new Insets(20, 0, 0, 0));
        itemStack.setMargin(statusLabel, new Insets(0, 0, 20, 0));

        itemStack.getChildren().addAll(background, titleLabel, statusLabel);

        return itemStack;
    }
}
