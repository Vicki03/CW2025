package com.comp2042.tetris.model;

/** Maps score thresholds to gravity (milliseconds between auto-drops). */
public final class LevelService {
    private static final int firstLevelThreshold = 1000;
    private static final int subsequentStep = 500; //step for subsequent levels
    private static final int baseGravityMs = 400; //base gravity for level 1
    private static final int decrementPerLevelMs = 30; //gravity decrement per level
    private static final int minGravityMs = 120;

    /** ms between drops for a given score. */
    public int gravityMsForScore(int score){
        int level = levelForScore(score);
        int ms = baseGravityMs - (level - 1) * decrementPerLevelMs;
        return Math.max(ms, minGravityMs);
    }

    public int levelForScore(int score){
        if(score < firstLevelThreshold){
            return 1;
        }
        return 1 + ((score - firstLevelThreshold) / subsequentStep);
    }

}
