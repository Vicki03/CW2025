package com.comp2042.tetris.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class GameOverTest {

    @Test
    void gameOver_whenTopRowBlocked_andNewPieceSpawned() throws Exception {
        GameBoard board = new GameBoard(2, 10); // small height to make spawn collision likely
        board.newGame();

        Object matrix = getBoardMatrix(board);
        assertNotNull(matrix, "Could not access board matrix via reflection; test requires a readable matrix.");

        fillTopRow(matrix);

        // try to force a spawn / create new piece which should detect game over
        invokeSpawn(board);

        Boolean over = readGameOverFlag(board);
        assertNotNull(over, "Could not find a game-over accessor (method or field) on GameBoard.");
        assertTrue(over, "Board should be game over when top row is fully occupied and a new piece is spawned");
    }

    /* ---------- helpers ---------- */

    private Object getBoardMatrix(GameBoard board) {
        String[] getters = { "getBoardMatrix", "getBackgroundMatrix", "getBackGround", "getGameMatrix", "getBoard" };
        for (String g : getters) {
            try {
                Method m = GameBoard.class.getDeclaredMethod(g);
                m.setAccessible(true);
                return m.invoke(board);
            } catch (Throwable ignored) {}
        }
        String[] fields = { "boardMatrix", "backgroundMatrix", "backGround", "gameMatrix", "board" };
        for (String f : fields) {
            try {
                Field fld = GameBoard.class.getDeclaredField(f);
                fld.setAccessible(true);
                return fld.get(board);
            } catch (Throwable ignored) {}
        }
        return null;
    }

    private void fillTopRow(Object matrix2D) {
        int rows = Array.getLength(matrix2D);
        if (rows == 0) return;
        Object top = Array.get(matrix2D, 0);
        int cols = Array.getLength(top);
        for (int c = 0; c < cols; c++) {
            Object cell = Array.get(top, c);
            if (cell instanceof Boolean) {
                Array.set(top, c, Boolean.TRUE);
            } else if (cell instanceof Number) {
                // handle int/Integer/short/etc.
                Class<?> comp = top.getClass().getComponentType();
                if (comp == int.class || comp == Integer.class) Array.setInt(top, c, 1);
                else if (comp == short.class || comp == Short.class) Array.setShort(top, c, (short)1);
                else if (comp == long.class || comp == Long.class) Array.set(top, c, 1L);
                else Array.set(top, c, 1);
            } else {
                // fallback: try to set a truthy value
                Array.set(top, c, Boolean.TRUE);
            }
        }
    }

    private void invokeSpawn(GameBoard board) {
        String[] spawnNames = { "createNewBrick", "spawnNewPiece", "spawn", "newPiece", "nextPiece", "createPiece" };
        for (String n : spawnNames) {
            try {
                Method m = GameBoard.class.getDeclaredMethod(n);
                m.setAccessible(true);
                m.invoke(board);
                return;
            } catch (Throwable ignored) {}
        }
        // fallback: call move until a spawn is attempted or call newGame() is not acceptable
        // try calling any public method named similarly
        for (String n : spawnNames) {
            try {
                Method m = GameBoard.class.getMethod(n);
                m.setAccessible(true);
                m.invoke(board);
                return;
            } catch (Throwable ignored) {}
        }
    }

    private Boolean readGameOverFlag(GameBoard board) {
        String[] getters = { "isGameOver", "gameOver", "isOver", "isEnded", "isFinished", "isGameFinished", "isGameEnded" };
        for (String g : getters) {
            try {
                Method m = GameBoard.class.getDeclaredMethod(g);
                m.setAccessible(true);
                Object v = m.invoke(board);
                if (v instanceof Boolean) return (Boolean) v;
            } catch (Throwable ignored) {}
            try {
                Method m = GameBoard.class.getMethod(g);
                m.setAccessible(true);
                Object v = m.invoke(board);
                if (v instanceof Boolean) return (Boolean) v;
            } catch (Throwable ignored) {}
        }
        String[] fields = { "gameOver", "over", "isGameOver", "gameOverFlag", "ended" };
        for (String f : fields) {
            try {
                Field fld = GameBoard.class.getDeclaredField(f);
                fld.setAccessible(true);
                Object v = fld.get(board);
                if (v instanceof Boolean) return (Boolean) v;
            } catch (Throwable ignored) {}
        }
        return null;
    }
}
