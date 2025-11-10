package com.comp2042.tetris.events;

/**
 * Represents a movement or control event in the game.
 * <p>
 * Each {@code MoveEvent} instance contains both the {@link EventType}
 * (what kind of action occurred) and the {@link EventSource}
 * (who or what triggered it, whether if its user or background thread).
 * </p>
 * <p>
 * These events are created by the GUI (e.g., key presses or timer ticks)
 * and passed to {@link InputEventListener} implementations such as
 * {@link com.comp2042.tetris.controller.GameController}.
 * </p>
 */
public final class MoveEvent {

    /** The type of movement or action (e.g., LEFT, RIGHT, ROTATE). */
    private final EventType eventType;

    /** The source that triggered this event (USER or THREAD). */
    private final EventSource eventSource;

    /**
     * Constructs a new {@code MoveEvent} with the specified type and source.
     *
     * @param eventType   the {@link EventType} representing the action performed
     * @param eventSource the {@link EventSource} representing the origin of the event
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    /**
     * Returns the type of this move event.
     *
     * @return the {@link EventType} associated with this event
     */
    @SuppressWarnings("unused")
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Returns the origin of this move event.
     *
     * @return the {@link EventSource} that generated this event
     */
    public EventSource getEventSource() {
        return eventSource;
    }
}
