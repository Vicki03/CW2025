package com.comp2042.tetris.model;

/**
 * Defines the core contract for the Tetris game board model.
 * <p>
 * Implementations (such as {@code GameBoard}) are responsible for maintaining
 * the playfield state, managing active and background bricks, handling
 * collisions, scoring, and generating new pieces.
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *   <li>Manage the active falling brick and its position.</li>
 *   <li>Detect collisions and merge bricks into the background matrix.</li>
 *   <li>Handle line clearing, scoring, and level progression.</li>
 *   <li>Support hold and next-brick preview features.</li>
 *   <li>Provide lightweight {@link ViewData} snapshots for rendering.</li>
 * </ul>
 */
public interface Board {

    /**
     * Attempts to move the active brick one row downward.
     *
     * @return {@code true} if the move succeeded, {@code false} if blocked
     */
    boolean moveBrickDown();

    /**
     * Attempts to move the active brick one column to the left.
     *
     * @return {@code true} if the move succeeded, {@code false} if blocked
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean moveBrickLeft();

    /**
     * Attempts to move the active brick one column to the right.
     *
     * @return {@code true} if the move succeeded, {@code false} if blocked
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean moveBrickRight();

    /**
     * Attempts to rotate the active brick counter-clockwise.
     *
     * @return {@code true} if the rotation succeeded, {@code false} if blocked
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean rotateLeftBrick();

    /**
     * Spawns a new brick at the top of the board.
     *
     * @return {@code true} if the new brick immediately collides (i.e. game over), otherwise {@code false}
     */
    boolean createNewBrick();

    /**
     * Returns the current background board matrix.
     * <p>
     * Each cell contains an integer color index or {@code 0} for empty.
     * </p>
     *
     * @return a 2D integer array representing the current board state
     */
    int[][] getBoardMatrix();

    /**
     * Returns the {@link ViewData} snapshot for the active brick’s current position.
     *
     * @return current brick’s view data
     */
    ViewData getViewData();

    /**
     * Merges the active brick’s cells into the background matrix
     * when it can no longer move downward.
     */
    void mergeBrickToBackground();

    /**
     * Scans the board for completed rows, clears them, and updates score.
     *
     * @return a {@link ClearRow} object describing removed lines and score bonus
     */
    ClearRow clearRows();

    /**
     * Returns the board’s scoring object.
     *
     * @return the {@link Score} tracker
     */
    Score getScore();

    /**
     * Resets the entire game state to start a new session.
     */
    void newGame();

    /**
     * Returns a {@link ViewData} describing the queued “next” brick for preview.
     * <p>May return {@code null} if no next piece is available.</p>
     *
     * @return next brick’s view data or {@code null}
     */
    ViewData getNextBrickViewData();

    /**
     * Default alias for {@link #getNextBrickViewData()}.
     *
     * @return next brick’s view data
     */
    default ViewData getNextViewData(){
        return getNextBrickViewData();
    }

    /**
     * Returns a ViewData describing the currently held block for preview
     * May return null if nothing is held
     */

    /**
     * Returns a {@link ViewData} describing the currently held brick for preview.
     * <p>May return {@code null} if no brick is held.</p>
     *
     * @return held brick’s view data or {@code null}
     */
    ViewData getHeldBrickViewData();

    /**
     * Performs a hold or swap action for the current falling brick.
     * <p>
     * If a hold is already in use, swaps the active brick with the held one.
     * If the resulting spawn causes an immediate collision, the game ends.
     * </p>
     *
     * @return {@code true} if the resulting spawn caused an immediate collision (game over)
     */
    boolean holdCurrentBrick();
}