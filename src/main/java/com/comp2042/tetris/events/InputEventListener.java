package com.comp2042.tetris.events;

//interface for handling game input events.
//means methods that must be implemented by any class that wants to handle input events in the game.

import com.comp2042.tetris.model.ViewData;
import com.comp2042.tetris.model.DownData;

/**
 * Defines the callback methods that respond to gameplay input events.
 * <p>
 * Any class that implements this interface (such as
 * {@link com.comp2042.tetris.controller.GameController})
 * must handle player or system-triggered move events and update
 * the model and view accordingly.
 * </p>
 *
 * <p><b>Purpose:</b></p>
 * <ul>
 *     <li>Provide a unified input event contract between the GUI and game logic.</li>
 *     <li>Translate key actions (left, right, down, rotate, hard drop, hold) into model updates.</li>
 *     <li>Support starting new game sessions via {@link #createNewGame()}.</li>
 * </ul>
 */
public interface InputEventListener {

    /**
     * Handles a single downward move event (either from user or gravity timer).
     *
     * @param event the {@link MoveEvent} that triggered the action
     * @return a {@link DownData} object containing information about cleared rows and updated view data
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Handles moving the active brick one cell to the left.
     *
     * @param event the {@link MoveEvent} representing the left movement
     * @return updated {@link ViewData} after performing the move
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Handles moving the active brick one cell to the right.
     *
     * @param event the {@link MoveEvent} representing the right movement
     * @return updated {@link ViewData} after performing the move
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Handles rotation of the active brick (typically counter-clockwise).
     *
     * @param event the {@link MoveEvent} representing the rotation
     * @return updated {@link ViewData} after performing the rotation
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Handles the hard drop event which instantly dropping the brick
     * to its lowest valid position and locking it in place.
     *
     * @param event the {@link MoveEvent} representing the hard drop action
     * @return a {@link DownData} object containing information about cleared rows and updated view data
     */
    DownData onHardDropEvent(MoveEvent event);

    /**
     * Handles the hold/swap action for the current piece.
     *
     * @param event the {@link MoveEvent} representing the hold action
     * @return updated {@link ViewData} after performing the hold/swap
     */
    ViewData onHoldEvent(MoveEvent event);

    /**
     * Resets the game state and starts a new session.
     */
    void createNewGame();
}
