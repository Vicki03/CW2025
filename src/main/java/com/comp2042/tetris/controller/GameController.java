package com.comp2042.tetris.controller;

import com.comp2042.tetris.events.EventSource;
import com.comp2042.tetris.events.InputEventListener;
import com.comp2042.tetris.events.MoveEvent;
import com.comp2042.tetris.model.*;

import javafx.beans.value.ChangeListener;

/**
 *Acts as the main game coordinator.
 *Connects the GUI and the underlying game model.
 *Implements InputEventListener to respond to player and timer inputs.
 */
public final class GameController implements InputEventListener {

    //creates new game board with 25 rows and 10 columns
    //can change to higher height next time(?)
    private final Board board = new GameBoard(25, 10);

    private final GuiController viewGuiController;

    private final LevelService levelService = new LevelService();

    private int currentLevel = 1;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.showNext(board.getNextViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());

        //set initial gravity and listen for score changes
        int initialScore = board.getScore().scoreProperty().get();
        int initialGravityMs = levelService.gravityMsForScore(initialScore);
        viewGuiController.setGravityMs(initialGravityMs); // add this method to GuiController

        ChangeListener<Number> scoreListener = (obs, oldVal, newVal) -> {
            int s = newVal.intValue();
            int g = levelService.gravityMsForScore(s);
            int level = levelService.levelForScore(s);
            viewGuiController.setGravityMs(g);
            viewGuiController.showLevel(level);


            //maybe add level display later
            if(level > currentLevel){
                viewGuiController.showLevelUpNotification(level);
                currentLevel = level;
            }
        };
        //attach the listener
        board.getScore().scoreProperty().addListener(scoreListener);
    }

    @Override
    public DownData onDownEvent(MoveEvent event) { //handles the event when a piece moves down
        boolean canMove = board.moveBrickDown(); //tries to move the piece down, returns false if it can't
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }
            boolean gameOver = board.createNewBrick();
            if(gameOver){
                viewGuiController.gameOver();
            } else{
                //update preview to the newly queued next piece
                viewGuiController.showNext(board.getNextViewData());
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix()); //updates the GUI with new board state

        } else {
            if (event.getEventSource() == EventSource.USER) {  //if the move was triggered by user
                board.getScore().add(1); //add 1 point for user
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    /**
     * Hard drop implementation: move piece down until it cannot move,
     * award drop bonus, merge, clear rows, and spawn next brick.
     */
    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        // count how many cells the piece falls
        int droppedCells = 0;
        while (board.moveBrickDown()) {
            droppedCells++;
        }

        //apply bonus for the hard drop
        if (droppedCells > 0 && event.getEventSource() == EventSource.USER) {
            board.getScore().add(droppedCells * 2); //2 points per cell
        }

        //finalize position
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
        }

        if (board.createNewBrick()) {
            viewGuiController.gameOver();
        } else{
            //update preview to the newly queued next piece
            viewGuiController.showNext(board.getNextViewData());
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());

        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        boolean gameOver = board.holdCurrentBrick();
        if(gameOver){
            viewGuiController.gameOver();
        }else{
            viewGuiController.showNext(board.getNextViewData());
        }
        viewGuiController.showHeld(board.getHeldBrickViewData());
        return board.getViewData();
    }


    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());

        viewGuiController.showNext(board.getNextViewData());
        currentLevel = 1; //reset level tracker
        //reset gravity when a new game starts
        int g = levelService.gravityMsForScore(board.getScore().scoreProperty().get());
        viewGuiController.setGravityMs(g);
        viewGuiController.showLevel(levelService.levelForScore(board.getScore().scoreProperty().get()));
    }
}
