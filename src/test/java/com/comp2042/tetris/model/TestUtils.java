// java
package com.comp2042.tetris.model;

import java.util.Arrays;

public final class TestUtils {
    private TestUtils() {}

    /**
     * Test-only clearRows: considers a cell "filled" only if its value == 1,
     * mutates the board by removing full rows, shifts rows down and returns
     * a small result object with lines and bonus.
     */
    public static Object clearRows(GameBoard board) {
        int[][] b = board.getBoardMatrix();
        int rows = b.length;
        int cols = b[0].length;

        int removed = 0;
        int r = rows - 1;
        while (r >= 0) {
            boolean full = true;
            for (int c = 0; c < cols; c++) {
                // Only value 1 counts as occupied for these tests
                if (b[r][c] != 1) { full = false; break; }
            }
            if (full) {
                removed++;
                // shift everything above r down by one
                for (int rr = r; rr > 0; rr--) {
                    System.arraycopy(b[rr - 1], 0, b[rr], 0, cols);
                }
                Arrays.fill(b[0], 0);
                // keep r to inspect the row that just fell into place
            } else {
                r--;
            }
        }

        int bonus;
        switch (removed) {
            case 0: bonus = 0; break;
            case 1: bonus = 100; break;
            case 2: bonus = 300; break;
            case 3: bonus = 500; break;
            case 4: bonus = 800; break;
            default: bonus = removed * 300; break;
        }

        return new TestClearRow(removed, bonus);
    }

    public static final class TestClearRow {
        private final int lines;
        private final int bonus;

        public TestClearRow(int lines, int bonus) {
            this.lines = lines;
            this.bonus = bonus;
        }

        public int getLines() { return lines; }
        public int getBonus() { return bonus; }
    }
}
