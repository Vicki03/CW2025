package com.comp2042.tetris.events;


/**
 * Defines the different types of move or control events
 * that can occur during gameplay.
 * <p>
 * Each constant corresponds to a specific player or
 * system action that can be passed to the event listener.
 * </p>
 */
// type of event in the game
//use this when wanna add a new button/ action, jz add a constant
public enum EventType {

    /** Move the active piece down by one row. */
    DOWN,

    /** Move the active piece left by one column. */
    LEFT,

    /** Move the active piece right by one column. */
    RIGHT,

    /** Rotate the active piece (typically counter-clockwise). */
    ROTATE,

    /** Instantly drop the active piece to its lowest valid position. */
    HARD_DROP,

    /** Hold or swap the active piece with the currently held piece. */
    HOLD
}
