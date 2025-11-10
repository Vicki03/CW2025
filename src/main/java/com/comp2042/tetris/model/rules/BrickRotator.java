package com.comp2042.tetris.model.rules;

import com.comp2042.tetris.model.NextShapeInfo;
import com.comp2042.tetris.model.brick.Brick;

/**
 * Handles the rotation logic for a {@link Brick}.
 * <p>
 * This class manages the current rotation index of the active brick and
 * provides methods to access both the current and next rotation states.
 * </p>
 *
 * <p>
 * The rotation state is tracked using an integer index corresponding
 * to the position within the brick’s list of rotation matrices.
 * Rotations wrap around cyclically when reaching the end of the list.
 * </p>
 */
public class BrickRotator {

    /** The current brick being rotated. */
    private Brick brick;

    /** The index of the current rotation state within the brick’s matrix list. */
    private int currentShape = 0; //tracks the current rotation of the brick

    /**
     * Computes and returns the next rotation state of the current brick.
     * <p>
     * The rotation index is incremented (cyclically) and wrapped around
     * when the last state is reached.
     * </p>
     *
     * @return a {@link NextShapeInfo} object containing the next rotation matrix
     *         and its corresponding index
     */
    //method to get next rotation index of the Brick object
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape; //change redundancy later
        nextShape = (++nextShape) % brick.getShapeMatrix().size(); //increment nextShape and wrap around
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape); //return next shape matrix and index
    }


    /**
     * Returns the current rotation matrix of the active brick.
     *
     * @return the current shape matrix for the brick
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    } //method to get current rotation matrix

    /**
     * Sets the current rotation index.
     * <p>
     * This can be used to manually reset or sync rotation state
     * (e.g., after rotating or spawning a new piece).
     * </p>
     *
     * @param currentShape the new rotation index to set
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Assigns a new brick to this rotator and resets its rotation state.
     *
     * @param brick the {@link Brick} to control
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;//reset rotation index to initial state
    }


}
