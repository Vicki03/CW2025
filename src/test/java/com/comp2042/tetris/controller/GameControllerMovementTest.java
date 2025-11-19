// java
package com.comp2042.tetris.controller;

import com.comp2042.tetris.events.*;
import com.comp2042.tetris.model.*;
import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.Test;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerMovementTest {

    static class FakeBoardMovement implements Board {
        int leftCalls = 0;
        int rightCalls = 0;

        @Override public boolean moveBrickDown() { return false; }
        @Override public boolean moveBrickLeft() { leftCalls++; return true; }
        @Override public boolean moveBrickRight() { rightCalls++; return true; }
        @Override public boolean rotateLeftBrick() { return false; }

        @Override public boolean createNewBrick() { return false; }
        @Override public int[][] getBoardMatrix() { return new int[0][0]; }
        @Override public ViewData getViewData() { return null; }
        @Override public void mergeBrickToBackground() {}
        @Override public ClearRow clearRows() { return null; }
        @Override public Score getScore() { return null; }
        @Override public void newGame() {}
        @Override public ViewData getNextBrickViewData() { return null; }
        @Override public ViewData getHeldBrickViewData() { return null; }
        @Override public boolean holdCurrentBrick() { return false; }
    }

    /**
     * Create GameController and GuiController instances without invoking their constructors,
     * initialize GuiController UI fields to safe dummy GridPane objects, then inject the
     * GuiController and fake Board into the GameController instance.
     */
    private GameController makeController(FakeBoardMovement fakeBoard) throws Exception {
        ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
        Constructor<Object> objCtor = Object.class.getDeclaredConstructor();

        // create GameController instance without running its constructor
        @SuppressWarnings("unchecked")
        Constructor<GameController> gcCtor =
                (Constructor<GameController>) rf.newConstructorForSerialization(GameController.class, objCtor);
        GameController controller = gcCtor.newInstance();

        // create a real GuiController instance without running its constructor
        @SuppressWarnings("unchecked")
        Constructor<GuiController> guiCtor =
                (Constructor<GuiController>) rf.newConstructorForSerialization(GuiController.class, objCtor);
        GuiController realGui = guiCtor.newInstance();

        // Initialize common GridPane fields on the GuiController to avoid NPEs when its methods run
        String[] gridFieldNames = {
                "gamePanel", "brickLayer", "backgroundGrid", "gameGrid", "boardPane",
                "nextGrid", "heldGrid", "nextPanel", "holdPanel"
        };
        for (String fname : gridFieldNames) {
            try {
                Field f = GuiController.class.getDeclaredField(fname);
                f.setAccessible(true);
                f.set(realGui, new GridPane());
            } catch (NoSuchFieldException ignored) {
                // OK if a field doesn't exist in this version of GuiController
            }
        }

        // Inject the real GuiController into the (final) viewGuiController field of GameController
        Field viewField = GameController.class.getDeclaredField("viewGuiController");
        viewField.setAccessible(true);
        viewField.set(controller, realGui);

        // Inject the fake board into the private board field
        Field boardField = GameController.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(controller, fakeBoard);

        return controller;
    }

    @Test
    void leftEvent_calls_moveBrickLeft() throws Exception {
        FakeBoardMovement fake = new FakeBoardMovement();
        GameController gc = makeController(fake);

        gc.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));

        assertEquals(1, fake.leftCalls, "left should be called once");
        assertEquals(0, fake.rightCalls, "right should not be called");
    }

    @Test
    void rightEvent_calls_moveBrickRight() throws Exception {
        FakeBoardMovement fake = new FakeBoardMovement();
        GameController gc = makeController(fake);

        gc.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));

        assertEquals(1, fake.rightCalls, "right should be called once");
        assertEquals(0, fake.leftCalls, "left should not be called");
    }
}
