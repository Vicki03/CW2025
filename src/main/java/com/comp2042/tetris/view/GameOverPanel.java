package com.comp2042.tetris.view;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * A simple overlay panel displayed when the game ends.
 * <p>
 * This component is not a separate scene — it is a styled overlay
 * layered on top of the main game view to display the "Game Over" message.
 * </p>
 *
 * <p>
 * The panel contains a centered {@link Label} with large, bold text
 * and a white rounded background with a drop shadow effect for visibility.
 * </p>
 *
 * <p>
 * Used by {@link com.comp2042.tetris.controller.GuiController} when the
 * game state transitions to "game over."
 * </p>
 */
//this is an overlay, not a separate screen
public class GameOverPanel extends BorderPane {


    /**
     * Constructs a {@code GameOverPanel} with default styling and layout.
     * <p>
     * The label is centered, styled with bold red text, and the panel is
     * given a white rounded background with a shadow for emphasis.
     * </p>
     */
    public GameOverPanel() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: darkred;");
        setCenter(gameOverLabel);

        setPrefSize(260, 80);
        setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-effect: dropshadow(gaussian, #444, 18, 0.5, 0, 2);");
    }

}
