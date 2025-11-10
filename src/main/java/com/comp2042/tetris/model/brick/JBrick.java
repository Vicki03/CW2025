package com.comp2042.tetris.model.brick;

import com.comp2042.tetris.util.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "J" shaped Tetris brick (a left-facing L block).
 * <p>
 * This brick consists of three blocks in a line with one block attached
 * below the left side, forming a "J" shape. It supports four rotation states:
 * up, right, down, and left.
 * </p>
 *
 * <p>
 * Each rotation state is stored as a 4×4 integer matrix, where
 * {@code 2} indicates a filled cell and {@code 0} an empty cell.
 * The returned list of matrices from {@link #getShapeMatrix()} is deep-copied
 * via {@link MatrixOperations#deepCopyList(List)} to ensure immutability.
 * </p>
 */
final class JBrick implements Brick {

    /** All four rotation states of the J-brick. */
    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new J-brick and initializes its four rotation states.
     * <ul>
     *     <li>State 0 — horizontal with an extra block on the right end below.</li>
     *     <li>State 1 — vertical with base on the left side.</li>
     *     <li>State 2 — horizontal flipped version of state 0.</li>
     *     <li>State 3 — vertical flipped version of state 1.</li>
     * </ul>
     */
    public JBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {2, 2, 2, 0},
                {0, 0, 2, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 2, 2, 0},
                {0, 2, 0, 0},
                {0, 2, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 2, 2, 2},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 2, 0},
                {0, 0, 2, 0},
                {0, 2, 2, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns a deep copy of all four rotation states for this J-brick.
     * <p>
     * Each element of the returned list represents one rotation matrix.
     * </p>
     *
     * @return a list of 4×4 matrices representing the J-brick’s rotations
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}
