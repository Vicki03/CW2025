package com.comp2042.tetris.model;

/**
 * Provides level and gravity (fall speed) calculations based on the player’s score.
 * <p>
 * This service maps score thresholds to:
 * <ul>
 *   <li><b>Level number</b> — increases every fixed score interval.</li>
 *   <li><b>Gravity delay</b> — milliseconds between automatic downward moves,
 *       decreasing as level increases.</li>
 * </ul>
 * </p>
 *
 * <p>
 * The gravity speed cannot go below a minimum threshold, ensuring gameplay remains
 * fast but still manageable at high levels.
 * </p>
 *
 * <p>
 * Used primarily by {@link com.comp2042.tetris.controller.GameController} to
 * adjust the {@code Timeline} drop rate dynamically when the score changes.
 * </p>
 */
public final class LevelService {

    /** Score required to reach Level 2. */
    private static final int firstLevelThreshold = 1000;

    /** Additional score required per subsequent level. */
    private static final int subsequentStep = 500; //step for subsequent levels

    /** Base gravity interval (ms) for Level 1. */
    private static final int baseGravityMs = 400; //base gravity for level 1

    /** Gravity reduction (ms) per level increase. */
    private static final int decrementPerLevelMs = 30; //gravity decrement per level

    /** Minimum possible gravity interval (ms). */
    private static final int minGravityMs = 120;

    /**
     * Calculates the gravity interval (milliseconds per automatic drop)
     * corresponding to the given score.
     *
     * @param score the current total score
     * @return gravity interval in milliseconds, never below {@link #minGravityMs}
     */
    public int gravityMsForScore(int score){
        int level = levelForScore(score);
        int ms = baseGravityMs - (level - 1) * decrementPerLevelMs;
        return Math.max(ms, minGravityMs);
    }

    /**
     * Calculates the current level number based on the given score.
     * <p>
     * Level 1 starts from score 0. Each additional {@link #subsequentStep}
     * points increases the level after reaching {@link #firstLevelThreshold}.
     * </p>
     *
     * @param score the player’s current score
     * @return the level number (≥ 1)
     */
    public int levelForScore(int score){
        if(score < firstLevelThreshold){
            return 1;
        }
        return 1 + ((score - firstLevelThreshold) / subsequentStep);
    }

}
