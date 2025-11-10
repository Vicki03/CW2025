package com.comp2042.tetris.model.brick;

import com.comp2042.tetris.util.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "S" shaped Tetris brick (a zigzag block).
 * <p>
 * This brick forms a mirrored Z shape, which is two blocks on the top row and
 * two offset on the bottom row. It supports two rotation states:
 * horizontal and vertical.
 * </p>
 *
 * <p>
 * Each rotation state is defined as a 4×4 integer matrix, where
 * {@code 5} represents a filled cell and {@code 0} an empty cell.
 * The returned list from {@link #getShapeMatrix()} is deep-copied
 * using {@link MatrixOperations#deepCopyList(List)} to preserve immutability.
 * </p>
 */
final class SBrick implements Brick {

    /** Both rotation states of the S-brick (horizontal and vertical). */
    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new S-brick and initializes its two rotation states.
     * <ul>
     *   <li>State 0 — horizontal “S” shape.</li>
     *   <li>State 1 — vertical “S” shape (rotated 90°).</li>
     * </ul>
     */
    public SBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 5, 5, 0},
                {5, 5, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {5, 0, 0, 0},
                {5, 5, 0, 0},
                {0, 5, 0, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns a deep copy of the S-brick’s two rotation states.
     *
     * @return a list of 4×4 matrices representing the S-brick’s rotations
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}
