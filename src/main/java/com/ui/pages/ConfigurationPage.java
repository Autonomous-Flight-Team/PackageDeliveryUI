package com.ui.pages;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import java.awt.Desktop;
import java.io.File;
import javafx.scene.control.Label;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;

import com.ui.*;

public class ConfigurationPage implements Page {
    // --------------
    // VARIABLES
    // --------------
    private BorderPane main;
    private CommandService command;
    private FileManager fileManager;
    private Data data;
    private Settings settings;
    private Pane[] menuPages;
    IntegerProperty selectedMenuPage = new SimpleIntegerProperty(0);
    
    // --------------
    // GENERAL
    // --------------

    public ConfigurationPage(Data data, CommandService command, Settings settings, FileManager fileManager) {
        this.command = command;
        this.data = data;
        this.settings = settings;
        this.fileManager = fileManager;
        menuPages = new Pane[2]; // Replace with number of pages
        menuPages[0] = telemetryPanel();
        menuPages[1] = systemPanel();
        main = new BorderPane();
        main.setLeft(menuPanel());
        main.setStyle("-fx-background-color: #d7d7d3;");
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
            new Button("Telemetry"),
            new Button("Drone"),
            new Button("System")
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
    // TELEMETRY OPTIONS
    // --------------

    private Pane telemetryPanel() {
        StackPane telemetryStack = new StackPane();
        Rectangle background = new Rectangle(760, 420, Color.web("#e7e7e7"));
        background.setArcWidth(30);
        background.setArcHeight(30);

        VBox options = new VBox(10);
        // Settings that should be included
        // - Telemetry poll rate
        Label connectionTitle = settingsTitleLabel("Connection Settings");
        Label portTitle = settingsItemLabel("Connection Port #");
        Label addressTitle = settingsItemLabel("Connection Address");
        Label pollTitle = settingsItemLabel("Telemetry Poll Rate");

        Label cameraTitle = settingsTitleLabel("Camera Settings");
        Label resXTitle = settingsItemLabel("Horizontal Camera Resolution");
        Label resYTitle = settingsItemLabel("Vertical Camera Resolution");

        TextField connectionAddressField = new TextField(settings.getConnectionAddress());
        TextField portConnectionField = new TextField((Integer.toString(settings.getConnectionPort())));
        TextField pollTextField = new TextField((Integer.toString(settings.getPollRate())));
        TextField resXField = new TextField(Integer.toString(settings.getCameraResX()));
        TextField resYField = new TextField(Integer.toString(settings.getCameraResY()));

        Button updateSettings = new Button("Update Settings");
        updateSettings.getStyleClass().add("update-settings-button");
        updateSettings.setTextFill(Color.web("#252525"));
        updateSettings.setOnAction(e -> {
            settings.setConnectionAddress(connectionAddressField.getText());
            settings.setConnectionPort(Integer.parseInt(portConnectionField.getText()));
            settings.setCameraResX(Integer.parseInt(resXField.getText()));
            settings.setCameraResY(Integer.parseInt(resYField.getText()));
            settings.setPollRate(Integer.parseInt(pollTextField.getText()));
        });

        options.getChildren().addAll(
            connectionTitle,
            settingStack(addressTitle, connectionAddressField),
            settingStack(portTitle, portConnectionField),
            settingStack(pollTitle, pollTextField),
            cameraTitle,
            settingStack(resXTitle, resXField),
            settingStack(resYTitle, resYField),
            updateSettings
        );

        options.setMaxHeight(700);
        options.setAlignment(Pos.CENTER);
        telemetryStack.setPadding(new Insets(30, 30, 0, 0));
        telemetryStack.getChildren().addAll(background, options);
        return telemetryStack;
    }

    // --------------
    // SYSTEM OPTIONS
    // --------------

    private Pane systemPanel() {
        StackPane systemStack = new StackPane();
        Rectangle background = new Rectangle(760, 420, Color.web("#e7e7e7"));
        background.setArcWidth(30);
        background.setArcHeight(30);

        VBox options = new VBox(10);

        Label generalTitle = settingsTitleLabel("General Settings");
        Label testTitle = settingsItemLabel("Enable Test Mode");
        Label saveLocationTitle = settingsItemLabel("Open Save Data Location");


        Label mapTitle = settingsTitleLabel("Map Settings");
        Label mapResTitle = settingsItemLabel("Map X Resolution");
        Label mapResYTitle = settingsItemLabel("Map Y Resolution");
        Label mapPollTitle = settingsItemLabel("Map Poll Rate (>1000)");

        TextField mapResYField = new TextField(Integer.toString(settings.getMapResY()));
        TextField mapPollField = new TextField(Integer.toString(settings.getMapPollRate()));

        CheckBox testModeBox = new CheckBox();
        testModeBox.setOnAction(e -> {
            settings.setTestMode(testModeBox.isSelected());
        });
        testModeBox.getStyleClass().add("option-box");
        testModeBox.setSelected(settings.getTestMode());
        Label testModeBoxText = new Label();
        testModeBoxText.textProperty().bind(
            Bindings.when(testModeBox.selectedProperty())
            .then("Enabled")
            .otherwise("Disabled")
        );

        // Save Location Styling
        Button saveLocationButton = new Button("Open");
        saveLocationButton.setOnAction(e -> openFileLocation(fileManager.getAppDataDir().toString()));
        saveLocationButton.getStyleClass().add("option-button");

        StackPane saveStack = new StackPane();
        AnchorPane saveAnchor = new AnchorPane();

        AnchorPane.setTopAnchor(saveLocationButton, 0.0);
        AnchorPane.setRightAnchor(saveLocationButton, -1.0);
        AnchorPane.setTopAnchor(saveLocationTitle, 10.0);
        AnchorPane.setLeftAnchor(saveLocationTitle, 16.0);
        saveAnchor.setMaxSize(700, 60);

        Rectangle saveLocationBackground = new Rectangle(700, 44, Color.web("#252525"));

        saveLocationBackground.setArcHeight(30);
        saveLocationBackground.setArcWidth(30);

        saveAnchor.getChildren().addAll(saveLocationTitle, saveLocationButton);
        saveStack.getChildren().addAll(saveLocationBackground, saveAnchor);

        // Test Mode Styling

        StackPane testStack = new StackPane();
        StackPane checkStack = new StackPane();
        AnchorPane testAnchor = new AnchorPane();
        checkStack.getChildren().addAll(testModeBox, testModeBoxText);

        AnchorPane.setTopAnchor(checkStack, 0.0);
        AnchorPane.setRightAnchor(checkStack, -1.0);
        AnchorPane.setTopAnchor(testTitle, 10.0);
        AnchorPane.setLeftAnchor(testTitle, 16.0);
        testAnchor.setMaxSize(700, 60);

        Rectangle testBackground = new Rectangle(700, 44, Color.web("#252525"));

        testBackground.setArcHeight(30);
        testBackground.setArcWidth(30);

        testAnchor.getChildren().addAll(testTitle, checkStack);
        testStack.getChildren().addAll(testBackground, testAnchor);

        TextField mapResField = new TextField(Integer.toString(settings.getMapResX()));

        Button updateSettings = new Button("Update Settings");
        updateSettings.getStyleClass().add("update-settings-button");
        updateSettings.setTextFill(Color.web("#252525"));
        updateSettings.setOnAction(e -> {
            settings.setTestMode(testModeBox.isSelected());
            settings.setMapResX(Integer.parseInt(mapResField.getText()));
            settings.setMapResY(Integer.parseInt(mapResYField.getText()));
            settings.setMapPollRate(Integer.parseInt(mapPollField.getText()));
        });

        options.getChildren().addAll(
            generalTitle,
            saveStack,
            testStack,
            mapTitle,
            settingStack(mapResTitle, mapResField),
            settingStack(mapResYTitle, mapResYField),
            settingStack(mapPollTitle, mapPollField),
            updateSettings
        );

        options.setMaxHeight(700);
        options.setAlignment(Pos.CENTER);
        systemStack.setPadding(new Insets(30, 30, 0, 0));
        systemStack.getChildren().addAll(background, options);
        return systemStack;
    }

    // --------------
    // UI
    // --------------

    private Pane settingStack(Label title, TextField field) {
        StackPane stack = new StackPane();
        AnchorPane anchor = new AnchorPane();

        AnchorPane.setTopAnchor(field, 0.0);
        AnchorPane.setRightAnchor(field, -1.0);
        AnchorPane.setTopAnchor(title, 10.0);
        AnchorPane.setLeftAnchor(title, 16.0);
        anchor.setMaxSize(700, 60);

        Rectangle background = new Rectangle(700, 44, Color.web("#252525"));
        
        background.setArcHeight(30);
        background.setArcWidth(30);

        field.getStyleClass().add("option-field");
        field.setMaxWidth(200);
        field.setMaxHeight(10);
        
        anchor.getChildren().addAll(title, field);
        stack.getChildren().addAll(background, anchor);
        
        return stack;
    }

    private Label settingsTitleLabel(String text) {
        Label title = new Label(text);
        title.setFont(new Font(20));
        title.setTextFill(Color.web("#202020"));
        return title;
    }
    private Label settingsItemLabel(String text) {
        Label title = new Label(text);
        title.setFont(new Font(18));
        title.setTextFill(Color.web("#ededed"));
        return title;
    }

    private Label warningItemLabel(String text) {
        Label title = new Label(text);
        title.setFont(new Font(18));
        title.setTextFill(Color.web("#992c2c"));
        return title;
    }

    // --------------
    // FUNCTION
    // --------------

    private void selectPage(int pageIndex) {
        selectedMenuPage.set(pageIndex);
        main.setRight(menuPages[selectedMenuPage.get()]);
    }

    public static void openFileLocation(String path) {
        Desktop.getDesktop().browseFileDirectory(new File(path));
    }
}
