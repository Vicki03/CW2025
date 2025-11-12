package com.comp2042.tetris.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class GamePreviewPanelTest {

    private GameBoard newBoard() {
        GameBoard b = new GameBoard(25, 10);
        b.newGame();
        return b;
    }

    /* -------- helper to lock piece and spawn next -------- */
    private void invokeLockMethod(GameBoard board) throws Exception {
        String[] candidates = {
                "mergeBrick", "mergeCurrent", "mergeCurrentPiece",
                "lockPiece", "lockCurrentPiece", "placeCurrent", "lock"
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

        throw new IllegalStateException("No lock or spawn method found in GameBoard.");
    }

    /* -------- test cases -------- */

    @Test
    void preview_isNotNull_afterNewGame() {
        GameBoard board = newBoard();

        // many implementations use getNextBrickViewData()
        Object next = null;
        try {
            Method m = GameBoard.class.getMethod("getNextBrickViewData");
            next = m.invoke(board);
        } catch (Exception ignored) {}

        assertNotNull(next, "Preview panel data should be initialized after newGame()");
    }

    @Test
    void preview_updates_afterCurrentPieceLocks() throws Exception {
        GameBoard board = newBoard();

        // get current and next before
        Object currentBefore = board.getViewData();
        Object nextBefore = tryGetNext(board);

        // Simulate dropping and locking
        while (board.moveBrickDown()) {}
        invokeLockMethod(board);

        // After locking, new piece spawns; preview should shift
        Object currentAfter = board.getViewData();
        Object nextAfter = tryGetNext(board);

        assertNotEquals(currentBefore.toString(), currentAfter.toString(),
                "After lock, new current piece should differ from old one.");
        assertNotEquals(nextBefore.toString(), nextAfter.toString(),
                "Preview piece should update after current piece locks.");
        assertNotNull(nextAfter, "Preview panel should not become null after update.");
    }

    @Test
    void preview_neverNull_duringGameplay() throws Exception {
        GameBoard board = newBoard();

        for (int i = 0; i < 5; i++) {
            Object next = tryGetNext(board);
            assertNotNull(next, "Preview should never be null (iteration " + i + ")");
            // Drop and lock repeatedly to simulate multiple turns
            while (board.moveBrickDown()) {}
            invokeLockMethod(board);
        }
    }

    /* -------- reflection helper to get next preview piece -------- */
    private Object tryGetNext(GameBoard board) {
        String[] names = {
                "getNextBrickViewData", "getNextBrick", "getNextPiece", "nextBrick", "nextPiece"
        };
        for (String n : names) {
            try {
                Method m = GameBoard.class.getMethod(n);
                m.setAccessible(true);
                return m.invoke(board);
            } catch (Exception ignored) { }
        }
        return null;
    }
}
