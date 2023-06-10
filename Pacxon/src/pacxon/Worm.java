
package pacxon;

/**
 *
 * @author hanst
 */
public class Worm extends Enemy{
    int directionX, directionY;
    public Worm(int x, int y) {
        super(x, y);
        directionX=1;
        directionY=1;
    }

    @Override
    public void move(int[][] grid) {
        double tempX,tempY;
        tempX=x/20;
        tempY=(y-80)/20;
        int nextX = (int) Math.ceil(tempX) + directionX;
        int nextY = (int) Math.ceil(tempY) + directionY;

        if (nextX <= 0 || nextX >= 64 - 1 || grid[(int) Math.ceil(tempY)][nextX]>0) {
            directionX *= -1;
        }

        if (nextY <= 0 || nextY >= 32 - 1 || grid[nextY][(int) Math.ceil(tempX)]>0) {
            directionY *= -1;
        }

        x += directionX*2;
        y += directionY*2;
        
    }
   
    
}
