package com.comp2042.tetris.model.brick;

/**
 * Defines a generator responsible for producing {@link Brick} instances.
 * <p>
 * Implementations of this interface handle random brick selection
 * and manage the "next" preview piece shown to the player.
 * </p>
 *
 * <p>
 * Typical usage:
 * <ul>
 *   <li>{@link #getBrick()} — retrieves the current brick and advances the queue.</li>
 *   <li>{@link #getNextBrick()} — previews the upcoming brick without consuming it.</li>
 * </ul>
 * </p>
 */
public interface BrickGenerator {

    /**
     * Returns a new {@link Brick} instance for the current turn.
     * <p>
     * This call usually consumes the "next" brick and generates a new
     * one for the following turn.
     * </p>
     *
     * @return the active {@link Brick} to be used in the current round
     */
    Brick getBrick();

    /**
     * Returns the next {@link Brick} that will appear after the current one.
     * <p>
     * This method does <em>not</em> consume the brick — it only previews
     * it for display in the "next" panel of the GUI.
     * </p>
     *
     * @return the upcoming {@link Brick} instance
     */
    Brick getNextBrick();
}
