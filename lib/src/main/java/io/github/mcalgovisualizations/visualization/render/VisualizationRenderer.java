package io.github.mcalgovisualizations.visualization.render;

import io.github.mcalgovisualizations.visualization.layouts.Layout;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.*;

public class VisualizationRenderer implements Renderer {
    private final Instance instance;
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

    @Override
    public void render(Pos[] values, Block block) {
        if(layout == null) { throw new NullPointerException("layout is null"); }

        // parse Positions to Display Entities
        final var n = values.length;
        final var entities = new DisplayValue[n];

        Pos pos;

        for(int i = 0; i < n; i++) {
            pos = values[i];
            entities[i] = new DisplayValue(pos, block, Integer.toString(i));
        }

        List<DisplayValue> parsedEntities = Arrays.asList(entities);

        // append entities to the list
        this.values.addAll(parsedEntities);

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
