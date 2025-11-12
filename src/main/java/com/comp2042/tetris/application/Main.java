package com.comp2042.tetris.application;

import com.comp2042.tetris.controller.GameController;
import com.comp2042.tetris.controller.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the tetris application
 * this class initializes the JavaFX environment, loads hte FXML layout,
 * connects the GUI controller to the game controller, and launches the
 * game window.
 *
 */

public class Main extends Application {

    /**
     * Starts the TetrisFJX application.
     *
     * @throws Exception if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/comp2042/tetris/view/gameLayout.fxml"));
        Parent root = loader.load();

        GuiController guiController = loader.getController();
        new GameController(guiController);

        primaryStage.setTitle("TetrisJFX");
        primaryStage.setScene(new Scene(root, 350, 510)); //size of window

        //lock window size
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    /**
     * Launches the JavaFX application
     * @param args command line arguments passed at runtime
     */
    public static void main(String[] args) {
        launch(args);
    }
}
