package com.comp2042.tetris.view;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

//this is an overlay, not a separate screen
public class GameOverPanel extends BorderPane {


    public GameOverPanel() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: darkred;");
        setCenter(gameOverLabel);

        setPrefSize(260, 80);
        setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-effect: dropshadow(gaussian, #444, 18, 0.5, 0, 2);");
    }

}
