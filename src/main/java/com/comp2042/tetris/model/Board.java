package com.comp2042.tetris.model;

public interface Board {

    boolean moveBrickDown();

    @SuppressWarnings("UnusedReturnValue")
    boolean moveBrickLeft();

    @SuppressWarnings("UnusedReturnValue")
    boolean moveBrickRight();

    @SuppressWarnings("UnusedReturnValue")
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

    /**
     * Returns a ViewData describing the currently held block for preview
     * May return null if nothing is held
     */

    ViewData getHeldBrickViewData();

    /**
     * Hold / swap the current falling brick.
     * Returns true if the resulting spawn/swap caused an immediate collision (i.e., game over).
     */
    boolean holdCurrentBrick();
}