package com.comp2042.tetris.model;

import java.util.NavigableMap;
import java.util.TreeMap;

/** Maps score thresholds to gravity (milliseconds between auto-drops). */
public final class LevelService {
    private final int firstLevelThreshold = 1000;
    private final int subsequentStep = 500; //step for subsequent levels
    private final int baseGravityMs = 400; //base gravity for level 1
    private final int decrementPerLevelMs = 30; //gravity decrement per level
    private final int minGravityMs = 120;

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
