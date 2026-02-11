package io.github.mcalgovisualizations.algorithms;

import io.github.mcalgovisualizations.visualization.io.github.mcalgovisualizations.visualization.models.AdjacencyList;
import io.github.mcalgovisualizations.visualization.io.github.mcalgovisualizations.visualization.layouts.GraphLayout;
import io.github.mcalgovisualizations.visualization.io.github.mcalgovisualizations.visualization.layouts.MatrixLayout;
import io.github.mcalgovisualizations.visualization.renderers.AbstractVisualization;
import io.github.mcalgovisualizations.visualization.renderers.DisplayValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;

import java.util.*;

/**
 * Visualizes Breadth-First Search on a randomly generated graph.
 * Handles graph creation, layout, rendering, and step-based traversal.
 */

public class BFSVisualization extends AbstractVisualization<Integer> {

    private final Random r = new Random();

    // --- Graph config ---
    private static final int DEFAULT_VERTICES = 10;
    private static final int MAX_EXTRA_EDGES = 0;

    // --- Graph ---
    private AdjacencyList adjacencyList;

    // --- Incremental render state (vertex i then its edges) ---
    private int renderIndex = 0;
    private boolean[] vertexPlaced = new boolean[0];
    private final Set<Long> edgePlaced = new HashSet<>(); // undirected edge key (min,max)

    // --- Layout ---
    private Pos[] nodePos = new Pos[0];

    // --- Edge rendering (stretched chain BlockDisplays) ---
    private final List<Entity> edgeDisplays = new ArrayList<>();
    private static final double EDGE_THICKNESS = 0.20;
    private static final double EDGE_Y_OFFSET = -0.20;

    // --- BFS state ---
    private boolean[] visited = new boolean[0];
    private int[] parent = new int[0];

    private int[] queue = new int[0];
    private int head = 0, tail = 0;

    // "micro-step" state: process one neighbor per step
    private int current = -1;
    private int neighborIndex = 0;
    private boolean complete = false;

    // --- BFS history for stepBack() ---
    private final List<AlgoSnapshot> algoHistory = new ArrayList<>();
    private int algoHistoryIndex = -1;

    private int startVertexId = 0;

    public BFSVisualization(UUID uuid, Pos origin, InstanceContainer instance) {
        super("bfs", uuid, new ArrayList<>(), origin, instance);
        randomize();
    }

    @Override
    public void cleanup() {
        stop();
        clearEdges();
        super.cleanup();
    }

    @Override
    protected void executeStep() {
        if (complete) {
            stop();
            return;
        }

        saveAlgoState();
        bfsStep();
        renderGraphHighlightsOnly();

        if (complete) stop();
    }

    @Override
    public void randomize() {
        stop();

        clearEdges();
        for (var v : values) v.remove();
        values.clear();

        startVertexId = 0;

        // Build graph (moved into Graph)
        adjacencyList = AdjacencyList.randomConnectedUndirected(DEFAULT_VERTICES, MAX_EXTRA_EDGES, r);
        int n = adjacencyList.size();

        // One DisplayValue per vertex id (index = id)
        Collections.shuffle(values);

        /*
        double radius = Math.max(6.0, n * 1.2);
        GraphLayout layout = new CircleLayout(radius, 1.0); // yOffset = +1.0
         */

        GraphLayout layout = new MatrixLayout(2, 20, 20, 20);
        nodePos = layout.compute(adjacencyList, origin);

        // Reset incremental render tracking
        renderIndex = 0;
        vertexPlaced = new boolean[n];
        edgePlaced.clear();

        // Place all vertices + edges (deterministic; no animation here)
        while (renderIndex < n) renderNextVertexAndEdges();

        // Reset BFS state
        visited = new boolean[n];
        parent = new int[n];
        Arrays.fill(parent, -1);

        queue = new int[n];
        head = tail = 0;
        current = -1;
        neighborIndex = 0;
        complete = false;

        visited[startVertexId] = true;
        queue[tail++] = startVertexId;

        // Reset history
        algoHistory.clear();
        algoHistoryIndex = -1;

        renderGraphHighlightsOnly();
    }

    @Override
    public void stepForward() {
        if (!running && !complete) {
            executeStep();
        }
    }

    @Override
    public void stepBack(UUID uuid) {
        if (algoHistoryIndex <= 0) return;

        algoHistoryIndex--;
        restoreAlgoState(algoHistory.get(algoHistoryIndex));
        renderGraphHighlightsOnly();
    }

    // ---------------- Rendering ----------------

    /**
     * Places the next vertex (renderIndex) and all of its outgoing edges (once).
     * Call repeatedly until renderIndex == n.
     */
    private void renderNextVertexAndEdges() {
        if (adjacencyList == null) return;

        int n = adjacencyList.size();
        if (renderIndex >= n) return;

        int v = renderIndex;
        Pos pv = nodePos[v];
        if (pv == null) {
            renderIndex++;
            return;
        }

        // 1) Place vertex display (only once)
        if (!vertexPlaced[v]) {
            values.get(v).teleport(pv);
            vertexPlaced[v] = true;
        }

        // 2) Place all edges from v to its neighbors (only once per undirected pair)
        for (int u : adjacencyList.neighbors(v)) {
            if (u < 0 || u >= n) continue;
            Pos pu = nodePos[u];
            if (pu == null) continue;

            long key = undirectedEdgeKey(v, u);
            if (edgePlaced.add(key)) {
                spawnEdgeSegmented(nodePos[v], nodePos[u]);
            }
        }

        // 3) Advance
        renderIndex++;
    }

    /**
     * Returns a unique key for an undirected edge so (a,b) == (b,a).
     */
    private long undirectedEdgeKey(int a, int b) {
        int lo = Math.min(a, b);
        int hi = Math.max(a, b);
        return (((long) lo) << 32) | (hi & 0xFFFFFFFFL);
    }

    private void spawnEdgeBeam3D(Pos pa, Pos pb) {
        if (pa == null || pb == null) return;

        double dx = pb.x() - pa.x();
        double dy = pb.y() - pa.y();
        double dz = pb.z() - pa.z();

        System.out.print("position a - (" + pa.x() + ", " + pa.y() + ", " + pa.z() + ") - ");
        System.out.print("position b - (" + pb.x() + ", " + pb.y() + ", " + pb.z() + ") - ");

        double length = Math.sqrt(dx*dx + dy*dy + dz*dz);
        if (length < 1e-6) return;
        System.out.println(length);
        double mx = (pa.x() + pb.x()) * 0.5;
        double my = (pa.y() + pb.y()) * 0.5 + EDGE_Y_OFFSET;
        double mz = (pa.z() + pb.z()) * 0.5;

        float yawDeg = (float) Math.toDegrees(Math.atan2(-dx, dz));
        double horiz = Math.sqrt(dx*dx + dz*dz);
        float pitchDeg = (float) -Math.toDegrees(Math.atan2(dy, horiz));

        Entity edge = new Entity(EntityType.BLOCK_DISPLAY);
        BlockDisplayMeta meta = (BlockDisplayMeta) edge.getEntityMeta();
        meta.setBlockState(Block.BLACK_CONCRETE);          // <- full cube
        meta.setHasNoGravity(true);

        meta.setTranslation(new Vec(-0.5, 0, -0.5));    // <- center the block on the entity
        meta.setScale(new Vec(length, EDGE_THICKNESS, EDGE_THICKNESS));

        // edge.setInstance(instance);
        edge.teleport(new Pos(mx, my, mz, yawDeg, pitchDeg));

        edgeDisplays.add(edge);
    }

    /**
     * Spawns a segmented beam between two nodes using BLOCK_DISPLAY entities.
     *
     * Uses world coordinates (x/y/z) to keep the beam centered.
     * The +90° yaw compensates for the BlockDisplay stretch axis.
     */

    private void spawnEdgeSegmented(Pos pa, Pos pb) {
        if (pa == null || pb == null) return;

        // If your node positions are block-aligned (integers) and you want center-to-center beams,
        // uncomment the +0.5 lines.
        final double ax = pa.x(); // + 0.5;
        final double ay = pa.y(); // + 0.5;
        final double az = pa.z(); // + 0.5;

        final double bx = pb.x(); // + 0.5;
        final double by = pb.y(); // + 0.5;
        final double bz = pb.z(); // + 0.5;

        final double dx = bx - ax;
        final double dy = by - ay;
        final double dz = bz - az;

        final double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 1e-6) return;

        // Orientation for all segments
        final float yawDeg = (float) Math.toDegrees(Math.atan2(-dx, dz)) + 90f; //
        final double horiz = Math.sqrt(dx * dx + dz * dz);
        final float pitchDeg = (float) -Math.toDegrees(Math.atan2(dy, horiz));

        // Unit direction (A -> B)
        final double ux = dx / length;
        final double uy = dy / length;
        final double uz = dz / length;

        // --- Segmentation settings ---
        // Each segment will be at most this long (in blocks). Tune to taste.
        final double maxSegmentLen = 1.0;

        // Optional small gap between segments (visual separation)
        final double gap = 0.05;

        // Compute number of segments and actual segment length (accounting for gap)
        final int segments = Math.max(1, (int) Math.ceil(length / maxSegmentLen));
        final double rawSegLen = length / segments;
        final double segLen = Math.max(0.001, rawSegLen - gap);

        // Place each segment centered along the beam
        for (int i = 0; i < segments; i++) {
            // Center distance from A for this segment
            final double centerDist = (i + 0.5) * rawSegLen;

            final double mx = ax + ux * centerDist;
            final double my = ay + uy * centerDist + EDGE_Y_OFFSET;
            final double mz = az + uz * centerDist;

            Entity seg = new Entity(EntityType.BLOCK_DISPLAY);
            BlockDisplayMeta meta = (BlockDisplayMeta) seg.getEntityMeta();
            meta.setBlockState(Block.BLACK_CONCRETE);
            meta.setHasNoGravity(true);

            // Center the cube on the entity position, then stretch it along local +X
            meta.setTranslation(new Vec(-0.5, -0.5, -0.5));
            meta.setScale(new Vec(segLen, EDGE_THICKNESS, EDGE_THICKNESS));

            // seg.setInstance(instance);
            seg.teleport(new Pos(mx, my, mz, yawDeg, pitchDeg));

            edgeDisplays.add(seg);
        }
    }


    /**
     * Updates vertex highlights without moving entities or rebuilding edges.
     */
    private void renderGraphHighlightsOnly() {
        if (adjacencyList == null) return;

        int n = adjacencyList.size();
        int limit = Math.min(n, values.size());
        for (int id = 0; id < limit; id++) {
            boolean inFrontier = isInQueue(id);
            boolean isCurrent = (id == current);
            values.get(id).setHighlighted(isCurrent || inFrontier);
        }
    }

    private boolean isInQueue(int id) {
        for (int i = head; i < tail; i++) {
            if (queue[i] == id) return true;
        }
        return false;
    }

    private void clearEdges() {
        for (Entity e : edgeDisplays) e.remove();
        edgeDisplays.clear();
    }

    // ---------------- BFS mechanics ----------------

    /**
     * Executes one BFS micro-step by processing a single neighbor.
     * Allows smoother visualization and precise step-back behavior.
     */
    private void bfsStep() {
        if (complete || adjacencyList == null) return;

        if (current == -1) {
            if (head >= tail) {
                complete = true;
                return;
            }
            current = queue[head++];
            neighborIndex = 0;
            return;
        }

        int[] ns = adjacencyList.neighbors(current);
        while (neighborIndex < ns.length) {
            int u = ns[neighborIndex++];
            if (!visited[u]) {
                visited[u] = true;
                parent[u] = current;
                queue[tail++] = u;
                return;
            }
        }

        current = -1;
    }

    // ---------------- History ----------------

    private void saveAlgoState() {
        while (algoHistory.size() > algoHistoryIndex + 1) {
            algoHistory.remove(algoHistory.size() - 1);
        }

        algoHistory.add(new AlgoSnapshot(
                Arrays.copyOf(visited, visited.length),
                Arrays.copyOf(parent, parent.length),
                Arrays.copyOf(queue, queue.length),
                head, tail,
                current, neighborIndex,
                complete
        ));
        algoHistoryIndex = algoHistory.size() - 1;
    }

    private void restoreAlgoState(AlgoSnapshot s) {
        this.visited = Arrays.copyOf(s.visited, s.visited.length);
        this.parent = Arrays.copyOf(s.parent, s.parent.length);
        this.queue = Arrays.copyOf(s.queue, s.queue.length);
        this.head = s.head;
        this.tail = s.tail;
        this.current = s.current;
        this.neighborIndex = s.neighborIndex;
        this.complete = s.complete;
    }

    private record AlgoSnapshot(
            boolean[] visited,
            int[] parent,
            int[] queue,
            int head,
            int tail,
            int current,
            int neighborIndex,
            boolean complete
    ) {}

    private Block blockForId(int id) {
        return switch (id % 10) {
            case 0 -> Block.WHITE_WOOL;
            case 1 -> Block.LIGHT_GRAY_WOOL;
            case 2 -> Block.YELLOW_WOOL;
            case 3 -> Block.ORANGE_WOOL;
            case 4 -> Block.PINK_WOOL;
            case 5 -> Block.MAGENTA_WOOL;
            case 6 -> Block.PURPLE_WOOL;
            case 7 -> Block.BLUE_WOOL;
            case 8 -> Block.CYAN_WOOL;
            default -> Block.RED_WOOL;
        };
    }
}
