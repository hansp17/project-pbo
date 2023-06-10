/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacxon;
import processing.core.PImage;
import processing.core.PApplet;
/**
 *
 * @author Reddy
 */
public abstract class GameObjects {
        /**
     * The Game Object's x-coordinate.
     */
    protected int x;
    
    /**
     * The Game Object's y-coordinate.
     */
    protected int y;

    /**
     * The Game Object's sprite.
     */
    private PImage sprite;

    /**
     * Creates a new Game Object object.
     * 
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public GameObjects(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the game object's sprite.
     * 
     * @param sprite The new sprite to use.
     */
    public void setSprite(PImage sprite) {
        this.sprite = sprite;
    }

    public GameObjects(PImage sprite) {
        this.sprite = sprite;
    }

    
    
    /** 
     * @param app
     */
    /** 
     * @param app
     */
    /**
     * Updates the game every frame.
     */
    public abstract void tick();

    /**
     * Draws the game objects to the screen.
     * 
     * @param app The window to draw onto.
     */
    public void draw(PApplet app) {
        // The image() method is used to draw PImages onto the screen.
        // The first argument is the image, the second and third arguments are coordinates
        app.image(this.sprite, this.x, this.y);
    }

    /**
     * Gets the x-coordinate.
     * @return The x-coordinate.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Returns the y-coordinate.
     * @return The y-coordinate.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Returns the sprite.
     */
    public PImage getSprite() {
        return this.sprite;
    }
}
