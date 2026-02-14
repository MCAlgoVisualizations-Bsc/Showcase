package io.github.mcalgovisualizations.visualization.engine;


public interface VisualizationLifeCycle {
    void onStart();
    void onStop();
    void onCleanup();
}
