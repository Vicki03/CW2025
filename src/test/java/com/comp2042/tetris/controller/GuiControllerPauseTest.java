package com.comp2042.tetris.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class GuiControllerPauseTest {

    private GuiController controller;

    @org.testfx.framework.junit5.Start
    private void start(Stage stage) throws Exception {
        var url = java.util.Objects.requireNonNull(
                getClass().getResource("/com/comp2042/tetris/view/gameLayout.fxml"),
                "FXML not found at /com/comp2042/tetris/view/gameLayout.fxml"
        );
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();
        controller = loader.getController();

        // Don't let the timer fire during the test
        controller.setGravityMs(1_000_000); // 1,000,000 ms (~16 min)

        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }


    @Test
    void pauseButtonTogglesStateAndText(FxRobot robot) {
        Button pauseBtn = robot.lookup("#pauseButton").queryAs(Button.class);
        assertNotNull(pauseBtn, "fx:id=\"pauseButton\" not found in FXML");

        assertFalse(controller.isPaused(), "Should start unpaused");
        assertEquals("Pause", pauseBtn.getText(), "Initial text should be 'Pause'");

        robot.clickOn(pauseBtn);
        assertTrue(controller.isPaused(), "Should be paused after first click");
        assertEquals("Resume", pauseBtn.getText(), "Text should switch to 'Resume'");

        robot.clickOn(pauseBtn);
        assertFalse(controller.isPaused(), "Should be running after second click");
        assertEquals("Pause", pauseBtn.getText(), "Text should switch back to 'Pause'");
    }

}
