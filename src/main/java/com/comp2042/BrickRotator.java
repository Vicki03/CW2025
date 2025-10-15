package com.comp2042;

import com.comp2042.logic.bricks.Brick;

public class BrickRotator {

    private Brick brick;
    private int currentShape = 0; //tracks the current rotation of the brick

    //method to get next rotation index of the Brick object
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size(); //increment nextShape and wrap around
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape); //return next shape matrix and index
    }


    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    } //method to get current rotation matrix

    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;//reset rotation index to initial state
    }


}
