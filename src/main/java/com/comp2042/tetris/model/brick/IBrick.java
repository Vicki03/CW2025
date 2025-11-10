package com.comp2042.tetris.model.brick;

import com.comp2042.tetris.util.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "I" shaped Tetris brick (a straight block).
 * <p>
 * This brick consists of four consecutive blocks forming a line.
 * It supports two rotation states: horizontal and vertical.
 * </p>
 *
 * <p>
 * Internally, each rotation state is stored as a 4×4 integer matrix
 * where {@code 1} indicates a filled cell and {@code 0} an empty cell.
 * The matrices are deep-copied via {@link MatrixOperations#deepCopyList(List)}
 * before being returned to prevent external modification.
 * </p>
 */
final class IBrick implements Brick {

    /** All rotation states of the I-brick (horizontal and vertical). */
    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new I-brick and initializes its two rotation states.
     * <ul>
     *     <li>State 0 — horizontal line of four blocks.</li>
     *     <li>State 1 — vertical line of four blocks.</li>
     * </ul>
     */
    public IBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0}
        });
    }

    /**
     * Returns a deep copy of all rotation states for this I-brick.
     * <p>
     * Each element of the returned list represents one rotation matrix.
     * The deep copy ensures the original matrices remain immutable.
     * </p>
     *
     * @return a list of 4×4 matrices representing the I-brick’s rotations
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

}
