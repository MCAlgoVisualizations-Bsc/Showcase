package io.github.mcalgovisualizations.visualization.models;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class IntListTest {

    @Test
    void constructorRejectsNull() {
        var ex = assertThrows(IllegalArgumentException.class, () -> new IntList(null));
        assertEquals("data cannot be null", ex.getMessage());
    }

    @Test
    void sizeAndLengthMatchBackingArray() {
        int[] a = {1, 2, 3};
        IntList list = new IntList(a);

        assertEquals(3, list.size());
        assertEquals(3, list.length());
    }

    @Test
    void getAndSetWork() {
        int[] a = {1, 2, 3};
        IntList list = new IntList(a);

        assertEquals(2, list.get(1));
        list.set(1, 99);
        assertEquals(99, list.get(1));
        assertArrayEquals(new int[]{1, 99, 3}, a);
    }

    @Test
    void swapSwapsValues() {
        int[] a = {1, 2, 3};
        IntList list = new IntList(a);

        list.swap(0, 2);
        assertArrayEquals(new int[]{3, 2, 1}, a);
    }

    @Test
    void swapSameIndexNoOp() {
        int[] a = {1, 2, 3};
        IntList list = new IntList(a);

        list.swap(1, 1);
        assertArrayEquals(new int[]{1, 2, 3}, a);
    }

    @Test
    void boundsChecks() {
        IntList list = new IntList(new int[]{1, 2, 3});

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> list.get(3));

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> list.set(-1, 0));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> list.set(3, 0));

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> list.swap(-1, 0));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> list.swap(0, 3));
    }

    @Test
    void toArrayCurrentlyReturnsBackingArrayReference() {
        int[] a = {1, 2, 3};
        IntList list = new IntList(a);

        // Current implementation returns the same reference (NOT a defensive copy)
        assertSame(a, list.toArray());

        // Demonstrate mutability leak:
        int[] exposed = list.toArray();
        exposed[0] = 42;
        assertEquals(42, list.get(0));
    }

    @Test
    void randomIsDeterministicForSameSeedAndInRange() {
        IntList a = IntList.random(10, -2, 2, new Random(12345L));
        IntList b = IntList.random(10, -2, 2, new Random(12345L));

        assertArrayEquals(a.toArray(), b.toArray());

        for (int v : a.toArray()) {
            assertTrue(v >= -2 && v <= 2);
        }
    }


    @Test
    void randomRejectsInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> IntList.random(-1, 0, 0, new Random()));
        assertThrows(IllegalArgumentException.class, () -> IntList.random(1, 5, 4, new Random()));
    }
}
