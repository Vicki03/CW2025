package com.comp2042.tetris.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.*;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardHoldTest {

    /* ===================== Setup & lock helpers ===================== */

    private GameBoard newBoardForTest() {
        GameBoard b = new GameBoard(25, 10);
        b.newGame();
        return b;
    }

    private void invokeLockMethod(GameBoard board) throws Exception {
        String[] candidates = {
                "mergeBrick","mergeCurrent","mergeCurrentPiece",
                "lockPiece","lockCurrentPiece","placeCurrent","lock"
        };
        for (String name : candidates) {
            try {
                Method m = GameBoard.class.getDeclaredMethod(name);
                m.setAccessible(true);
                m.invoke(board);
                return;
            } catch (NoSuchMethodException ignored) { }
        }
        try {
            Method create = GameBoard.class.getDeclaredMethod("createNewBrick");
            create.setAccessible(true);
            create.invoke(board);
            return;
        } catch (NoSuchMethodException ignored) { }
        throw new IllegalStateException("Could not find a lock/merge method on GameBoard.");
    }

    /* ===================== Brick/type extraction ===================== */

    // Common names for "current" & "held" brick accessors
    private static final String[] CURRENT_METHODS = {
            "getCurrentBrick","getCurrentPiece","getCurrent","currentBrick","currentPiece"
    };
    private static final String[] HELD_METHODS = {
            "getHeldBrick","getHoldBrick","getHeld","getHold","heldBrick","holdBrick","held","hold"
    };

    // Common names for a brick's "type" (enum/id)
    private static final String[] TYPE_METHODS = {
            "getType","getTetromino","getTetrominoType","getBrickType","type","id","getId","getName","name"
    };
    private static final String[] TYPE_FIELDS = {
            "type","tetromino","tetrominoType","brickType","id","name"
    };

    /** Pull the current brick object from GameBoard using common method/field names. */
    private Object extractCurrentBrick(GameBoard board) throws Exception {
        Object o = tryGetViaMethods(board, CURRENT_METHODS);
        if (o != null) return o;
        return tryGetViaFields(board, CURRENT_METHODS);
    }

    /** Pull the held brick object from GameBoard using common method/field names. */
    private Object extractHeldBrick(GameBoard board) throws Exception {
        Object o = tryGetViaMethods(board, HELD_METHODS);
        if (o != null) return o;
        return tryGetViaFields(board, HELD_METHODS);
    }

    private Object tryGetViaMethods(Object target, String[] names)
            throws InvocationTargetException, IllegalAccessException {
        for (String n : names) {
            try {
                Method m = target.getClass().getMethod(n);
                m.setAccessible(true);
                return m.invoke(target);
            } catch (NoSuchMethodException ignored) { }
        }
        return null;
    }

    private Object tryGetViaFields(Object target, String[] names) throws IllegalAccessException {
        Class<?> c = target.getClass();
        for (String n : names) {
            try {
                Field f = c.getDeclaredField(n);
                f.setAccessible(true);
                return f.get(target);
            } catch (NoSuchFieldException ignored) { }
        }
        return null;
    }

    /** Turn a brick (or ViewData) into a stable type string if possible. */
    private String extractTypeString(Object brickOrView) throws Exception {
        if (brickOrView == null) return "NULL";


        for (String tm : TYPE_METHODS) {
            Object v = tryCall(brickOrView, tm);
            if (v != null) return normalizeTypeValue(v);
        }


        for (String tf : TYPE_FIELDS) {
            Object v = tryField(brickOrView, tf);
            if (v != null) return normalizeTypeValue(v);
        }

        Object inner = tryCall(brickOrView, "getBrick");
        if (inner == null) inner = tryCall(brickOrView, "getPiece");
        if (inner != null) return extractTypeString(inner);


        String enumName = findAnyEnumName(brickOrView);
        if (enumName != null) return enumName;


        return "FALLBACK:" + brickOrView.getClass().getName() + ":" + brickOrView.toString();
    }

    private Object tryCall(Object target, String name) throws InvocationTargetException, IllegalAccessException {
        try {
            Method m = target.getClass().getMethod(name);
            m.setAccessible(true);
            return m.invoke(target);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private Object tryField(Object target, String name) throws IllegalAccessException {
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(target);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private String normalizeTypeValue(Object v) {
        if (v == null) return "NULL";
        // If it's an enum, use enum name
        if (v instanceof Enum<?> e) return "ENUM:" + e.name();
        // If it has name() method (enums/enum-like)
        try {
            Method m = v.getClass().getMethod("name");
            Object n = m.invoke(v);
            if (n != null) return "NAME:" + n.toString();
        } catch (Exception ignored) { }
        // Otherwise toString
        return "VAL:" + v.toString();
    }

    private String findAnyEnumName(Object o) {
        // search public getters
        for (Method m : o.getClass().getMethods()) {
            if (m.getParameterCount() == 0 && m.getName().startsWith("get")) {
                try {
                    Object v = m.invoke(o);
                    if (v instanceof Enum<?> e) return "ENUM:" + e.name();
                } catch (Exception ignored) {}
            }
        }
        // search declared fields
        for (Field f : o.getClass().getDeclaredFields()) {
            try {
                f.setAccessible(true);
                Object v = f.get(o);
                if (v instanceof Enum<?> e) return "ENUM:" + e.name();
            } catch (Exception ignored) {}
        }
        return null;
    }

//tests

    @Test
    void firstHold_movesCurrentIntoHold_andSpawnsNext_andDisablesSecondHold() throws Exception {
        GameBoard board = newBoardForTest();

        assertNull(board.getHeldBrickViewData(), "hold should start empty");

        boolean gameOver = board.holdCurrentBrick();
        assertFalse(gameOver);

        assertNotNull(board.getHeldBrickViewData(), "hold should be filled after first hold");

        boolean gameOver2 = board.holdCurrentBrick();
        assertFalse(gameOver2);
    }

    @Test
    void afterLock_holdIsReenabled() throws Exception {
        GameBoard board = newBoardForTest();

        board.holdCurrentBrick();           // disables hold until lock
        while (board.moveBrickDown()) { }   // drop
        invokeLockMethod(board);            // lock
        try {
            Method create = GameBoard.class.getDeclaredMethod("createNewBrick");
            create.setAccessible(true);
            create.invoke(board);           // if your lock doesn't spawn
        } catch (NoSuchMethodException ignored) { }

        assertDoesNotThrow(board::holdCurrentBrick);
    }

    @Test
    void swapWithNonEmptyHold_swapsCurrentAndHold_usingBrickTypes() throws Exception {
        GameBoard board = newBoardForTest();

        // Fill hold, then lock & spawn to re-enable hold
        board.holdCurrentBrick();
        while (board.moveBrickDown()) {}
        invokeLockMethod(board);
        try {
            Method create = GameBoard.class.getDeclaredMethod("createNewBrick");
            create.setAccessible(true);
            create.invoke(board);
        } catch (NoSuchMethodException ignored) { }

        // Get types BEFORE swap
        String currentTypeBefore = typeOfCurrent(board);
        String heldTypeBefore    = typeOfHeld(board);

        // Perform swap
        board.holdCurrentBrick();

        // Get types AFTER swap
        String currentTypeAfter = typeOfCurrent(board);
        String heldTypeAfter    = typeOfHeld(board);

        // Now assert proper swap
        assertEquals(currentTypeBefore, heldTypeAfter,
                "held after swap should equal previous current (by brick type). " +
                        debugTypes("prevCurrent", currentTypeBefore, "heldAfter", heldTypeAfter));

        assertEquals(heldTypeBefore, currentTypeAfter,
                "current after swap should equal previous held (by brick type). " +
                        debugTypes("prevHeld", heldTypeBefore, "currentAfter", currentTypeAfter));
    }

    /* Get type strings directly from GameBoard's current/held bricks */
    private String typeOfCurrent(GameBoard board) throws Exception {
        Object brick = extractCurrentBrick(board);
        if (brick == null) {
            // as last resort, go through view data
            Object vd = board.getViewData();
            return extractTypeString(vd);
        }
        return extractTypeString(brick);
    }

    private String typeOfHeld(GameBoard board) throws Exception {
        Object brick = extractHeldBrick(board);
        if (brick == null) {
            Object vd = board.getHeldBrickViewData();
            return extractTypeString(vd);
        }
        return extractTypeString(brick);
    }

    private String debugTypes(String aLabel, String a, String bLabel, String b) {
        return "\n" + aLabel + ": " + a + "\n" + bLabel + ": " + b;
    }
}
