package visualization.sorting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.example.visualization.core.AbstractVisualization;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Visualization of the Insertion Sort algorithm using armor stands.
 * Each armor stand represents an element in the array, with height indicating value.
 * 
 * Color coding:
 * - GREEN: Sorted portion of the array
 * - RED: Currently being compared/moved
 * - WHITE: Unsorted portion
 */
public class InsertionSortVisualization extends AbstractVisualization {
    private int[] values;
    private final List<LivingEntity> displayEntities = new ArrayList<>();
    private final int arraySize;
    private final Random random = new Random();

    // Algorithm state
    private int currentIndex = 1;      // The element we're currently inserting
    private int compareIndex = -1;     // Current position during insertion
    private boolean algorithmComplete = false;

    // Visual spacing constants
    private static final double SPACING = 2.0;           // Horizontal space between elements
    private static final double BASE_HEIGHT = 1.0;       // Base Y offset (above ground)
    private static final double HEIGHT_MULTIPLIER = 0.5; // How much height per value unit

    public InsertionSortVisualization(Pos origin, InstanceContainer instance, int arraySize) {
        super("Insertion Sort", origin, instance);
        this.arraySize = arraySize;
        this.values = new int[arraySize];
        randomize();
    }

    @Override
    public void randomize() {
        stop();
        algorithmComplete = false;
        currentIndex = 1;
        compareIndex = -1;
        history.clear();
        historyIndex = -1;

        // Generate random values between 1 and 10
        for (int i = 0; i < arraySize; i++) {
            values[i] = random.nextInt(1, 11);
        }
        
        saveState(); // Save initial state
        renderState(values);
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
        if (algorithmComplete) {
            stop();
            return;
        }

        // Check if we've sorted the entire array
        if (currentIndex >= arraySize) {
            algorithmComplete = true;
            stop();
            renderState(values); // Final render with all green
            return;
        }

        if (compareIndex == -1) {
            // Starting a new insertion - pick up the current element
            compareIndex = currentIndex;
        }

        // Insertion sort logic: shift elements right until we find the right spot
        if (compareIndex > 0 && values[compareIndex - 1] > values[compareIndex]) {
            // Swap with the element to the left
            int temp = values[compareIndex];
            values[compareIndex] = values[compareIndex - 1];
            values[compareIndex - 1] = temp;
            compareIndex--;
            saveState();
        } else {
            // Found the right position, move to next element
            currentIndex++;
            compareIndex = -1;
        }

        renderState(values);
    }

    @Override
    protected void saveState() {
        // Remove any future states if we're not at the end of history
        while (history.size() > historyIndex + 1 && !history.isEmpty()) {
            history.remove(history.size() - 1);
        }
        history.add(values.clone());
        historyIndex = history.size() - 1;
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
            values = history.get(historyIndex).clone();
            
            // Reset algorithm state - we'd need to recalculate, 
            // but for visualization purposes just re-render
            algorithmComplete = false;
            renderState(values);
        }
    }

    @Override
    protected void renderState(int[] state) {
        // Remove old entities
        for (LivingEntity entity : displayEntities) {
            entity.remove();
        }
        displayEntities.clear();

        // Create new display entities (armor stands)
        for (int i = 0; i < state.length; i++) {
            double x = origin.x() + (i * SPACING);
            double y = origin.y() + BASE_HEIGHT;
            double z = origin.z();

            LivingEntity armorStand = new LivingEntity(EntityType.ARMOR_STAND);
            ArmorStandMeta meta = (ArmorStandMeta) armorStand.getEntityMeta();
            meta.setSmall(false);
            meta.setHasNoGravity(true);
            meta.setCustomNameVisible(true);
            meta.setInvisible(false);
            meta.setMarker(true); // Prevent collision/interaction issues

            // Determine color based on algorithm state
            NamedTextColor color;
            if (algorithmComplete) {
                color = NamedTextColor.GREEN; // All sorted
            } else if (i == compareIndex) {
                color = NamedTextColor.RED; // Currently being compared/moved
            } else if (i < currentIndex && compareIndex == -1) {
                color = NamedTextColor.GREEN; // Sorted portion
            } else if (i < compareIndex) {
                color = NamedTextColor.GREEN; // Sorted portion during comparison
            } else {
                color = NamedTextColor.WHITE; // Unsorted
            }

            armorStand.setCustomName(Component.text("[" + state[i] + "]", color));

            // Set helmet based on value (colored wool for visual height indicator)
            // Stack wool blocks based on value for height representation
            armorStand.setEquipment(EquipmentSlot.HELMET, getBlockForValue(state[i]));

            // Spawn at height based on value
            Pos spawnPos = new Pos(x, y + (state[i] * HEIGHT_MULTIPLIER), z);
            armorStand.setInstance(instance, spawnPos);
            displayEntities.add(armorStand);
        }
    }

    /**
     * Get a colored wool block based on the value.
     * Higher values get "warmer" colors.
     */
    private ItemStack getBlockForValue(int value) {
        return switch (value) {
            case 1 -> ItemStack.of(Material.WHITE_WOOL);
            case 2 -> ItemStack.of(Material.LIGHT_GRAY_WOOL);
            case 3 -> ItemStack.of(Material.YELLOW_WOOL);
            case 4 -> ItemStack.of(Material.ORANGE_WOOL);
            case 5 -> ItemStack.of(Material.PINK_WOOL);
            case 6 -> ItemStack.of(Material.MAGENTA_WOOL);
            case 7 -> ItemStack.of(Material.PURPLE_WOOL);
            case 8 -> ItemStack.of(Material.BLUE_WOOL);
            case 9 -> ItemStack.of(Material.CYAN_WOOL);
            case 10 -> ItemStack.of(Material.RED_WOOL);
            default -> ItemStack.of(Material.BLACK_WOOL);
        };
    }

    @Override
    public void cleanup() {
        stop();
        for (LivingEntity entity : displayEntities) {
            entity.remove();
        }
        displayEntities.clear();
    }
}
