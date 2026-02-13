package com.ui.pages;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.control.Button;
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
    private Data data;
    private Pane[] menuPages;
    IntegerProperty selectedMenuPage = new SimpleIntegerProperty(0);
    
    // --------------
    // GENERAL
    // --------------

    public ConfigurationPage(Data data, CommandService command) {
        this.command = command;
        this.data = data;
        menuPages = new Pane[1]; // Replace with number of pages
        menuPages[0] = telemetryPanel();
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
        menuPanel.setPadding(new Insets(30));
    
        return menuPanel;
    }

    // --------------
    // TELEMETRY OPTIONS
    // --------------

    private Pane telemetryPanel() {
        StackPane telemetryStack = new StackPane();
        Rectangle background = new Rectangle(740, 420, Color.web("#e7e7e7"));
        background.setArcWidth(30);
        background.setArcHeight(30);

        telemetryStack.setPadding(new Insets(30));
        telemetryStack.getChildren().addAll(background);
        return telemetryStack;
    }

    // --------------
    // FUNCTION
    // --------------

    private void selectPage(int pageIndex) {
        selectedMenuPage.set(pageIndex);
        main.setRight(menuPages[selectedMenuPage.get()]);
    }
}
