package io.github.mcalgovisualizations.visualization.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Adjacency-list where vertices are indexed 0..size-1.
 */
public final class AdjacencyList {
    private final int size;
    private final int[][] adj;

    /**
     * @param adj adjacency list (index = vertex, value = neighbors)
     */
    public AdjacencyList(int[][] adj) {
        if (adj == null) throw new IllegalArgumentException("adj cannot be null");
        this.size = adj.length;
        this.adj = Arrays.copyOf(adj, adj.length);
    }

    /**
     * @return number of vertices
     */
    public int size() {
        return size;
    }

    /**
     * @param v vertex id
     * @return copy of the neighbors for the given vertex
     */
    public int[] neighbors(int v) {
        return Arrays.copyOf(adj[v], adj[v].length);
    }

    /**
     * @return shallow copy of the adjacency list
     */
    public int[][] adj() {
        return Arrays.copyOf(adj, adj.length);
    }

    /**
     * Generates a connected undirected graph.
     *
     * @param n number of vertices
     * @param maxExtraEdges maximum additional random edges
     * @param r random generator
     * @return connected graph
     */
    public static AdjacencyList randomConnectedUndirected(int n, int maxExtraEdges, Random r) {
        @SuppressWarnings("unchecked")
        List<Integer>[] tmp = new List[n];
        for (int i = 0; i < n; i++) tmp[i] = new ArrayList<>();

        // Spanning tree for connectivity
        for (int v = 1; v < n; v++) {
            int u = r.nextInt(v);
            addUndirectedEdge(tmp, u, v);
        }

        // Extra random edges
        int extras = r.nextInt(maxExtraEdges + 1);
        for (int i = 0; i < extras; i++) {
            int a = r.nextInt(n);
            int b = r.nextInt(n);
            if (a == b) continue;
            if (tmp[a].contains(b)) continue;
            addUndirectedEdge(tmp, a, b);
        }

        int[][] out = new int[n][];
        for (int i = 0; i < n; i++) {
            out[i] = tmp[i].stream().mapToInt(Integer::intValue).toArray();
        }

        return new AdjacencyList(out);
    }

    /**
     * Adds an undirected edge between two vertices.
     *
     * @param tmp temporary adjacency structure
     * @param a first vertex
     * @param b second vertex
     */
    private static void addUndirectedEdge(List<Integer>[] tmp, int a, int b) {
        tmp[a].add(b);
        tmp[b].add(a);
    }
}
