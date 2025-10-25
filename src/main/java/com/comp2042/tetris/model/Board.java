package com.comp2042.tetris.model;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();

    /**
     * Returns a ViewData describing the queued "next" block for preview
     * May return null if not available
     */
    ViewData getNextBrickViewData();

    default ViewData getNextViewData(){
        return getNextBrickViewData();
    }
}
