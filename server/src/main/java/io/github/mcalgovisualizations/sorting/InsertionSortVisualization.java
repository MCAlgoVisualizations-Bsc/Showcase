package io.github.mcalgovisualizations.sorting;

import io.github.mcalgovisualizations.visualization.AbstractVisualization;
import io.github.mcalgovisualizations.visualization.DisplayValue;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Visualization of the Insertion Sort algorithm using armor stands.
 * Each armor stand represents an element in the array, with height indicating value.
 * <p>
 * Color coding:
 * - GREEN: Sorted portion of the array
 * - RED: Currently being compared/moved
 * - WHITE: Unsorted portion
 */
public class InsertionSortVisualization extends AbstractVisualization<Integer> {
    private final Random random = new Random();

    // Algorithm state
    private int currentIndex = 1;      // The element we're currently inserting
    private int compareIndex = -1;     // Current position during insertion
    private boolean algorithmComplete = false;

    public InsertionSortVisualization(Pos origin, InstanceContainer instance) {
        List<DisplayValue<Integer>> values = new ArrayList<>();
        // Generate random values between 1 and 10
        for (int i = 0; i < 10; i++) {
            Random r = new Random();
            var v = r.nextInt(1, 11);
            values.add(new DisplayValue<>(
                    instance,
                    v,
                    getBlockForValue(v)
            ));
        }
        super("Insertion Sort", values, origin, instance);
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

        // Logic check: are we actually moving?
        if (compareIndex > 0 && values.get(compareIndex - 1).compareTo(values.get(compareIndex)) > 0) {
            // Perform Swap
            swap(compareIndex, compareIndex - 1);

            instance.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_BANJO, Sound.Source.RECORD, 1.0f, 1.2f), origin);

            compareIndex--;
            // Only save state and increment history when a change actually happens
            saveState();
        } else {
            // Move to the next item to insert
            currentIndex++;
            compareIndex = -1;
            // Execute the next step immediately or wait for the next tick
        }
    }

    @Override
    public void stepForward() {
        if (!running && !algorithmComplete) {
            executeStep();
        }
    }


    @Override
    public void stepBack() {
        if (historyIndex > 0) {
            historyIndex--;
            values = new ArrayList<>(history.get(historyIndex));

            // Reset algorithm state - we'd need to recalculate,
            // but for visualization purposes just re-render
            algorithmComplete = false;
        }
    }
}