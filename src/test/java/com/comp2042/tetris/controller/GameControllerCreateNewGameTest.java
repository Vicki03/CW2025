package com.comp2042.tetris.controller;

import com.comp2042.tetris.model.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.Test;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerCreateNewGameTest {

    // ensure JavaFX toolkit is initialized once for tests that touch GuiController
    private static volatile boolean fxInitialized = false;
    private static synchronized void ensureFxInitialized() {
        if (!fxInitialized) {
            try {
                final CountDownLatch latch = new CountDownLatch(1);
                Platform.startup(latch::countDown);
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            } catch (IllegalStateException ignored) {
                // already started
            }
            fxInitialized = true;
        }
    }

    // Helper: create a real Score instance (public ctor or reflection fallback)
    private static Score makeScore() throws Exception {
        try {
            Constructor<Score> ctor = Score.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (NoSuchMethodException ignored) {
            ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
            Constructor<Object> objCtor = Object.class.getDeclaredConstructor();
            @SuppressWarnings("unchecked")
            Constructor<Score> scCtor = (Constructor<Score>) rf.newConstructorForSerialization(Score.class, objCtor);
            Score s = scCtor.newInstance();
            for (Field f : Score.class.getDeclaredFields()) {
                if (IntegerProperty.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    f.set(s, new SimpleIntegerProperty(0));
                    break;
                }
            }
            return s;
        }
    }

    // Helper: create a LevelService instance (try ctor, fallback to ReflectionFactory)
    private static LevelService makeLevelService() throws Exception {
        try {
            Constructor<LevelService> ctor = LevelService.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (NoSuchMethodException ignored) {
            ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
            Constructor<Object> objCtor = Object.class.getDeclaredConstructor();
            @SuppressWarnings("unchecked")
            Constructor<LevelService> lsCtor = (Constructor<LevelService>) rf.newConstructorForSerialization(LevelService.class, objCtor);
            return lsCtor.newInstance();
        }
    }

    static class FakeBoardNewGame implements Board {
        boolean newGameCalled = false;
        final Score score;

        FakeBoardNewGame() throws Exception {
            this.score = makeScore();
        }

        @Override public boolean moveBrickDown() { return false; }
        @Override public boolean moveBrickLeft() { return false; }
        @Override public boolean moveBrickRight() { return false; }
        @Override public boolean rotateLeftBrick() { return false; }

        @Override public boolean createNewBrick() { return false; }
        @Override public int[][] getBoardMatrix() { return new int[][]{{0}}; }
        @Override public ViewData getViewData() { return null; }
        @Override public void mergeBrickToBackground() {}
        @Override public ClearRow clearRows() { return null; }
        @Override public Score getScore() { return score; }
        @Override public void newGame() { newGameCalled = true; }
        @Override public ViewData getNextBrickViewData() { return null; }
        @Override public ViewData getHeldBrickViewData() { return null; }
        @Override public boolean holdCurrentBrick() { return false; }
    }

    // Create GameController without running ctor and inject fake board & a safe GuiController instance
    private GameController makeController(Board fakeBoard) throws Exception {
        // initialize JavaFX toolkit to avoid Toolkit not initialized errors
        ensureFxInitialized();

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

        String[] gridFieldNames = {"gamePanel","brickLayer","backgroundGrid","gameGrid","boardPane","nextGrid","heldGrid","nextPanel","holdPanel"};
        for (String fname : gridFieldNames) {
            try {
                Field f = GuiController.class.getDeclaredField(fname);
                f.setAccessible(true);
                f.set(realGui, new GridPane());
            } catch (NoSuchFieldException ignored) {}
        }

        Field viewField = GameController.class.getDeclaredField("viewGuiController");
        viewField.setAccessible(true);
        viewField.set(controller, realGui);

        Field boardField = GameController.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(controller, fakeBoard);

        // inject a LevelService so createNewGame() won't NPE
        Field levelServiceField = GameController.class.getDeclaredField("levelService");
        levelServiceField.setAccessible(true);
        levelServiceField.set(controller, makeLevelService());

        return controller;
    }

    @Test
    void createNewGame_calls_board_newGame_and_resets_currentLevel() throws Exception {
        FakeBoardNewGame fake = new FakeBoardNewGame();
        GameController gc = makeController(fake);

        // set currentLevel to a non-1 value so we can verify it gets reset
        Field levelField = GameController.class.getDeclaredField("currentLevel");
        levelField.setAccessible(true);
        levelField.setInt(gc, 5);

        gc.createNewGame();

        assertTrue(fake.newGameCalled, "board.newGame() should be called");
        int currentLevel = levelField.getInt(gc);
        assertEquals(1, currentLevel, "currentLevel should be reset to 1 after createNewGame()");
        // also ensure score property exists and is readable
        IntegerProperty p = fake.getScore().scoreProperty();
        assertNotNull(p, "scoreProperty must not be null");
    }
}
