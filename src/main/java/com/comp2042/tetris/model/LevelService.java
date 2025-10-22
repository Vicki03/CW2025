package com.comp2042.tetris.model;

import java.util.NavigableMap;
import java.util.TreeMap;

/** Maps score thresholds to gravity (milliseconds between auto-drops). */
public final class LevelService {
    private final NavigableMap<Integer, Integer> gravityByScore = new TreeMap<>();
    private final int minGravityMs;

    //tuned using the 0.8 multiplier from the NES Tetris
    public LevelService() {
        gravityByScore.put(0,     400); // Level 1 (modified 800 -> 400 for better starting speed)
        gravityByScore.put(1000,  320); // Level 2
        gravityByScore.put(1500,  260); // Level 3
        gravityByScore.put(2000,  200); // Level 4
        this.minGravityMs = 120;        // safety cap
    }

    /** ms between drops for a given score. */
    public int gravityMsForScore(int score) {
        return Math.max(gravityByScore.floorEntry(score).getValue(), minGravityMs);
    }

    public int levelForScore(int score) {
        return gravityByScore.headMap(score, true).size();
    }
}
