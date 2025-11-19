package com.comp2042.tetris.controller;

import com.comp2042.tetris.events.*;
import com.comp2042.tetris.model.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.Test;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerDownTest {

    // Helper: create a real Score instance. Try public/private ctor first; fallback to ReflectionFactory
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

            // try to set any IntegerProperty field to a working SimpleIntegerProperty
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

    // Helper: create a ClearRow instance with given values (tries ctor, then reflection fallback)
    private static ClearRow makeClearRow(int linesRemoved, int scoreBonus) throws Exception {
        // try common constructor signatures first
        try {
            Constructor<ClearRow> ctor = ClearRow.class.getDeclaredConstructor(int.class, int.class);
            ctor.setAccessible(true);
            return ctor.newInstance(linesRemoved, scoreBonus);
        } catch (NoSuchMethodException ignored) {
            // try any ctor with two params
            for (Constructor<?> c : ClearRow.class.getDeclaredConstructors()) {
                Class<?>[] pts = c.getParameterTypes();
                if (pts.length == 2 &&
                        (Number.class.isAssignableFrom(pts[0]) || pts[0] == int.class) &&
                        (Number.class.isAssignableFrom(pts[1]) || pts[1] == int.class)) {
                    c.setAccessible(true);
                    return (ClearRow) c.newInstance(linesRemoved, scoreBonus);
                }
            }
            // fallback: instantiate without constructor and set likely fields
            ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
            Constructor<Object> objCtor = Object.class.getDeclaredConstructor();
            @SuppressWarnings("unchecked")
            Constructor<ClearRow> crCtor = (Constructor<ClearRow>) rf.newConstructorForSerialization(ClearRow.class, objCtor);
            ClearRow cr = crCtor.newInstance();

            for (Field f : ClearRow.class.getDeclaredFields()) {
                f.setAccessible(true);
                String name = f.getName().toLowerCase();
                if (f.getType() == int.class || f.getType() == Integer.class) {
                    if (name.contains("line")) {
                        f.setInt(cr, linesRemoved);
                    } else if (name.contains("score") || name.contains("bonus")) {
                        f.setInt(cr, scoreBonus);
                    } else {
                        // if ambiguous and not set yet, set to linesRemoved by default
                        f.setInt(cr, linesRemoved);
                    }
                }
            }
            return cr;
        }
    }

    static class FakeBoardCanMove implements Board {
        boolean mergeCalled = false;
        final Score score;

        FakeBoardCanMove() throws Exception {
            this.score = makeScore();
        }

        @Override public boolean moveBrickDown() { return true; } // can move
        @Override public boolean moveBrickLeft() { return false; }
        @Override public boolean moveBrickRight() { return false; }
        @Override public boolean rotateLeftBrick() { return false; }

        @Override public boolean createNewBrick() { return false; }
        @Override public int[][] getBoardMatrix() { return new int[0][0]; }
        @Override public ViewData getViewData() { return null; }
        @Override public void mergeBrickToBackground() { mergeCalled = true; }
        @Override public ClearRow clearRows() { return null; }
        @Override public Score getScore() { return score; }
        @Override public void newGame() {}
        @Override public ViewData getNextBrickViewData() { return null; }
        @Override public ViewData getHeldBrickViewData() { return null; }
        @Override public boolean holdCurrentBrick() { return false; }
    }

    static class FakeBoardBlocked implements Board {
        boolean mergeCalled = false;
        boolean clearedCalled = false;
        boolean createdNewBrick = false;
        final Score score;

        FakeBoardBlocked() throws Exception {
            this.score = makeScore();
        }

        @Override public boolean moveBrickDown() { return false; } // blocked
        @Override public boolean moveBrickLeft() { return false; }
        @Override public boolean moveBrickRight() { return false; }
        @Override public boolean rotateLeftBrick() { return false; }

        @Override public boolean createNewBrick() { createdNewBrick = true; return false; }
        @Override public int[][] getBoardMatrix() { return new int[0][0]; }
        @Override public ViewData getViewData() { return null; }
        @Override public void mergeBrickToBackground() { mergeCalled = true; }
        @Override public ClearRow clearRows() {
            clearedCalled = true;
            try {
                return makeClearRow(1, 100);
            } catch (Exception e) {
                // last-resort: return null (test will fail meaningfully)
                return null;
            }
        }
        @Override public Score getScore() { return score; }
        @Override public void newGame() {}
        @Override public ViewData getNextBrickViewData() { return null; }
        @Override public ViewData getHeldBrickViewData() { return null; }
        @Override public boolean holdCurrentBrick() { return false; }
    }

    private GameController makeController(Board fakeBoard) throws Exception {
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

        return controller;
    }

    @Test
    void onDown_userMove_incrementsScore() throws Exception {
        FakeBoardCanMove fake = new FakeBoardCanMove();
        GameController gc = makeController(fake);

        gc.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));

        // read the Score via its public API
        IntegerProperty p = fake.getScore().scoreProperty();
        assertNotNull(p, "scoreProperty must not be null");
        assertEquals(1, p.get(), "user down should add 1 point");
        assertFalse(fake.mergeCalled, "merge should not be called when move succeeds");
    }

    @Test
    void onDown_blocked_mergesClearsAndCreatesNew() throws Exception {
        FakeBoardBlocked fake = new FakeBoardBlocked();
        GameController gc = makeController(fake);

        // use USER to avoid missing TIMER symbol; controller should still merge when moveBrickDown() returns false
        gc.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));

        assertTrue(fake.mergeCalled, "merge should be called when blocked");
        assertTrue(fake.clearedCalled, "clearRows should be called when blocked");
        assertTrue(fake.createdNewBrick, "createNewBrick should be attempted when blocked");
    }
}
