package io.github.mcalgovisualizations.visualization.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.recipe.display.SlotDisplay;
import net.minestom.server.tag.Tag;

import java.util.Set;

import static io.github.mcalgovisualizations.visualization.Tags.*;

public class AlgorithmUI implements IAlgorithmUI {
    @Override
    public Inventory openSelector(Set<String> algorithms) {
        if (algorithms.size() > 26) throw new RuntimeException("To many algorithms for ui, the ui can only handle 26 algorithms");
        Inventory inventory = new Inventory(InventoryType.CHEST_3_ROW, Component.text("Select Algorithm", NamedTextColor.DARK_PURPLE));


        int i = 0;
        for (String algorithm : algorithms) {
            ItemStack item = ItemStack.builder(Material.STICK)
                    .customName(Component.text(algorithm, NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                    .set(ALGO_ID_TAG, algorithm)
                    .build();
            inventory.setItemStack(i, item);
        }

        ItemStack clearItem = ItemStack.builder(Material.BARRIER)
                .customName(Component.text("Clear Visualization", NamedTextColor.RED)
                        .decoration(TextDecoration.ITALIC, false))
                .lore(
                        Component.text("Remove current visualization", NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("and clear your hotbar.", NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false)
                ).set(ALGO_CLEAR_TAG, "clear")
                .build();
        inventory.setItemStack(26, clearItem);

        return inventory;
    }

    @Override
    public void applyRunningLayout(Player player) {
        PlayerInventory inv = player.getInventory();
        inv.clear();

        inv.setItemStack(0,
                ItemStack.builder(Material.ENDER_PEARL)
                        .customName(Component.text("Randomize"))
                        .set(ALGO_RANDOMIZE_TAG, "randomize")
                        .build()
        );
        inv.setItemStack(1,
                ItemStack.builder(Material.GREEN_DYE)
                .customName(Component.text("Start"))
                .set(ALGO_START_TAG, "start")
                .build()
        );
        inv.setItemStack(2,
                ItemStack.builder(Material.RED_DYE)
                        .customName(Component.text("Stop"))
                        .set(ALGO_STOP_TAG, "stop")
                        .build()
        );
        inv.setItemStack(3,
                ItemStack.builder(Material.ARROW)
                        .customName(Component.text("Step Forward"))
                        .set(ALGO_STEP_TAG, "forward")
                        .build()
        );
        inv.setItemStack(4,
                ItemStack.builder(Material.SPECTRAL_ARROW)
                        .customName(Component.text("Step Back"))
                        .set(ALGO_STEP_TAG, "back")
                        .build()
        );
    }
}
