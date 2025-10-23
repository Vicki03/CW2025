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
            //viewGuiController.showLevel(levelService.levelForScore(s));
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
            if (board.createNewBrick()) { //tries to create a new brick, if cant, then game over
                viewGuiController.gameOver();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix()); //updates the GUI with new board state

        } else {
            if (event.getEventSource() == EventSource.USER) {  //if the move was triggered by user
                board.getScore().add(1); //add 1 point for user
            }
        }
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
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        currentLevel = 1; //reset level tracker
        //reset gravity when a new game starts
        int g = levelService.gravityMsForScore(board.getScore().scoreProperty().get());
        viewGuiController.setGravityMs(g);
        viewGuiController.showLevel(levelService.levelForScore(board.getScore().scoreProperty().get()));
    }
}
