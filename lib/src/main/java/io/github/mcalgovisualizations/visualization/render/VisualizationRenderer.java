package io.github.mcalgovisualizations.visualization.render;

import io.github.mcalgovisualizations.visualization.layouts.Layout;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.*;

public class VisualizationRenderer implements Renderer {
    private final Instance instance;
    private final Pos origin = new Pos(0, 43, 0); // TODO : config this somewhere else
    private Layout layout;

    // Currently spawned entities
    private final List<DisplayValue> values = new ArrayList<>();

    public VisualizationRenderer(Instance instance) {
        this.instance = instance;
    }

    public VisualizationRenderer(Instance instance, Layout layout) {
        this.instance = instance;
        this.layout = layout;
    }

    public Layout getLayout() {
        return this.layout;
    }

    @Override
    public void render(int[] values, Block block) {
        if(layout == null) { throw new NullPointerException("layout is null"); }

        clear(); // should probably move into controller as that governs time

        // parse Positions to Display Entities
        final var n = values.length;
        final var parsedEntitiesEntities = new ArrayList<DisplayValue>(n);
        final Pos[] computedValues = layout.compute(n, origin);

        Pos pos;
        for(int i = 0; i < n; i++) {
            pos = computedValues[i];
            parsedEntitiesEntities.add(new DisplayValue(pos, block, Integer.toString(values[i])));
        }

        // append entities to the list
        this.values.addAll(parsedEntitiesEntities);

        // display
        for (DisplayValue v : this.values) {
            v.setInstance(instance);
        }
    }

    public void addHighlight(int id) {
        values.get(id).setHighlighted(true);
    }

    public void addHighlights(int[] ids) {
        for(int id : ids) {
            values.get(id).setHighlighted(true);
        }
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    @Override
    public void cleanup() {
        for(DisplayValue v : values) {
            v.remove();
        }
        clear();
    }

    public void clear() {
        for(DisplayValue v : values) {
            v.setHighlighted(false);
            v.remove();
        }
        values.clear();
    }
}
