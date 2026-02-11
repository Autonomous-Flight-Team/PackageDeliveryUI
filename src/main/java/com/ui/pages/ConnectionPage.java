package com.ui.pages;
import javafx.scene.layout.*;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
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

    // --------------
    // GENERAL
    // --------------

    public ConnectionPage(Data data, Control control) {
        this.data = data;
        this.control = control;
        main = new BorderPane();
        main.setLeft(connectionPanel());
        main.setCenter(healthPanel());
        main.setRight(configurationPanel());
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
        Rectangle background = new Rectangle(270, 450, Color.web("#eae8e8"));
        background.setArcWidth(10);
        background.setArcHeight(10);

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

        TextField portField = new TextField("Port");
        portField.getStyleClass().add("connect-field");

        Button connectButton = new Button("Connect");
        connectButton.setOnAction(e -> control.tryConnectToDrone());
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

        leftBox.getChildren().addAll(connectionTitleStack, addressField, portField, connectButton,
            protocolTitleStack, protocolStack);
        leftBox.setAlignment(Pos.CENTER);

        leftBox.setPadding(new Insets(30));
        connectionPanel.setPadding(new Insets(20));

        connectionPanel.getChildren().addAll(background, leftBox);
        return connectionPanel;
    }

    // --------------
    // HEALTH
    // --------------

    private Pane healthPanel() {
        VBox healthBox = new VBox(10);
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
        healthPanel.setPadding(new Insets(20));

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
    // CONNECTION
    // --------------

    private Pane configurationPanel() {
        // Load stack and background
        StackPane configPanel = new StackPane();

        Rectangle configBackground = new Rectangle(250, 450, Color.AQUAMARINE);
        configBackground.setArcWidth(10);
        configBackground.setArcHeight(10);

        // Pane contents
        VBox configItems = new VBox(10);

        Label droneTitle = new Label("Drone Name");
        Label droneName = new Label();
        Label flightTitle = new Label("Flight Name");
        Label flightName = new Label();

        droneName.textProperty().bind(
            data.droneNameProperty()
        );

        flightName.textProperty().bind(
            data.flightNameProperty()
        );

        configItems.getChildren().addAll(configItem(droneTitle, droneName, Color.VIOLET),
         configItem(flightTitle, flightName, Color.VIOLET));

        // Item adding and return

        configPanel.getChildren().addAll(configBackground, configItems);
        configPanel.setPadding(new Insets(20));

        return configPanel;
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
        itemStack.setMargin(contents, new Insets(0, 0, 20, 0));

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
