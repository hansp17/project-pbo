package pacxon;

import java.io.*;
import java.util.*;
import processing.core.*;
import processing.data.JSONObject;
import processing.data.JSONArray;
/**
 *
 * @author hanst
 */
public class Pacxon extends PApplet {
    private int move = 0;
    private int gridSizeX = 64;
    private int gridSizeY = 32;
    private int tileWidth = 20;
    private int tileHeight = 20;
    private int windowWidth = gridSizeX * tileWidth;
    private int windowHeight = gridSizeY * tileHeight + 80;
    private int pacxonX, pacxonY;
    private int enemyX, enemyY;
    private int enemyDirectionX, enemyDirectionY;
    private boolean[][] grid;
    private boolean[][] filled;
    private int[][] filledYet;
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private float characterSpeed = 1.0f;
    private float enemySpeed = 0.5f;
    private ArrayList<Concrete> concretes;

    public void settings() {
        size(windowWidth, windowHeight);
    }

    public void setup() {
        frameRate(10);
        grid = new boolean[gridSizeX][gridSizeY];
        filled = new boolean[gridSizeX][gridSizeY];
        filledYet = new int[gridSizeX][gridSizeY];
        for (int i = 0; i < gridSizeX; i++) {
            for (int j = 0; j < gridSizeY; j++) {
                if (i == 0 || j == 0 || i == gridSizeX - 1 || j == gridSizeY - 1) {
                    filled[i][j] = true;
                    filledYet[i][j] = 2;
                } else {
                    filled[i][j] = false;
                    filledYet[i][j] = 0;
                }
            }
        }
        pacxonX = 0;
        pacxonY = 0;
        enemyX = (int) random(1, gridSizeX - 1);
        enemyY = (int) random(1, gridSizeY - 1);
        enemyDirectionX = 1;
        enemyDirectionY = 1;
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
    }

    public void draw() {
        background(255);
        drawGrid();
        drawInformationBar();
        drawPacxon();
        drawEnemy();
        updatePacxon();
        updateEnemy();
    }

    private void drawGrid() {
        for (int i = 0; i < gridSizeX; i++) {
            for (int j = 0; j < gridSizeY; j++) {
                if (filled[i][j] || i == 0 || j == 0 || i == gridSizeX - 1 || j == gridSizeY - 1) {
                    fill(0);
                    rect(i * tileWidth, j * tileHeight + 80, tileWidth, tileHeight);
                } else if (filledYet[i][j] == 1) {
                    fill(150);
                    rect(i * tileWidth, j * tileHeight + 80, tileWidth, tileHeight);
                } else {
                    fill(255);
                }
            }
        }
    }

    private void drawInformationBar() {
        fill(200);
        rect(0, 0, windowWidth, 80);
        fill(0);
        textSize(20);
        text("Lives: 3", 10, 30);
        text("Progress: 50%", 10, 60);
        text("Level: 1", 150, 30);
    }

    private void drawPacxon() {
        fill(255, 255, 0);
        rect(pacxonX * tileWidth, pacxonY * tileHeight + 80, tileWidth, tileHeight);
    }

    private void drawEnemy() {
        fill(255, 0, 0);
        rect(enemyX * tileWidth, enemyY * tileHeight + 80, tileWidth, tileHeight);
    }

    private void updatePacxon() {
        if (move == 1 && pacxonY > 0 && upPressed) {
            if (!filled[pacxonX][pacxonY - 1]) {
                pacxonY--;
                filledYet[pacxonX][pacxonY] = 1;
            } else {
                pacxonY--;
            }
        } else if (move == 2 && pacxonY < gridSizeY - 1 && downPressed) {
            if (!filled[pacxonX][pacxonY + 1]) {
                pacxonY++;
                filledYet[pacxonX][pacxonY] = 1;
            } else {
                pacxonY++;
            }
        } else if (move == 3 && pacxonX > 0 && leftPressed) {
            if (!filled[pacxonX - 1][pacxonY]) {
                pacxonX--;
                filledYet[pacxonX][pacxonY] = 1;
            } else {
                pacxonX--;
            }
        } else if (move == 4 && pacxonX < gridSizeX - 1 && rightPressed) {
            if (!filled[pacxonX + 1][pacxonY]) {
                pacxonX++;
                filledYet[pacxonX][pacxonY] = 1;
            } else {
                pacxonX++;
            }
        } else {
            move = 0;
            upPressed = false;
            leftPressed = false;
            downPressed = false;
            rightPressed = false;
        }
        
        if(filledYet[pacxonX][pacxonY]==2){
            int dx = 0, dy = 0;
            drop(enemyX,enemyY);
            for (int i = 0; i < gridSizeX; i++) {
                for (int j = 0; j < gridSizeY; j++) {
                    if(filledYet[i][j]==-1){
                        filledYet[i][j]=0;
                    }
                    else{
                        filledYet[i][j]=1;
                    }
                }
            }
            updateFilled();
        }
    }

   private void updateEnemy() {
    int nextX = enemyX + enemyDirectionX;
    int nextY = enemyY + enemyDirectionY;

    if (nextX <= 0 || nextX >= gridSizeX - 1 || filled[nextX][enemyY]) {
        enemyDirectionX *= -1;
    }

    if (nextY <= 0 || nextY >= gridSizeY - 1 || filled[enemyX][nextY]) {
        enemyDirectionY *= -1;
    }

    enemyX += enemyDirectionX;
    enemyY += enemyDirectionY;
}

    // 1 tembok
   // 2 filled yet=1
   public void drop(int x, int y){
       if(filledYet[x][y]==0){
           filledYet[x][y]=-1;
       }
       if(filledYet[x-1][y]==0){
           drop(x-1,y);
       }
       if(filledYet[x+1][y]==0){
           drop(x+1,y);
       }
       if(filledYet[x][y-1]==0){
           drop(x,y-1);
       }
       if(filledYet[x][y+1]==0){
           drop(x,y+1);
       }
   }
    
    private void updateFilled() {
        for (int i = 0; i < gridSizeX; i++) {
            for (int j = 0; j < gridSizeY; j++) {
                if (filledYet[i][j] == 1) {
                    filled[i][j] = true;
                    filledYet[i][j] = 2;
                }
            }
        }
    }
   
    public void keyPressed() {
        if (keyCode == ENTER) {
            for (int i = 0; i < gridSizeX; i++) {
                for (int j = 0; j < gridSizeY; j++) {
                    if (i == 0 || j == 0 || i == gridSizeX - 1 || j == gridSizeY - 1) {
                        filled[i][j] = true;
                        filledYet[i][j] = 2;
                    } else {
                        filled[i][j] = false;
                        filledYet[i][j] = 0;
                    }
                }
            }
            pacxonX = 0;
            pacxonY = 0;
        }

        if (key == 'w' && pacxonY > 0) {
            if(filled[pacxonX][pacxonY]){
                move = 1;
                upPressed = true;    
            }
            else if(move != 2){
                move = 1;
                upPressed = true;  
            }
        } else if (key == 's' && pacxonY < gridSizeY - 1) {
            if(filled[pacxonX][pacxonY]){
                move = 2;
                downPressed = true;    
            }
            else if(move != 1){
                move = 2;
                downPressed = true;  
            }
        } else if (key == 'a' && pacxonX > 0) {
            if(filled[pacxonX][pacxonY]){
                move = 3;
                leftPressed = true;    
            }
            else if(move != 4){
                move = 3;
                leftPressed = true;  
            }
        } else if (key == 'd' && pacxonX < gridSizeX - 1) {
            if(filled[pacxonX][pacxonY]){
                move = 4;
                rightPressed = true;    
            }
            else if(move != 3){
                move = 4;
                rightPressed = true;  
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main("pacxon.Pacxon");
    }
}
