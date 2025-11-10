package com.comp2042.tetris.model;

import com.comp2042.tetris.util.MatrixOperations;

/**
 * Encapsulates information about the next rotation state of a brick.
 * <p>
 * A {@code NextShapeInfo} object stores both:
 * <ul>
 *     <li>The next shape matrix (2D array) representing the rotated form of the brick.</li>
 *     <li>The corresponding rotation index within the brick’s shape list.</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class is primarily used by {@link com.comp2042.tetris.model.rules.BrickRotator}
 * to handle rotation transitions. The {@link #getShape()} method returns a deep copy
 * of the shape matrix to preserve immutability.
 * </p>
 */
public final class NextShapeInfo {

    /** The 2D matrix representing the brick’s next rotation state. */
    private final int[][] shape;

    /** The index of the next rotation position in the brick’s shape list. */
    private final int position;

    /**
     * Constructs a {@code NextShapeInfo} with the specified shape and rotation index.
     *
     * @param shape     the 2D matrix of the next rotation state
     * @param position  the rotation index within the brick’s shape list
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Returns a deep copy of the next rotation matrix.
     * <p>
     * The returned matrix is copied using {@link MatrixOperations#copy(int[][])}
     * to prevent external modifications to the internal state.
     * </p>
     *
     * @return a copy of the 2D shape matrix for the next rotation
     */
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    /**
     * Returns the rotation index of this shape.
     *
     * @return the rotation position index
     */
    public int getPosition() {
        return position;
    }
}
