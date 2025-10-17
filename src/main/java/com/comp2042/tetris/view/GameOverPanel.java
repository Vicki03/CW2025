package com.comp2042.tetris.view;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

//this is an overlay, not a separate screen
public class GameOverPanel extends BorderPane {

    public GameOverPanel() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        setCenter(gameOverLabel);
    }

}
