package io.github.mcalgovisualizations.visualization.render;

import io.github.mcalgovisualizations.visualization.layouts.Layout;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisualizationRenderer implements Renderer {
    private List<DisplayValue> values = new ArrayList<>();
    private final List<Integer> highlighted = new ArrayList<>();
    private final Layout layout;

    public VisualizationRenderer(Layout layout) {
        this.layout = layout;
    }

    @Override
    public void render(Instance instance, DisplayValue[] values) {
        this.values = Arrays.asList(values);

        for (DisplayValue v : this.values) {
            v.setInstance(instance);
        }
    }


    @Override
    public void cleanup() {
        for(DisplayValue v : values) {
            v.remove();
        }
        clear();
    }

    public void clear() {
        for(int v : highlighted) {
            values.get(v).setHighlighted(false);
        }
        highlighted.clear();

        for(DisplayValue v : values) {
            v.remove();
        }
        values.clear();
    }
}
