package com.comp2042.tetris.controller;

import com.comp2042.tetris.events.EventSource;
import com.comp2042.tetris.events.InputEventListener;
import com.comp2042.tetris.events.MoveEvent;
import com.comp2042.tetris.model.*;

import javafx.beans.value.ChangeListener;

/**
 * Coordinates the core game flow between the GUI and the domain model.
 * <p>
 * This controller wires {@link GuiController} (the view) to the {@link Board}
 * (the model), listens for input/timer events via {@link InputEventListener},
 * applies game rules (movement, hard drop, hold, line clears, scoring),
 * and updates the view accordingly (board state, next/held previews, gravity, level).
 * </p>
 */
public final class GameController implements InputEventListener {

    /**
     * The game board (25 rows x 10 columns).
     */
    //creates new game board with 25 rows and 10 columns
    //can change to higher height next time(?)
    private final Board board = new GameBoard(25, 10);

    /**
     * The GUI controller used to display board state and UI elements.
     */
    private final GuiController viewGuiController;

    /**
     * Level/gravity service mapping score → gravity interval and level number.
     */
    private final LevelService levelService = new LevelService();

    /**
     * Tracks the current level for level-up notifications.
     */
    private int currentLevel = 1;

    /**
     * Creates a new game controller and initializes the view and model for a new session.
     * <ul>
     *   <li>Spawns the first brick</li>
     *   <li>Binds score to the view</li>
     *   <li>Sets initial gravity based on current score</li>
     *   <li>Registers a score listener to update gravity/level and show level-up</li>
     * </ul>
     *
     * @param c the GUI controller to connect to this game session
     */
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

        ChangeListener<Number> scoreListener = (_, _, newVal) -> {
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

    /**
     * Handles a single-step down move (timer tick or user press).
     * <p>
     * If the piece cannot move further, merges it into the background, clears any full rows,
     * applies score bonuses, and spawns the next piece (or ends the game if blocked).
     * If the move was user-initiated and successful, awards +1 point.
     * </p>
     *
     * @param event the move event that triggered the downward step
     * @return a {@link DownData} bundle with any clear-row info and fresh {@link ViewData}
     */
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

    /**
     * Moves the active piece one cell to the left (if possible) and returns fresh view data.
     *
     * @param event the move event that triggered the left action
     * @return updated {@link ViewData} after attempting the move
     */
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    /**
     * Moves the active piece one cell to the right (if possible) and returns fresh view data.
     *
     * @param event the move event that triggered the right action
     * @return updated {@link ViewData} after attempting the move
     */
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }
    /**
     * Rotates the active piece (counter-clockwise in this implementation) and returns fresh view data.
     *
     * @param event the move event that triggered the rotation
     * @return updated {@link ViewData} after attempting the rotation
     */
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    /**
     * Handles the hold action: swaps current/held pieces or stores the current piece if none is held.
     * <p>
     * Updates the next and held previews, and ends the game if a new spawn is blocked.
     * </p>
     *
     * @param event the move event that triggered the hold action
     * @return updated {@link ViewData} after performing the hold
     */
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


    /**
     * Resets the model and view to start a fresh game session.
     * <ul>
     *   <li>Clears board and score</li>
     *   <li>Spawns a new piece</li>
     *   <li>Resets level state and gravity</li>
     *   <li>Refreshes background and next preview</li>
     * </ul>
     */
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
