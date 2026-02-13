package io.github.mcalgovisualizations.visualization.layouts;

import io.github.mcalgovisualizations.visualization.renderer.LayoutEntry;
import net.minestom.server.coordinate.Pos;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FloatingLinearLayoutTest {

    // --- Constructor validation ---

    @Test
    void constructor_throwsWhenSpacingIsNonPositive() {
        var ex0 = assertThrows(IllegalArgumentException.class,
                () -> new FloatingLinearLayout(0.0, 0.0, 0.0));
        assertEquals("spacing must be > 0", ex0.getMessage());

        var exNeg = assertThrows(IllegalArgumentException.class,
                () -> new FloatingLinearLayout(-1.0, 0.0, 0.0));
        assertEquals("spacing must be > 0", exNeg.getMessage());

        assertDoesNotThrow(() -> new FloatingLinearLayout(0.0001, 0.0, 0.0));
    }

    @Test
    void defaultConstructor_hasExpectedDefaults() {
        var layout = new FloatingLinearLayout();
        assertEquals(1.0, layout.spacing());
        assertEquals(0.0, layout.yOffset());
        assertEquals(0.0, layout.zOffset());
    }

    // --- compute() behaviour ---

    @Test
    void compute_returnsEmpty_whenModelIsNullOrEmpty() {
        var layout = new FloatingLinearLayout();

        assertEquals(0, layout.compute(null, Pos.ZERO).length);
        assertEquals(0, layout.compute(new int[0], Pos.ZERO).length);
    }

    @Test
    void compute_preservesValues_andSetsXByIndexTimesSpacing_andAppliesOffsets() {
        var layout = new FloatingLinearLayout(2.5, 3.0, -4.0);
        var model = new int[] { 9, 1, 4, 7 };
        var origin = new Pos(10.0, 20.0, 30.0);

        LayoutEntry[] out = layout.compute(model, origin);

        assertEquals(model.length, out.length);

        double expectedY = origin.y() + layout.yOffset();
        double expectedZ = origin.z() + layout.zOffset();

        for (int i = 0; i < model.length; i++) {
            assertEquals(model[i], out[i].value(), "value should be preserved");

            double expectedX = origin.x() + (i * layout.spacing());
            assertEquals(expectedX, out[i].pos().x(), "x should advance by i*spacing");
            assertEquals(expectedY, out[i].pos().y(), "y should be origin.y + yOffset");
            assertEquals(expectedZ, out[i].pos().z(), "z should be origin.z + zOffset");
        }
    }

    @Test
    void compute_doesNotMutateInputModel() {
        var layout = new FloatingLinearLayout(1.0, 0.0, 0.0);
        var model = new int[] { 5, 6, 7 };
        var copy = model.clone();

        layout.compute(model, Pos.ZERO);

        assertArrayEquals(copy, model, "compute() must not mutate the input array");
    }

    // --- random() behaviour ---

    @Test
    void random_returnsSameMultisetOfEntriesAsCompute_sameLength_sameValues_samePositions() {
        var layout = new FloatingLinearLayout(2.0, 1.0, 0.5);
        var model = new int[] { 9, 1, 4, 7, 7, 2 };
        var origin = new Pos(3.0, 4.0, 5.0);

        LayoutEntry[] expected = layout.compute(model, origin);
        LayoutEntry[] randomized = layout.random(model, origin);

        assertEquals(expected.length, randomized.length);

        // Compare as multisets: (pos,value) counts match, order can differ.
        Map<String, Integer> expectedCounts = multisetKeyCounts(expected);
        Map<String, Integer> randomCounts = multisetKeyCounts(randomized);

        assertEquals(expectedCounts, randomCounts,
                "random() must contain exactly the same entries as compute(), just shuffled");
    }

    @Test
    void random_returnsEmpty_whenModelIsNullOrEmpty() {
        var layout = new FloatingLinearLayout();

        assertEquals(0, layout.random(null, Pos.ZERO).length);
        assertEquals(0, layout.random(new int[0], Pos.ZERO).length);
    }

    // --- helpers ---

    private static Map<String, Integer> multisetKeyCounts(LayoutEntry[] entries) {
        Map<String, Integer> counts = new HashMap<>();
        for (LayoutEntry e : entries) {
            // Keyed by exact x/y/z/value from this layout implementation.
            // Using String key is fine for tests; avoids needing equals/hashCode on LayoutEntry/Pos.
            String key = e.value() + "@" + e.pos().x() + "," + e.pos().y() + "," + e.pos().z();
            counts.put(key, counts.getOrDefault(key, 0) + 1);
        }
        return counts;
    }
}
