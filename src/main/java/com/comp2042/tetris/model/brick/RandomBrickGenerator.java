package com.comp2042.tetris.model.brick;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates random {@link Brick} instances for gameplay.
 * <p>
 * This implementation of {@link BrickGenerator} randomly selects
 * brick types (I, J, L, O, S, T, Z) and maintains a small queue
 * of upcoming bricks to support the "next piece" preview feature.
 * </p>
 *
 * <p><b>How it works:</b></p>
 * <ul>
 *   <li>All seven standard Tetris bricks are stored in a {@link List}.</li>
 *   <li>A {@link Deque} queue tracks the current and next brick.</li>
 *   <li>{@link #getBrick()} dequeues the current brick and replenishes the queue with a new random one.</li>
 *   <li>{@link #getNextBrick()} previews the next brick without removing it.</li>
 * </ul>
 *
 * <p>
 * Randomization is handled using {@link ThreadLocalRandom}, ensuring
 * each brick type has an equal chance of appearing.
 * </p>
 */
public class RandomBrickGenerator implements BrickGenerator {

    /** Holds all seven available brick prototypes (I, J, L, O, S, T, Z). */
    private final List<Brick> brickList;

    /** Queue containing the current and next bricks for the game. */
    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    /**
     * Constructs a new {@code RandomBrickGenerator}.
     * <p>
     * Initializes the list of available bricks and pre-loads
     * two random bricks into the queue for immediate use.
     * </p>
     */
    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
    }

    /**
     * Retrieves the current brick and replaces it with a newly generated one.
     * <p>
     * If the queue contains only one brick, a new random brick is added before
     * dequeuing to maintain a continuous supply.
     * </p>
     *
     * @return the current {@link Brick} to be used in gameplay
     */
    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
        return nextBricks.poll();
    }

    /**
     * Returns the next upcoming brick without removing it from the queue.
     *
     * @return the {@link Brick} that will appear after the current one
     */
    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }
}
