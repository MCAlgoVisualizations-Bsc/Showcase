package io.github.mcalgovisualizations.visualization.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * An adjacency-list representation of a graph where vertices are indexed
 * from {@code 0} to {@code size - 1}.
 * <p>
 * The graph is backed by a 2D {@code int[][]} array, where each index
 * represents a vertex and the corresponding array contains its neighbors.
 * <p>
 * This implementation does not enforce immutability of the inner arrays.
 * Defensive copies are returned where appropriate.
 */
public final class Graph implements DataModel {

    private final int[][] adj;

    /**
     * Creates a graph from the given adjacency list.
     *
     * @param adj adjacency list (index = vertex, value = neighbors)
     * @throws IllegalArgumentException if {@code adj} is {@code null}
     */
    public Graph(int[][] adj){
        if (adj == null)
            throw new IllegalArgumentException("adj cannot be null");

        this.adj = Arrays.copyOf(adj, adj.length); // shallow copy of outer array
    }

    /**
     * Returns the number of vertices in the graph.
     *
     * @return total number of vertices
     */
    @Override
    public int size() {
        throw new  UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns a copy of the neighbors of a given vertex.
     *
     * @param v vertex id (0 ≤ v < size)
     * @return a defensive copy of the neighbor list
     * @throws ArrayIndexOutOfBoundsException if {@code v} is invalid
     */
    public int[] neighbors(int v) {
        return Arrays.copyOf(adj[v], adj[v].length);
    }

    /**
     * Returns a shallow copy of the adjacency list.
     * <p>
     * The outer array is copied, but the inner arrays are shared.
     *
     * @return a shallow copy of the adjacency structure
     */
    public int[][] adj() {
        return Arrays.copyOf(adj, adj.length);
    }

    /**
     * Generates a connected undirected graph.
     * <p>
     * Connectivity is guaranteed by first constructing a spanning tree,
     * then optionally adding additional random edges.
     *
     * @param n              number of vertices
     * @param maxExtraEdges  maximum number of additional random edges
     * @param r              source of randomness
     * @return a connected undirected graph
     *
     * @throws IllegalArgumentException if {@code n <= 0}
     */
    public static Graph randomConnectedUndirected(int n, int maxExtraEdges, Random r) {
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

        return new Graph(out);
    }

    /**
     * Adds an undirected edge between two vertices.
     * <p>
     * The edge is added symmetrically to both adjacency lists.
     *
     * @param tmp temporary adjacency structure
     * @param a   first vertex
     * @param b   second vertex
     */
    private static void addUndirectedEdge(List<Integer>[] tmp, int a, int b) {
        tmp[a].add(b);
        tmp[b].add(a);
    }
}
