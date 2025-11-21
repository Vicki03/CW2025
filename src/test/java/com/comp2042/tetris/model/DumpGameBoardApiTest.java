// java
package com.comp2042.tetris.model;

import org.junit.jupiter.api.Test;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

class DumpGameBoardApiTest {

    private static Object makeGameBoard() throws Exception {
        Class<?> cls = Class.forName("com.comp2042.tetris.model.GameBoard");
        try {
            Constructor<?> ctor = cls.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (NoSuchMethodException ignored) {
            ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
            Constructor<Object> objCtor = Object.class.getDeclaredConstructor();
            @SuppressWarnings("unchecked")
            Constructor<?> gcCtor = (Constructor<?>) rf.newConstructorForSerialization(cls, objCtor);
            gcCtor.setAccessible(true);
            return gcCtor.newInstance();
        }
    }

    @Test
    void dumpGameBoardApi() throws Exception {
        Object board = makeGameBoard();
        Class<?> cls = board.getClass();
        System.err.println("=== GameBoard class: " + cls.getName() + " ===");

        System.err.println("\n-- DECLARED METHODS --");
        for (Method m : cls.getDeclaredMethods()) {
            System.err.printf("%s %s(%d params) -> %s%n",
                    Modifier.toString(m.getModifiers()), m.getName(), m.getParameterCount(), m.getReturnType().getSimpleName());
        }

        System.err.println("\n-- PUBLIC METHODS (incl inherited) --");
        for (Method m : cls.getMethods()) {
            System.err.printf("%s %s(%d params) -> %s%n",
                    Modifier.toString(m.getModifiers()), m.getName(), m.getParameterCount(), m.getReturnType().getSimpleName());
        }

        System.err.println("\n-- FIELDS --");
        for (Field f : cls.getDeclaredFields()) {
            f.setAccessible(true);
            Object val = null;
            try { val = f.get(board); } catch (Throwable ignored) {}
            System.err.printf("%s %s : %s = %s%n",
                    Modifier.toString(f.getModifiers()), f.getName(), f.getType().getSimpleName(),
                    val == null ? "null" : val.getClass().getSimpleName());
        }

        System.err.println("\n-- TRY INVOKING ZERO-ARG METHODS (safe attempts) --");
        for (Method m : cls.getDeclaredMethods()) {
            if (m.getParameterCount() != 0) continue;
            m.setAccessible(true);
            Object res = null;
            try {
                res = m.invoke(board);
            } catch (Throwable t) {
                System.err.printf("invoke %s() -> threw: %s%n", m.getName(), t.getClass().getSimpleName());
                continue;
            }
            if (res == null) {
                System.err.printf("invoke %s() -> null%n", m.getName());
            } else {
                Class<?> rt = res.getClass();
                if (res instanceof Collection) {
                    System.err.printf("invoke %s() -> Collection(%d) of %s%n", m.getName(), ((Collection<?>) res).size(),
                            rt.getSimpleName());
                } else if (rt.isArray()) {
                    System.err.printf("invoke %s() -> Array length=%d of %s%n", m.getName(), Array.getLength(res), rt.getComponentType().getSimpleName());
                } else {
                    System.err.printf("invoke %s() -> %s%n", m.getName(), rt.getName());
                }
            }
        }

        System.err.println("\n-- TIP --");
        System.err.println("Look for methods/fields that returned a Collection/Array or non-null wrapper");
        System.err.println("Common names to search for in output: preview, next, upcoming, queue, peek, getNext, getPreview, init, start, newGame");
    }
}
