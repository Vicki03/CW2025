package com.comp2042;

import com.comp2042.tetris.util.MatrixOperations;

public final class ClearRow {

    private final int linesRemoved; //maybe can display linesRemoved in the future
    private final int[][] newMatrix; //shows the new matrix after clearing the rows
    private final int scoreBonus;

    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    public int getLinesRemoved() {
        return linesRemoved;
    } //getter for no. lines removed

    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    public int getScoreBonus() {
        return scoreBonus;
    }
}
