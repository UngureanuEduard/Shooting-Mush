package com.mygdx.game.combat_system;

import java.util.*;

public class AStarPathfinder {
    public static class Node {
        public int x, y;
        public Node parent;
        public Node(int x, int y, Node parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }
    }

    public static List<Node> findPath(int startX, int startY, int endX, int endY, boolean[][] walkable) {
        int height = walkable.length;
        int width = walkable[0].length;

        boolean[][] visited = new boolean[height][width];
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> heuristic(n, endX, endY)));
        open.add(new Node(startX, startY, null));

        while (!open.isEmpty()) {
            Node current = open.poll();

            if (current.x == endX && current.y == endY) {
                List<Node> path = new ArrayList<>();
                while (current != null) {
                    path.add(0, current);
                    current = current.parent;
                }
                return path;
            }

            if (visited[current.y][current.x]) continue;
            visited[current.y][current.x] = true;

            for (int[] d : new int[][]{{0,1},{1,0},{0,-1},{-1,0}}) {
                int nx = current.x + d[0];
                int ny = current.y + d[1];

                if (nx >= 0 && ny >= 0 && nx < width && ny < height && walkable[ny][nx] && !visited[ny][nx]) {
                    open.add(new Node(nx, ny, current));
                }
            }
        }

        return null;
    }

    private static int heuristic(Node node, int endX, int endY) {
        return Math.abs(node.x - endX) + Math.abs(node.y - endY);
    }
}
