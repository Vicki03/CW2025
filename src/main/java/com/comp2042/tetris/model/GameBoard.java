package com.comp2042.tetris.model;

import com.comp2042.tetris.model.brick.Brick;
import com.comp2042.tetris.model.brick.BrickGenerator;
import com.comp2042.tetris.model.brick.RandomBrickGenerator;
import com.comp2042.tetris.model.rules.BrickRotator;
import com.comp2042.tetris.util.MatrixOperations;

import java.awt.*;

/**
 * Concrete implementation of the {@link Board} game model.
 * <p>
 * Maintains the playfield matrix, active/held bricks, spawn/hold rules, movement and rotation,
 * line clearing, score updates, and provides lightweight {@link ViewData} snapshots for rendering.
 * </p>
 *
 * <h3>Coordinate system</h3>
 * <ul>
 *   <li>{@code currentGameMatrix[row][col]} stores the background board (0 = empty, &gt;0 = filled/color id).</li>
 *   <li>{@link Point} {@code currentOffset} uses {@code (x,y)} == {@code (col,row)} of the active brick’s top-left corner.</li>
 *   <li>Rotations and collision checks are performed using {@link BrickRotator} and {@link MatrixOperations} utilities.</li>
 * </ul>
 *
 * <h3>Next &amp; Hold</h3>
 * <ul>
 *   <li>{@code brickGenerator} supplies the active and next bricks; {@code nextViewData} caches the preview.</li>
 *   <li>Holding is allowed once per spawn; swap with held brick resets rotation and position to the spawn offset.</li>
 * </ul>
 */
//manages game state including board matrix, current brick, score, and game logic
public class GameBoard implements Board {

    /** Number of rows in the playfield (named width in legacy code). */
    private final int width;

    /** Number of columns in the playfield (named height in legacy code). */
    private final int height;

    /** Supplies current/next bricks. */
    private final BrickGenerator brickGenerator;

    /** Tracks and advances rotation states for the active brick. */
    private final BrickRotator brickRotator;

    /** Background board matrix (0 = empty, >0 = filled). Indexed as [row][col]. */
    private int[][] currentGameMatrix;

    /** Top-left (x=col, y=row) placement of the active brick’s current shape. */
    private Point currentOffset;

    /** Score tracker for the session. */
    private final Score score;

    /** Cached view data for the “next” preview. */
    private ViewData nextViewData;

    //hold support
    /** Currently active falling brick. */
    private Brick currentBrick;

    /** Brick stored in the hold slot (may be null). */
    private Brick heldBrick;

    /** True after a hold has been used for the current spawn (enforces 1 hold per spawn). */
    private boolean holdUsed;

    /**
     * Creates a new {@code GameBoard} with the specified dimensions and default subsystems.
     *
     * @param width  number of rows in the playfield
     * @param height number of columns in the playfield
     */
    //declares board dimensions, initializes game matrix, brick generator, rotator, and score
    public GameBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    /**
     * Attempts to move the active brick one row downward.
     * <p>Returns {@code false} if a collision would occur.</p>
     *
     * @return {@code true} if the move succeeded; {@code false} if blocked
     */
    //copies the current matrtix, calcs the new position by moving down, checks for collision
    //updates position if no conflict
    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Attempts to move the active brick one column to the left.
     *
     * @return {@code true} if the move succeeded; {@code false} if blocked
     */
    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Attempts to move the active brick one column to the right.
     *
     * @return {@code true} if the move succeeded; {@code false} if blocked
     */
    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Attempts to rotate the active brick counter-clockwise.
     * <p>Applies the next rotation state if it does not collide.</p>
     *
     * @return {@code true} if the rotation succeeded; {@code false} if blocked
     */
    //gets next rotation shape, check for collision, updates shape if no conflict
    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    /**
     * Spawns a new active brick at the spawn position and refreshes the "next" preview cache.
     * <p>
     * Also resets {@code holdUsed=false} for the new spawn. Returns {@code true} if the newly
     * spawned brick immediately collides with the background (i.e., game over).
     * </p>
     *
     * @return {@code true} if an immediate collision occurs (game over); otherwise {@code false}
     */
    //generates a new brick, sets initial position, checks for collision at starting position
    @Override
    public boolean createNewBrick() {
        // consume the next brick as the current one
        this.currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(this.currentBrick);
        currentOffset = new Point(4, 0);

        //allow hold again for the new spawn
        holdUsed = false;

        // createNewBrick()
        Brick upcoming = brickGenerator.getNextBrick();
        if (upcoming != null) {
            int[][] previewShape = upcoming.getShapeMatrix().getFirst();  // <-- was get(0)
            nextViewData = new ViewData(previewShape, 0, 0, previewShape);
        }

        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }


    /**
     * Returns the current background board matrix.
     *
     * @return the board matrix (rows × cols) with 0=empty, &gt;0=filled
     */
    //returns current game matrix
    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    /**
     * Returns a {@link ViewData} snapshot describing the active brick and the next preview.
     * <p>
     * Uses cached {@code nextViewData} when available; otherwise peeks the generator.
     * </p>
     *
     * @return the current {@link ViewData} for rendering
     */
    @Override
    public ViewData getViewData() {
        //use cached preview data so view and board stay consistent
        int[][] previewShape = null;
        if(nextViewData != null){
            previewShape = nextViewData.getBrickData();
        }else{
            // getViewData()
            Brick peek = brickGenerator.getNextBrick();
            if (peek != null) {
                previewShape = peek.getShapeMatrix().getFirst();              // <-- was get(0)
            }

        }

        int x = (currentOffset != null) ? (int) currentOffset.getX() : 0;
        int y = (currentOffset != null) ? (int) currentOffset.getY(): 0;

        return new ViewData(brickRotator.getCurrentShape(), x, y, previewShape);
    }

    /**
     * Merges the active brick’s cells into the background matrix (lockdown).
     */
    //merges the current brick into the board matrix
    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    /**
     * Clears any completed rows, updates the background matrix, and returns detailed info.
     *
     * @return a {@link ClearRow} describing removed lines and the score bonus
     */
    //checks and removes completed rows, updates the game matrix, returns info about cleared rows
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    /**
     * Returns the score tracker.
     *
     * @return the {@link Score} object
     */
    @Override
    public Score getScore() {
        return score;
    }


    /**
     * Resets the entire game state and spawns the first brick.
     */
    //resets the game state for a new game
    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }

    /**
     * Returns the cached “next” brick preview data.
     *
     * @return {@link ViewData} for the next piece, or {@code null} if unavailable
     */
    @Override
    public ViewData getNextBrickViewData() {
        return nextViewData;
    }

    /**
     * Performs the Hold action: store current brick if none is held, or swap with held brick.
     * <p>
     * Enforces “one hold per spawn” with {@code holdUsed}. After a swap, rotation is reset
     * and position returns to the spawn offset. Returns {@code true} if the resulting spawn/swap
     * causes an immediate collision (game over).
     * </p>
     *
     * @return {@code true} if an immediate collision occurs after hold/swap (game over)
     */
    @Override
    public boolean holdCurrentBrick() {
        // prevent multiple holds in the same spawn
        if (holdUsed) return false;

        // If nothing is held, store current and spawn next.
        if (heldBrick == null) {
            heldBrick = currentBrick;
            // spawn next brick (createNewBrick() resets holdUsed to false for normal spawns)
            boolean gameOver = createNewBrick();
            // mark hold used for the new current piece (prevent another hold until next spawn)
            holdUsed = true;
            return gameOver;
        } else {
            // swap held and current
            Brick temp = heldBrick;
            heldBrick = currentBrick;
            currentBrick = temp;
            brickRotator.setBrick(currentBrick);
            currentOffset = new Point(4, 0);
            // mark hold used for this spawn
            holdUsed = true;
            // check immediate collision after swap
            return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(),
                    (int) currentOffset.getX(), (int) currentOffset.getY());

        }
    }

    /**
     * Returns a preview {@link ViewData} for the currently held brick.
     *
     * @return held brick preview, or {@code null} if nothing is held
     */
    @Override
    public ViewData getHeldBrickViewData(){
        if(heldBrick == null) return null;
        int[][] previewShape = heldBrick.getShapeMatrix().getFirst();
        return new ViewData(previewShape, 0, 0, previewShape);
    }
}
