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
    private CommandService command;
    private MapService mapService;


    // --------------
    // GENERAL
    // --------------

    public FlightPage(Data data, CommandService command, MapService mapService) {
        this.data = data;
        this.command = command;
        this.mapService = mapService;
        
        main = new BorderPane();

        Pane stats = statsPanel();
        Pane options = optionPanel();
        Pane map = mapPanel();

        main.setRight(stats);
        main.setCenter(options);
        main.setLeft(map);
        main.setStyle("-fx-background-color: #d7d7d3;");
    }

    public Pane getPane() {
        return main;
    }

    // --------------
    // PANES
    // --------------

    private StackPane mapPanel() {
        StackPane mapPanel = new StackPane();
        Rectangle background = new Rectangle(395, 405, Color.web("#e7e7e7"));
        background.setArcHeight(30);
        background.setArcWidth(30);
        // TODO functionality: pull position data to get imagery from google maps, used center,
        // dir, xwidth and ywidth to place the drone icons. Placehold locations for now - build out 
        // placement functionality in modules
        Image mapImage = new Image("Images/mapUnavailable.png");
        ImageView mapImageView = new ImageView();
        mapImageView.setImage(mapImage);
        mapImageView.setFitWidth(360);
        mapImageView.setFitHeight(380);
        mapImageView.imageProperty().bind(mapService.getMapImageObjectProperty());

        //Image droneIcon = new Image("Images/droneIcon.png");
        //Image homeIcon = new Image("Images/droneIcon.png");
        //Image targetIcon = new Image("Images/droneIcon.png");

        StackPane mapStack = new StackPane();

        mapStack.getChildren().add(mapImageView);
        mapPanel.setPadding(new Insets(10, 20, 0, 25));
        mapPanel.getChildren().addAll(background, mapStack);
        return mapPanel;
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

    private Pane statsPanel() {
        StackPane statsPanel = new StackPane();
        Rectangle background = new Rectangle(345, 420, Color.web("#e7e7e7"));
        background.setArcHeight(30);
        background.setArcWidth(30);

        // Object creation

        GridPane statsGrid = new GridPane();
        statsGrid.setAlignment(Pos.CENTER);
        statsPanel.setPadding(new Insets(25));

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

        statsGrid.add(statStack(altitudeLabel, 1, 1), 0, 0);
        statsGrid.add(statStack(speedLabel, 1, 1), 1, 0);
        statsGrid.add(statStack(batteryLabel, 1, 1), 0, 1);
        statsGrid.add(statStack(fullBatteryLabel, 1, 1), 1, 1);
        statsGrid.add(statStack(statusLabel, 2.1, 1), 0, 2, 2, 1);
        statsGrid.add(statStack(payloadStatus, 2.1, 1), 0, 3, 2, 1);
        statsGrid.add(statStack(flightTime, 2.1, 1), 0, 4, 2, 1);
        statsGrid.add(statStack(speed, 1, 1), 1, 5); //
        statsGrid.add(statStack(distToTarget, 1, 1), 0, 5); //

        statsGrid.setHgap(7.5);
        statsGrid.setVgap(7.5);

        statsPanel.getChildren().addAll(background, statsGrid);

        return statsPanel;
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
        Rectangle statBackground = new Rectangle(spanX * 150, spanY * 60, Color.web("#373737"));
        
        statBackground.setArcWidth(10);
        statBackground.setArcHeight(10);

        StackPane stack = new StackPane();
        stack.getChildren().addAll(statBackground, text);

        return stack;
    }

    // (Status)

}
