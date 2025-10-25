package com.comp2042.tetris.model;

import com.comp2042.tetris.model.brick.Brick;
import com.comp2042.tetris.model.brick.BrickGenerator;
import com.comp2042.tetris.model.brick.RandomBrickGenerator;
import com.comp2042.tetris.model.rules.BrickRotator;
import com.comp2042.tetris.util.MatrixOperations;

import java.awt.*;

//manages game state including board matrix, current brick, score, and game logic
public class GameBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private ViewData nextViewData;

    //declares board dimensions, initializes game matrix, brick generator, rotator, and score
    public GameBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

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

    //generates a new brick, sets initial position, checks for collision at starting position
    @Override
    public boolean createNewBrick() {
        // consume the next brick as the current one
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(4, 0);

        // peek the generator for the upcoming brick (do not consume it)
        Brick upcoming = brickGenerator.getNextBrick();
        if (upcoming != null) {
            int[][] previewShape = upcoming.getShapeMatrix().get(0);
            // construct ViewData for preview; positions don't matter for the small preview panel
            nextViewData = new ViewData(previewShape, 0, 0, previewShape);
        } else {
            nextViewData = null;
        }

        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }


    //returns current game matrix
    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        //use cached preview data so view and board stay consistent
        int[][] previewShape = null;
        if(nextViewData != null){
            previewShape = nextViewData.getBrickData();
        }else{
            Brick peek = brickGenerator.getNextBrick();
            if(peek != null){
                previewShape = peek.getShapeMatrix().get(0);
            }
        }

        int x = (currentOffset != null) ? (int) currentOffset.getX() : 0;
        int y = (currentOffset != null) ? (int) currentOffset.getY(): 0;

        return new ViewData(brickRotator.getCurrentShape(), x, y, previewShape);
    }

    //merges the current brick into the board matrix
    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    //checks and removes completed rows, updates the game matrix, returns info about cleared rows
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }



    //resets the game state for a new game
    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }

    @Override
    public ViewData getNextBrickViewData() {
        return nextViewData;
    }
}
