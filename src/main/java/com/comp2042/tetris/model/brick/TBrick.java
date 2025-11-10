package com.comp2042.tetris.model.brick;

import com.comp2042.tetris.util.MatrixOperations;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents the "T" shaped Tetris brick (the T block).
 * <p>
 * This brick consists of three blocks in a row with one additional block
 * centered beneath the middle, forming a "T" shape. It supports four
 * rotation states (up, right, down, and left).
 * </p>
 *
 * <p>
 * Each rotation state is stored as a 4×4 integer matrix, where
 * {@code 6} indicates a filled cell and {@code 0} an empty cell.
 * The returned list from {@link #getShapeMatrix()} is deep-copied using
 * {@link MatrixOperations#deepCopyList(List)} to prevent external modification.
 * </p>
 */
final class TBrick implements Brick {

    /** All four rotation states of the T-brick. */
    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new T-brick and initializes its four rotation states.
     * <ul>
     *     <li>State 0 — upright T (flat on top).</li>
     *     <li>State 1 — rotated 90° clockwise.</li>
     *     <li>State 2 — upside-down T.</li>
     *     <li>State 3 — rotated 270° clockwise (left-facing).</li>
     * </ul>
     */
    public TBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {6, 6, 6, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 6, 0, 0},
                {0, 6, 6, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 6, 0, 0},
                {6, 6, 6, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 6, 0, 0},
                {6, 6, 0, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns a deep copy of all four rotation states for this T-brick.
     * <p>
     * Each element of the returned list represents one rotation matrix.
     * </p>
     *
     * @return a list of 4×4 matrices representing the T-brick’s rotations
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}
