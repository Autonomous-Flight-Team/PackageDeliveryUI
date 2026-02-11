package com.ui.pages;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import com.ui.*;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.*;
import javafx.application.Platform;
import java.awt.image.BufferedImage;

public class CameraPage implements Page {
    // --------------
    // VARABLES
    // --------------
    private HBox main;

    private WritableImage bottomImage;
    private ImageView bottomImageView;
    
    private WritableImage frontImage;
    private ImageView frontImageView;

    private Data data;

    // --------------
    // GENERAL
    // --------------

    public CameraPage(Data data) {
        this.data = data;
        main = new HBox();
        main.setAlignment(Pos.CENTER);
        bottomImageView = new ImageView(new Image("Images/mapUnavailable.png"));
        frontImageView = new ImageView(new Image("Images/mapUnavailable.png"));
        main.getChildren().addAll(leftImagePanel(), rightImagePanel());
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

    VBox leftImagePanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setPadding(new Insets(15));

        Label title = titleText("Bottom View");

        bottomImageView.setFitWidth(480);
        bottomImageView.setFitHeight(400);

        leftPanel.getChildren().addAll(title, bottomImageView);
        
        return leftPanel;
    }

    VBox rightImagePanel() {
        VBox rightPanel = new VBox(10);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(15));

        Label title = titleText("Front View");

        frontImageView.setFitWidth(480);
        frontImageView.setFitHeight(400);

        rightPanel.getChildren().addAll(title, frontImageView);

        return rightPanel;
    }

    private Label titleText(String text) {
        Label titleLabel = new Label(text);

        titleLabel.setFont(new Font(24));
        titleLabel.setTextFill(Color.BLACK);

        return titleLabel;
    }
 }
