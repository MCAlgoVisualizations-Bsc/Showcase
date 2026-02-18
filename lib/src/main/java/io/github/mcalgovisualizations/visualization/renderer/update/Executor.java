package io.github.mcalgovisualizations.visualization.renderer.update;

import io.github.mcalgovisualizations.visualization.renderer.update.dispatch.AnimationPlan;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * Runs AnimationPlans sequentially over time (one at a time).
 *
 * Enqueue many plans per Controller.step(). The executor will consume them
 * one-by-one on subsequent scheduler ticks.
 *
 * This class does NOT decide what to animate (dispatcher does).
 * It only decides WHEN to advance (time).
 */
public final class Executor {

    /** Where a frame's SceneOps should be applied. Usually: renderer::apply */
    private final SceneOps apply;

    private Task runningTask = null;

    private final Queue<AnimationPlan> queue = new LinkedList<>();

    // Current running plan state
    private AnimationPlan currentPlan = null;
    private int frameIndex = 0;
    private int ticksRemainingInFrame = 0;
    private boolean frameJustEntered = false;

    private boolean paused = false;

    /**
     * SPEED controls how often we tick the executor.
     * If you want "Minecraft ticks": 50ms per tick.
     * Using SPEED as a multiplier keeps your earlier convention (SPEED * 50ms).
     */
    private int SPEED = 1;

    public Executor(SceneOps apply) {
        this.apply = apply;
    }

    /** Enqueue work. Safe to call even while running. */
    public void add(AnimationPlan plan) {
        if (plan == null) return;
        queue.add(plan);
    }

    /**
     * Called after you enqueue plans (e.g., from Controller.step()).
     * Starts the scheduler if idle.
     */
    public void startIfIdle() {
        if (paused) return;
        if (runningTask != null) return; // already ticking

        runningTask = MinecraftServer.getSchedulerManager()
                .buildTask(this::tick)
                .repeat(Duration.ofMillis(SPEED * 50L))
                .schedule();
    }

    /** Stops ticking but keeps queued and current state (resume continues). */
    public void pause() {
        paused = true;
        if (runningTask != null) {
            runningTask.cancel();
            runningTask = null;
        }
    }

    /** Resumes ticking from the current state. */
    public void resume() {
        if (!paused) return;
        paused = false;
        startIfIdle();
    }

    /** True when there is no running plan and nothing queued. */
    public boolean isIdle() {
        return currentPlan == null && queue.isEmpty() && runningTask == null;
    }

    /** Optional: change speed at runtime (will restart ticking if running). */
    public void setSpeed(int speed) {
        if (speed <= 0) throw new IllegalArgumentException("speed must be > 0");
        this.SPEED = speed;

        // restart scheduler to apply new interval
        if (runningTask != null) {
            runningTask.cancel();
            runningTask = null;
            startIfIdle();
        }
    }

    /**
     * One scheduler tick.
     * - If no current plan: poll next.
     * - Advance current plan by one tick.
     * - When plan ends: move to next next tick.
     * - If nothing left: stop scheduler (become idle).
     */
    private void tick() {
        if (paused) return;

        // Ensure we have a current plan
        if (currentPlan == null) {
            currentPlan = queue.poll();
            frameIndex = 0;
            ticksRemainingInFrame = 0;
            frameJustEntered = true;

            if (currentPlan == null) {
                stopScheduler(); // nothing to do
                return;
            }
        }

        // If we've exhausted frames, finish plan
//        if (frameIndex >= currentPlan.frames().size()) {
//            finishCurrentPlan();
//            return;
//        }

        // var frame = currentPlan.frames().get(frameIndex);

        // Entering a new frame: initialize countdown and apply ops once
        if (frameJustEntered) {
            //ticksRemainingInFrame = Math.max(1, frame.durationTicks());
            frameJustEntered = false;

            // Apply ops for this frame (once at frame entry)
            SceneOps ops = new SceneOps(); // replace with your real SceneOps impl if needed
            //frame.ops().accept(ops);
            //apply.accept(ops);
        }

        // Consume one tick of this frame
        ticksRemainingInFrame--;

        // If this frame is done, advance to next frame on next tick
        if (ticksRemainingInFrame <= 0) {
            frameIndex++;
            frameJustEntered = true;
        }
    }

    private void finishCurrentPlan() {
        currentPlan = null;
        frameIndex = 0;
        ticksRemainingInFrame = 0;
        frameJustEntered = false;

        // If nothing queued, stop ticking
        if (queue.isEmpty()) {
            stopScheduler();
        }
    }

    private void stopScheduler() {
        if (runningTask != null) {
            runningTask.cancel();
            runningTask = null;
        }
    }

    /** Cancels ticking, drops current + queued work. */
    public void onCleanup() {
        paused = false;
        stopScheduler();
        currentPlan = null;
        frameIndex = 0;
        ticksRemainingInFrame = 0;
        frameJustEntered = false;
        queue.clear();
    }

    /**
     * Placeholder. Replace with your real SceneOps type.
     * The executor should not know Minestom; apply() should.
     */
    public static class SceneOps {
        // put your ops recording API here (or use your existing type)
    }
}
