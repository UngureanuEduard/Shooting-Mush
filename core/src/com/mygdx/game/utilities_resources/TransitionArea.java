package com.mygdx.game.utilities_resources;

public class TransitionArea {
    private float x;
    private float y;
    private float width;
    private float height;

    public TransitionArea(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isWithinArea(float x, float y) {
        return x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height;
    }
}
