package com.comp2042.tetris.model.brick;

import com.comp2042.tetris.util.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "O" shaped Tetris brick (the square block).
 * <p>
 * This brick consists of a 2×2 block of filled cells and does not
 * change shape when rotated, so it has only one rotation state.
 * </p>
 *
 * <p>
 * The rotation state is stored as a 4×4 integer matrix where
 * {@code 4} indicates a filled cell and {@code 0} an empty cell.
 * The list returned by {@link #getShapeMatrix()} is deep-copied using
 * {@link MatrixOperations#deepCopyList(List)} to prevent external modification.
 * </p>
 */
final class OBrick implements Brick {

    /** Single rotation state for the O-brick (square). */
    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new O-brick (square block) and initializes
     * its single 4×4 rotation matrix.
     */
    public OBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 4, 4, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns the single rotation matrix for the O-brick.
     * <p>
     * The returned list contains one element representing the
     * square’s fixed shape.
     * </p>
     *
     * @return a list containing the single 4×4 matrix for the O-brick
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

}
