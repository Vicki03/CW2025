package com.comp2042.tetris.events;

/**
 * Identifies the origin of a game event.
 * <p>
 * Used to distinguish between user-initiated actions (keyboard input)
 * and background/timer-driven actions (gravity ticks, animations, etc.).
 * </p>
 */
//this is to see whether an event is triggered by user or background process
public enum EventSource {
    /**
     * Event originated from a player action (e.g., key press).
     */
    USER,

    /**
     * Event originated from a background process or timer thread
     * (e.g., automatic gravity tick).
     */
    THREAD
}
