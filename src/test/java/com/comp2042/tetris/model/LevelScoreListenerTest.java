package com.comp2042.tetris.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import org.junit.jupiter.api.Test;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class LevelScoreListenerTest {

    // create Score via ctor or serialization fallback
    private static Score makeScore() throws Exception {
        try {
            Constructor<Score> ctor = Score.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (NoSuchMethodException ignored) {
            ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
            Constructor<Object> objCtor = Object.class.getDeclaredConstructor();
            @SuppressWarnings("unchecked")
            Constructor<Score> scCtor = (Constructor<Score>) rf.newConstructorForSerialization(Score.class, objCtor);
            Score s = scCtor.newInstance();
            // ensure a backing IntegerProperty exists if implementation expects it
            for (Field f : Score.class.getDeclaredFields()) {
                if (IntegerProperty.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    f.set(s, new javafx.beans.property.SimpleIntegerProperty(0));
                    break;
                }
            }
            return s;
        }
    }

    // create LevelService via ctor or serialization fallback
    private static LevelService makeLevelService() throws Exception {
        try {
            Constructor<LevelService> ctor = LevelService.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (NoSuchMethodException ignored) {
            ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
            Constructor<Object> objCtor = Object.class.getDeclaredConstructor();
            @SuppressWarnings("unchecked")
            Constructor<LevelService> lsCtor = (Constructor<LevelService>) rf.newConstructorForSerialization(LevelService.class, objCtor);
            return lsCtor.newInstance();
        }
    }

    @Test
    void scoreProperty_firesListener_onChange() throws Exception {
        Score s = makeScore();

        IntegerProperty prop = s.scoreProperty();
        assertNotNull(prop, "scoreProperty must not be null");

        AtomicBoolean called = new AtomicBoolean(false);
        AtomicInteger newValue = new AtomicInteger(Integer.MIN_VALUE);

        ChangeListener<Number> listener = (obs, oldV, newV) -> {
            called.set(true);
            newValue.set(newV.intValue());
        };
        prop.addListener(listener);

        // update via property to ensure listener is invoked
        prop.set(12345);

        assertTrue(called.get(), "ChangeListener should have been invoked when score changed");
        assertEquals(12345, newValue.get(), "Listener should receive the new score value");
    }

    @Test
    void levelService_gravity_decreases_with_higher_score() throws Exception {
        LevelService ls = makeLevelService();

        // sanity: method exists
        int msLow = ls.gravityMsForScore(0);
        int msMid = ls.gravityMsForScore(1000);
        int msHigh = ls.gravityMsForScore(100000);

        // Expect gravity interval to be non-increasing as score increases (higher score -> equal or faster drops)
        assertTrue(msLow >= msMid, "gravityMsForScore should not increase for higher score (0 -> 1000)");
        assertTrue(msMid >= msHigh, "gravityMsForScore should not increase for higher score (1000 -> 100000)");

        // values should be reasonable positive ints
        assertTrue(msLow > 0, "gravity ms should be positive");
        assertTrue(msMid > 0, "gravity ms should be positive");
        assertTrue(msHigh > 0, "gravity ms should be positive");
    }
}
