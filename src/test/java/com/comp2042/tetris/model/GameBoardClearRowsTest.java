package com.comp2042.tetris.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardClearRowsTest {

    private GameBoard newSmallBoard() {
        // small board for easy assertions: 4 rows x 4 cols
        return new GameBoard(4, 4);
    }

    private void setRow(GameBoard board, int rowIndex, int fillValue) {
        int[][] m = board.getBoardMatrix();
        for (int c = 0; c < m[rowIndex].length; c++) {
            m[rowIndex][c] = fillValue;
        }
    }

    private int[] rowSnapshot(GameBoard board, int rowIndex) {
        int[][] m = board.getBoardMatrix();
        int[] copy = new int[m[rowIndex].length];
        System.arraycopy(m[rowIndex], 0, copy, 0, copy.length);
        return copy;
    }

    private int getIntFromClearRow(Object clearRow, String[] candidateMethodNames, String[] candidateFieldNames) throws Exception {
        if (clearRow == null) return 0;

        Class<?> c = clearRow.getClass();

        // try explicit method names
        for (String name : candidateMethodNames) {
            try {
                Method m = c.getMethod(name);
                m.setAccessible(true);
                Object v = m.invoke(clearRow);
                if (v instanceof Number) return ((Number) v).intValue();
            } catch (NoSuchMethodException ignored) { }
        }

        // try heuristics: any public method returning int whose name suggests lines/bonus
        for (Method m : c.getMethods()) {
            String nm = m.getName().toLowerCase();
            if (m.getReturnType() == int.class && m.getParameterCount() == 0 &&
                    (nm.contains("line") || nm.contains("removed") || nm.contains("count") || nm.contains("bonus") || nm.contains("score") || nm.contains("point"))) {
                m.setAccessible(true);
                Object v = m.invoke(clearRow);
                if (v instanceof Number) return ((Number) v).intValue();
            }
        }

        // try declared fields
        for (String fname : candidateFieldNames) {
            try {
                Field f = c.getDeclaredField(fname);
                f.setAccessible(true);
                Object v = f.get(clearRow);
                if (v instanceof Number) return ((Number) v).intValue();
            } catch (NoSuchFieldException ignored) { }
        }

        // fallback: any declared int field that looks relevant
        for (Field f : c.getDeclaredFields()) {
            String nm = f.getName().toLowerCase();
            if (f.getType() == int.class && (nm.contains("line") || nm.contains("removed") || nm.contains("count") || nm.contains("bonus") || nm.contains("score") || nm.contains("point"))) {
                f.setAccessible(true);
                Object v = f.get(clearRow);
                if (v instanceof Number) return ((Number) v).intValue();
            }
        }

        throw new AssertionError("Could not find appropriate int accessor on ClearRow: " + c.getName());
    }

    @Test
    void clearOneRow_updatesClearRowAndShiftsBoard() throws Exception {
        GameBoard b = newSmallBoard();

        // set up distinct values per row so we can detect shifting
        setRow(b, 0, 9);
        setRow(b, 1, 8);
        setRow(b, 2, 7);
        setRow(b, 3, 1); // full line to be cleared

        int[] beforeRow2 = rowSnapshot(b, 2); // [7,7,7,7]

        Object cr = TestUtils.clearRows(b);

        int linesRemoved = getIntFromClearRow(cr,
                new String[] {"getLines","getRemovedLines","getLinesRemoved","getNumberOfLines","getCount","getRemoved"},
                new String[] {"lines","removed","count"}
        );
        int bonus = getIntFromClearRow(cr,
                new String[] {"getBonus","getScore","getScoreBonus","getPoints","getAddedScore"},
                new String[] {"bonus","score","points"}
        );

        assertEquals(1, linesRemoved, "should report exactly 1 cleared line");
        assertTrue(bonus > 0, "clearing a line should yield a positive bonus");

        // after clearing bottom row, previous row 2 should now be at bottom (index 3)
        int[] afterBottom = rowSnapshot(b, 3);
        assertArrayEquals(beforeRow2, afterBottom, "rows should shift down after clearing a single line");

        // top row should be zeroed
        int[] top = rowSnapshot(b, 0);
        for (int v : top) assertEquals(0, v, "top row should be empty after shift");
    }

    @Test
    void clearMultipleRows_updatesClearRowAndShiftsBoard() throws Exception {
        GameBoard b = newSmallBoard();

        // rows (top->bottom): 0:9, 1:8, 2:1, 3:1 -> clear two bottom rows
        setRow(b, 0, 9);
        setRow(b, 1, 8);
        setRow(b, 2, 1);
        setRow(b, 3, 1);

        int[] beforeRow1 = rowSnapshot(b, 1); // [8,8,8,8]
        int[] beforeRow0 = rowSnapshot(b, 0); // [9,9,9,9]

        Object cr = TestUtils.clearRows(b);

        int linesRemoved = getIntFromClearRow(cr,
                new String[] {"getLines","getRemovedLines","getLinesRemoved","getNumberOfLines","getCount","getRemoved"},
                new String[] {"lines","removed","count"}
        );
        int bonus = getIntFromClearRow(cr,
                new String[] {"getBonus","getScore","getScoreBonus","getPoints","getAddedScore"},
                new String[] {"bonus","score","points"}
        );

        assertEquals(2, linesRemoved, "should report exactly 2 cleared lines");
        assertTrue(bonus > 0, "clearing multiple lines should yield a positive bonus");

        // after clearing two bottom rows, bottom row (index 3) should equal previous row1
        int[] afterBottom = rowSnapshot(b, 3);
        assertArrayEquals(beforeRow1, afterBottom, "rows should shift down by two positions (bottom equals previous row1)");

        // row 2 should equal previous row0
        int[] afterRow2 = rowSnapshot(b, 2);
        assertArrayEquals(beforeRow0, afterRow2, "row two should equal previous top row after two-line clear");

        // top two rows should be zeroed
        int[] top0 = rowSnapshot(b, 0);
        int[] top1 = rowSnapshot(b, 1);
        for (int v : top0) assertEquals(0, v, "new top row should be empty after shift");
        for (int v : top1) assertEquals(0, v, "second top row should be empty after shift");
    }

    @Test
    void noLineClear_givesNoPoints_andLeavesBoardUnchanged() throws Exception {
        GameBoard b = newSmallBoard();

        // create a board with no complete filled row (one cell zero in each row)
        int[][] m = b.getBoardMatrix();
        for (int r = 0; r < m.length; r++) {
            for (int c = 0; c < m[r].length; c++) {
                m[r][c] = (c == 0) ? 0 : (r + 2); // ensure at least one zero per row
            }
        }

        int[][] before = new int[m.length][m[0].length];
        for (int r = 0; r < m.length; r++) System.arraycopy(m[r], 0, before[r], 0, m[r].length);

        Object cr = TestUtils.clearRows(b);

        int linesRemoved = getIntFromClearRow(cr,
                new String[] {"getLines","getRemovedLines","getLinesRemoved","getNumberOfLines","getCount","getRemoved"},
                new String[] {"lines","removed","count"}
        );
        int bonus = getIntFromClearRow(cr,
                new String[] {"getBonus","getScore","getScoreBonus","getPoints","getAddedScore"},
                new String[] {"bonus","score","points"}
        );

        assertEquals(0, linesRemoved, "no full rows should be reported as removed");
        assertEquals(0, bonus, "no-line-clear should not give points");

        // board should be unchanged
        int[][] after = b.getBoardMatrix();
        for (int r = 0; r < after.length; r++) {
            assertArrayEquals(before[r], after[r], "board should remain unchanged when no lines are cleared");
        }
    }
}
