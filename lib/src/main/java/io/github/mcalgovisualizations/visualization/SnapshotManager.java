package io.github.mcalgovisualizations.visualization;

import io.github.mcalgovisualizations.visualization.engine.VisualizationController;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.util.*;

public class SnapshotManager {
    private static @Nullable SnapshotManager single_instance = null;

    // Maps a Player/Session UUID to a List of historical states
    private final Map<UUID, List<byte[]>> history = new HashMap<>();
    // Current active visualizations
    private final Map<UUID, VisualizationController> activeVis = new HashMap<>();

    private SnapshotManager() {}

    public static synchronized SnapshotManager getInstance()
    {
        if (single_instance == null)
            single_instance = new SnapshotManager();

        return single_instance;
    }

    public void assignVisualization(UUID uuid, VisualizationController vis) {
        this.activeVis.put(uuid, vis);
        this.history.put(uuid, new ArrayList<>());
    }

    /**
     * Captures the current state of the visualization and adds it to the history.
     */
    public void saveSnapshot(UUID uuid) {
        VisualizationController current = activeVis.get(uuid);
        if (current == null) return;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            oos.writeObject(current);
            history.get(uuid).add(baos.toByteArray());
            System.out.println("Saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reverts the visualization to a specific point in time.
     */
    public VisualizationController loadSnapshot(UUID uuid, int index) {
        List<byte[]> snapshots = history.get(uuid);
        if (snapshots == null || index >= snapshots.size() || index < 1) return null;

        byte[] data = snapshots.get(index);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {

            VisualizationController restored = (VisualizationController) ois.readObject();
            // Update the active map with the restored version
            activeVis.put(uuid, restored);
            return restored;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public VisualizationController loadLatestSnapshot(UUID uuid) {
        var lastElem = history.get(uuid).size() -1;
        return loadSnapshot(uuid, lastElem);
    }


    public void remove(UUID uuid) {
        activeVis.remove(uuid);
        history.remove(uuid);
    }
}