package org.modogthedev.superposition.screens.utils;

import org.modogthedev.superposition.system.cards.Node;

public class Bounds {
    private int minX, minY, maxX, maxY;
    private Node node;

    public Bounds(int minX, int minY, int maxX, int maxY, Node node) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.node = node;
    }

    public Bounds(Node node) {
        float xSize = node.getSize().x;
        float ySize = node.getSize().y;
        float x = node.getPosition().x;
        float y = node.getPosition().y;
        this.minX = (int) (x-xSize/2);
        this.minY = (int) (y-ySize/2);
        this.maxX = (int) (x+xSize/2);
        this.maxY = (int) (y+ySize/2);
        this.node = node;
    }

    public boolean colliding(int x, int y) {
        return isColliding(minX, minY, maxX, maxY, node, x, y);
    }

    public static boolean isColliding(int minX, int minY, int maxX, int maxY, Node node, float x, float y) {
        float positionX = node.getPosition().x;
        float positionY = node.getPosition().y;
        return x >= minX+positionX && x <= maxX+positionX && y >= minY+positionY && y <= maxY+positionY;
    }

    public static boolean isColliding(Node node, float x, float y) {
        float xSize = node.getSize().x;
        float ySize = node.getSize().y;
        return isColliding((int) (-xSize/2), (int) (-ySize/2), (int) (xSize/2), (int) (ySize/2), node, x, y);
    }
}
