package com.ui.pages;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Insets;
import com.ui.*;
import javafx.geometry.Pos;
import javafx.beans.binding.Bindings;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FlightPage implements Page {
    // --------------
    // VARABLES
    // --------------

    // References

    private BorderPane main;
    private Data data;
    private Control control;
    private Image mapImage;


    // --------------
    // GENERAL
    // --------------

    public FlightPage(Data data, Control control) {
        this.data = data;
        this.control = control;
        main = new BorderPane();

        Pane stats = statsPanel();
        Pane status = statusPanel();
        Pane options = optionPanel();
        Pane map = mapPanel();

        main.setRight(stats);
        main.setBottom(status);
        main.setCenter(options);
        main.setLeft(map);
    }

    public Pane getPane() {
        return main;
    }

    // --------------
    // PANES
    // --------------

    private StackPane mapPanel() {
        // TODO functionality: pull position data to get imagery from google maps, used center,
        // dir, xwidth and ywidth to place the drone icons. Placehold locations for now - build out 
        // placement functionality in modules
        mapImage = new Image("Images/mapUnavailable.png");
        ImageView mapView = new ImageView();
        mapView.setImage(mapImage);
        mapView.setFitWidth(390);
        mapView.setFitHeight(390);

        //Image droneIcon = new Image("Images/droneIcon.png");
        //Image homeIcon = new Image("Images/droneIcon.png");
        //Image targetIcon = new Image("Images/droneIcon.png");

        StackPane mapStack = new StackPane();

        mapStack.getChildren().add(mapView);
        mapStack.setPadding(new Insets(10, 20, 0, 20));
        return mapStack;
    }

    private VBox optionPanel() {
        VBox optionPane = new VBox(10);
        optionPane.setAlignment(Pos.CENTER);

        // Create buttons and logic
        Button armButton = new Button("ARM");
        armButton.setOnAction(e -> control.tryArmDrone());
        Button returnButton = new Button("RTH");
        returnButton.setOnAction(e -> control.tryReturnToHome());
        Button landButton = new Button("LAND");
        landButton.setOnAction(e -> control.tryLand());
        Button killButton = new Button("KILL");
        killButton.setOnAction(e -> control.tryKill());
        
        // Button background logic
        armButton.backgroundProperty().bind(
            Bindings.createObjectBinding(
                () -> data.droneArmedProperty().get() == true 
                    ? new Background(new BackgroundFill(
                    Color.web("#232323"), 
                    new CornerRadii(8), null))
                    : new Background(new BackgroundFill(
                    Color.web("#5481d4"), 
                    new CornerRadii(8), null))));
        

        returnButton.backgroundProperty().bind(
            Bindings.createObjectBinding(
                () -> data.droneArmedProperty().get() == true 
                    ? new Background(new BackgroundFill(
                    Color.web("#d3d757"), 
                    new CornerRadii(8), null))
                    : new Background(new BackgroundFill(
                    Color.web("#232323"), 
                    new CornerRadii(8), null))));

        landButton.backgroundProperty().bind(
            Bindings.createObjectBinding(
                () -> data.droneArmedProperty().get() == true 
                    ? new Background(new BackgroundFill(
                    Color.web("#d7a257"), 
                    new CornerRadii(8), null))
                    : new Background(new BackgroundFill(
                    Color.web("#232323"), 
                    new CornerRadii(8), null))));

        killButton.backgroundProperty().bind(
            Bindings.createObjectBinding(
                () -> data.droneArmedProperty().get() == true 
                    ? new Background(new BackgroundFill(
                    Color.web("#d76057"), 
                    new CornerRadii(8), null))
                    : new Background(new BackgroundFill(
                    Color.web("#232323"), 
                    new CornerRadii(8), null))));

                
        // Style buttons
        armButton.getStyleClass().addAll("flight-option-button", "flight-option-button-unavailable");
        returnButton.getStyleClass().addAll("flight-option-button", "flight-option-button-unavailable");
        landButton.getStyleClass().addAll("flight-option-button", "flight-option-button-unavailable");
        killButton.getStyleClass().addAll("flight-option-button", "flight-option-button-unavailable");
        
        optionPane.getChildren().addAll(armButton, returnButton, landButton, killButton);

        return optionPane;
    }

    private GridPane statsPanel() {
        // Object creation

        GridPane statsPanel = new GridPane();
        statsPanel.setAlignment(Pos.CENTER);
        statsPanel.setPadding(new Insets(20));

        Label altitudeLabel = statText("");
        altitudeLabel.textProperty().bind(
            data.droneAltitudeProperty().asString("Altitude: %.1f m")
        );

        Label speedLabel = statText("");
        speedLabel.textProperty().bind(
            data.droneSpeedProperty().asString("Speed: %.1f m/s")
        );

        Label batteryLabel = statText("");
        batteryLabel.textProperty().bind(
            data.droneBatCapacityProperty().asString("Battery: %.1f V")
        );

        Label fullBatteryLabel = statText("");
        batteryLabel.textProperty().bind(
            data.droneBatMaxCapacityProperty().asString("Max: %.1f V")
        );

        Label statusLabel = statText("");
        statusLabel.textProperty().bind(
            data.flightStatusProperty()
        );

        Label payloadStatus = statText("");
        payloadStatus.textProperty().bind(
            Bindings.when(data.payloadStatusProperty())
            .then("Payload: Loaded")
            .otherwise("Payload: Unloaded")
        );

        Label speed = statText("");
        speed.textProperty().bind(
            data.droneSpeedProperty().asString("Speed: %.1f m/s")
        );

        Label distToTarget = statText("");
        distToTarget.textProperty().bind(
            data.flightNextTargetDistProperty().asString("Target Dist: %.1f m")
        );

        Label flightTime = statText("");
        flightTime.textProperty().bind(
            Bindings.concat("Flight Time: ", data.flightElapsedTimeStringRepProperty().get())
        );


        // Insert into grid

        statsPanel.add(statStack(altitudeLabel, 1, 1), 0, 0);
        statsPanel.add(statStack(speedLabel, 1, 1), 1, 0);
        statsPanel.add(statStack(batteryLabel, 1, 1), 0, 1);
        statsPanel.add(statStack(fullBatteryLabel, 1, 1), 1, 1);
        statsPanel.add(statStack(statusLabel, 2.1, 1), 0, 2, 2, 1);
        statsPanel.add(statStack(payloadStatus, 2.1, 1), 0, 3, 2, 1);
        statsPanel.add(statStack(flightTime, 2.1, 1), 0, 4, 2, 1);
        statsPanel.add(statStack(speed, 1, 1), 1, 5); //
        statsPanel.add(statStack(distToTarget, 1, 1), 0, 5); //

        statsPanel.setHgap(7.5);
        statsPanel.setVgap(7.5);

        return statsPanel;
    }

    // Generate the status panel - the bottom status panel
    private Pane statusPanel() {
        HBox status = new HBox();

        StackPane connectionPane = new StackPane();
        Rectangle connectionBg = new Rectangle(300, 50, Color.DARKKHAKI);
        connectionBg.setArcWidth(10);
        connectionBg.setArcHeight(10);

        Label connectionStatus = new Label("");
        connectionStatus.textProperty().bind(
            Bindings.when(data.connectedToDroneProperty())
            .then("Connection Status: Connected")
            .otherwise("Connection Status: Disconnected")
        );
        connectionStatus.setFont(new Font(20));
        connectionStatus.setTextFill(Color.WHITE);

        status.setAlignment(javafx.geometry.Pos.CENTER);
        connectionPane.getChildren().addAll(connectionBg, connectionStatus);
        status.getChildren().add(connectionPane);
        status.setPadding(new Insets(10));
        return status;
    }

    // --------------
    // STYLE
    // --------------

    // (Stats)

    // Returns a label formatted with a certain font and color. For the stats panel.
    private Label statText(String text) {
        Label statLabel = new Label(text);

        statLabel.setFont(new Font(15));
        statLabel.setTextFill(Color.WHITE);

        return statLabel;
    }

    // Returns a stack pane with the label and a rectangle.
    // The spanX and spanY determine how wide the stack should be, based on
    // an integer multiple of the default 1x1 button height and width.
    // For the stats panel.
    private StackPane statStack(Label text, double spanX, double spanY) {
        Rectangle statBackground = new Rectangle(spanX * 180, spanY * 60, Color.DIMGRAY);
        
        statBackground.setArcWidth(10);
        statBackground.setArcHeight(10);

        StackPane stack = new StackPane();
        stack.getChildren().addAll(statBackground, text);

        return stack;
    }

    // (Status)

}
