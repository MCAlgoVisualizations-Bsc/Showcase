package io.github.mcalgovisualizations.algorithms;

import io.github.mcalgovisualizations.visualization.renderers.AbstractVisualization;
import io.github.mcalgovisualizations.visualization.renderers.DisplayValue;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.sound.SoundEvent;

import io.github.mcalgovisualizations.visualization.layouts.FloatingLinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Visualization of the Insertion Sort algorithm using armor stands.
 * Each armor stand represents an element in the array.
 */
public class InsertionSortVisualization extends AbstractVisualization<Integer> {
    private final Random random = new Random();

    // Algorithm state
    private int currentIndex = 1;      // The element we're currently inserting
    private int compareIndex = -1;     // Current position during insertion

    public InsertionSortVisualization(UUID uuid, Pos origin, InstanceContainer instance) {
        List<DisplayValue<Integer>> values = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Random r = new Random();
            var v = r.nextInt(1, 11);
            values.add(new DisplayValue<>(
                    instance,
                    v,
                    getBlockForValue(v)
            ));
        }

        super("Insertion Sort", uuid, values, origin, instance);

        setLayout(new FloatingLinearLayout());

        randomize();
    }

    @Override
    public void start(Player player) {
        if (algorithmComplete) {
            player.sendMessage(Component.text("Algorithm complete! Use randomize to restart.", NamedTextColor.YELLOW));
            return;
        }
        super.start(player);
    }

    @Override
    protected void executeStep() {
        if (algorithmComplete || values.size() < 2) {
            stop();
            return;
        }

        for (DisplayValue<Integer> v : values) {
            v.setHighlighted(false);
        }

        if (currentIndex >= values.size()) {
            algorithmComplete = true;
            stop();
            return;
        }

        if (compareIndex == -1) {
            compareIndex = currentIndex;
        }

        if (compareIndex > 0) {
            values.get(compareIndex).setHighlighted(true);
            values.get(compareIndex - 1).setHighlighted(true);
        }

        if (compareIndex > 0 && values.get(compareIndex - 1).compareTo(values.get(compareIndex)) > 0) {
            swap(compareIndex, compareIndex - 1);

            // TODO: add sound back
            /*
            instance.playSound(
                    Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_BANJO, Sound.Source.RECORD, 1.0f, 1.2f),
                    origin
            );
            */

            compareIndex--;
            saveState();
        } else {
            currentIndex++;
            compareIndex = -1;
        }
    }

    @Override
    public void stepForward() {
        if (!running && !algorithmComplete) {
            // Use base stepForward so rendering happens via the configured layout.
            super.stepForward();
        }
    }
}
