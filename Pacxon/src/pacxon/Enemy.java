/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pacxon;

import processing.core.PApplet;
import processing.core.PImage;

/**
 *
 * @author hanst
 */
public abstract class Enemy {
    protected int x;
    protected int y;
    PImage sprite;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public PImage getSprite() {
        return sprite;
    }

    public void setSprite(PImage sprite) {
        this.sprite = sprite;
    }
    public void draw(PApplet app) {
        // The image() method is used to draw PImages onto the screen.
        // The first argument is the image, the second and third arguments are coordinates
        app.image(this.sprite, this.x, this.y);
    }
    public abstract void move(int[][] grid);
}
