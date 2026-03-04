package io.github.mcalgovisualizations.visualization.ui;

import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.PlayerInventory;

import java.util.Set;

public interface IAlgorithmUI {
    Inventory openSelector(Set<String> algorithms);
    void applyRunningLayout(Player player);
}
