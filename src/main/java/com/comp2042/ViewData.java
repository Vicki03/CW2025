package com.comp2042;

//stores current brick's shape and position, and the next brick's shape
public final class ViewData {

    private final int[][] brickData; //2D array for current brick's shape
    private final int xPosition; //current brick's x position
    private final int yPosition;
    private final int[][] nextBrickData; //2D array for next brick's shape

    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }
}
