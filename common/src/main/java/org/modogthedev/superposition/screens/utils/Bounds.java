package org.modogthedev.superposition.screens.utils;

import org.joml.Vector2f;
import org.modogthedev.superposition.system.cards.Node;

public class Bounds {
    private int minX, minY, maxX, maxY;
    private Node node;

    public Bounds() {
        this.minX = 0;
        this.minY = 0;
        this.maxX = 0;
        this.maxY = 0;
    }

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

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public boolean nodeColliding(int x, int y) {
        return isColliding(minX, minY, maxX, maxY, node, x, y);
    }

    public boolean isColliding( float x, float y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    public static boolean isColliding(int minX, int minY, int maxX, int maxY, Node node, float x, float y) {
        float posX = node.getPosition().x;
        float posY = node.getPosition().y;
        return x >= minX+posX && x <= maxX+posX && y >= minY+posY && y <= maxY+posY;
    }

    public static boolean isColliding(int minX, int minY, int maxX, int maxY, float posX, float posY, float x, float y) {
        return x >= minX+posX && x <= maxX+posX && y >= minY+posY && y <= maxY+posY;
    }

    public static boolean isColliding(int minX, int minY, int maxX, int maxY, float x, float y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    public static boolean isColliding(Vector2f scale, float posX, float posY, float x, float y) {
        float xSize = scale.x;
        float ySize = scale.y;
        return isColliding((int) (-xSize/2), (int) (-ySize/2), (int) (xSize/2), (int) (ySize/2), posX, posY, x, y);
    }

    public static boolean isColliding(Node node, float x, float y) {
        float xSize = node.getSize().x;
        float ySize = node.getSize().y;
        return isColliding((int) (-xSize/2), (int) (-ySize/2), (int) (xSize/2), (int) (ySize/2), node, x, y);
    }
}
