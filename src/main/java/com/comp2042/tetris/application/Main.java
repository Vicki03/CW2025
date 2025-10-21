package com.comp2042.tetris.application;

import com.comp2042.tetris.controller.GameController;
import com.comp2042.tetris.controller.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameLayout.fxml"));
        Parent root = loader.load();

        GuiController guiController = loader.getController();
        new GameController(guiController);

        primaryStage.setTitle("TetrisJFX");
        primaryStage.setScene(new Scene(root, 300, 510));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
