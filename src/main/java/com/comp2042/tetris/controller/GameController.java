package com.comp2042.tetris.controller;

import com.comp2042.*;
import com.comp2042.tetris.model.Board;
import com.comp2042.tetris.model.ClearRow;
import com.comp2042.tetris.model.SimpleBoard;

//coordinates main game logic. bridge between user actions and game state updates
public class GameController implements InputEventListener {

    //creates new game board with 25 rows and 10 columns
    //can change to higher height next time(?)
    private Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
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
    }
}
