package com.comp2042.tetris.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Represents the player’s score in the Tetris game.
 * <p>
 * This class maintains an observable {@link IntegerProperty} that automatically
 * updates the UI when the score changes, enabling real-time score display in
 * the JavaFX view.
 * </p>
 *
 * <p>
 * It provides simple methods to add points and reset the score, and exposes
 * the property for binding (via {@link #scoreProperty()}).
 * </p>
 *
 * <p>
 * Used by {@link com.comp2042.tetris.controller.GameController} and
 * {@link com.comp2042.tetris.controller.GuiController} to synchronize the model
 * and view during gameplay.
 * </p>
 */
public final class Score {

    /** Observable integer property for the player’s score. */
    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Returns the observable score property for UI binding.
     * <p>
     * The property can be bound to a JavaFX {@code Label} to display
     * live updates when the score changes.
     * </p>
     *
     * @return the {@link IntegerProperty} representing the current score
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Adds the specified amount to the current score.
     *
     * @param i the number of points to add
     */
    public void add(int i){
        score.setValue(score.getValue() + i);
    }

    /**
     * Resets the score to zero.
     */
    public void reset() {
        score.setValue(0);
    }
}
