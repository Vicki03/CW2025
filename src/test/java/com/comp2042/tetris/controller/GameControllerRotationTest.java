package com.comp2042.tetris.controller;

import com.comp2042.tetris.events.*;
import com.comp2042.tetris.model.*;
import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.Test;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerRotationTest {

    static class FakeBoardRotation implements Board {
        int rotateCalls = 0;

        @Override public boolean moveBrickDown() { return false; }
        @Override public boolean moveBrickLeft() { return false; }
        @Override public boolean moveBrickRight() { return false; }
        @Override public boolean rotateLeftBrick() { rotateCalls++; return true; }

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

    private GameController makeController(FakeBoardRotation fakeBoard) throws Exception {
        ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
        Constructor<Object> objCtor = Object.class.getDeclaredConstructor();

        @SuppressWarnings("unchecked")
        Constructor<GameController> gcCtor =
                (Constructor<GameController>) rf.newConstructorForSerialization(GameController.class, objCtor);
        GameController controller = gcCtor.newInstance();

        @SuppressWarnings("unchecked")
        Constructor<GuiController> guiCtor =
                (Constructor<GuiController>) rf.newConstructorForSerialization(GuiController.class, objCtor);
        GuiController realGui = guiCtor.newInstance();

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
                // field absent is fine for different GuiController versions
            }
        }

        Field viewField = GameController.class.getDeclaredField("viewGuiController");
        viewField.setAccessible(true);
        viewField.set(controller, realGui);

        Field boardField = GameController.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(controller, fakeBoard);

        return controller;
    }

    @Test
    void rotateEvent_calls_rotateLeftBrick() throws Exception {
        FakeBoardRotation fake = new FakeBoardRotation();
        GameController gc = makeController(fake);

        gc.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER));

        assertEquals(1, fake.rotateCalls, "rotateLeftBrick should be called once");
    }
}
