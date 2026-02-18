package io.github.mcalgovisualizations.visualization.renderer.update;

import io.github.mcalgovisualizations.visualization.renderer.update.dispatch.AnimationPlan;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;

import java.util.LinkedList;
import java.util.Queue;

public class Executor {

    private Task runningTask = null;
    private boolean IS_RUNNING = false;
    private final Queue<AnimationPlan> animationPlanQueue = new LinkedList<>();

    // Configs
    private int SPEED = 20;

    public Executor() {

    }

    void step() {
        if (IS_RUNNING) return;
        IS_RUNNING = true;

        runningTask = MinecraftServer.getSchedulerManager()
                .buildTask(this::tick)
                .schedule();
    }

    void tick() {
        var plan =  animationPlanQueue.poll();
        // execute the plan.

        this.IS_RUNNING = false;
    }

    void pause() {

    }

    void add(AnimationPlan animationPlan) {
        animationPlanQueue.add(animationPlan);
    }

    boolean isIdle() {
        return this.IS_RUNNING;
    }

    void onCleanup() {
        this.IS_RUNNING = false;
        if(runningTask != null) {
            this.runningTask.cancel();
            runningTask = null;
        }
        this.animationPlanQueue.clear();
    }
}