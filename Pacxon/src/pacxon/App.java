/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pacxon;
/**
 *
 * @author hanst
 */
import java.io.*;
import java.util.*;
import processing.core.PApplet;
import processing.data.JSONObject;
import processing.data.JSONArray;

public class App extends PApplet {
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    public int move=0;
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final int SPRITESIZE = 20;
    public static final int TOPBAR = 80;
    public int timeInSeconds;
    public static final int FPS = 60;
    private int moveDelay = 10; // Delay between each movement update
    private int moveCounter = 0;
    public String configPath;

//    public static Grass grass;
    public static Green greenPath;
    
    public int playerX;
    public int playerY;
    public JSONObject config;
    public JSONArray levels;

    public int timer;
    public int currentLevel;
    public int currentScore;
    public List<Concrete> concretes;
    public List<Dirt> dirts;
    public List<String> outlays;
    public List<Green> trail;
    public List<Grass> grass;
    public List<Double> goals;
    public List<JSONArray> enemies_ls;
    public ArrayList<Enemy> enemies;
    public static boolean[][] filled;
    public static int[][] filledYet;
    public static char[][] loadTiles;

    public App() throws Exception {
        this.configPath = "config.json";
        //this.readyToDraw = false;
        currentLevel=0;
        timer=0;


        // Creates new App objects for player, enemies, and map
        timeInSeconds=0;
        grass = new ArrayList<Grass>();
        dirts = new ArrayList<Dirt>();
        concretes = new ArrayList<Concrete>();
        outlays = new ArrayList<String>();
        goals = new ArrayList<Double>();
        this.enemies_ls = new ArrayList<JSONArray>();
        this.trail = new ArrayList<>();
        enemies = new ArrayList<>();
        
    }

    /**
     * Initialise the setting of the window size.
    */
    public void settings() {
        size(WIDTH, HEIGHT);
    }
    

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
    */
    public void setup() {
        frameRate(FPS);
        playerX=0;
        playerY=0;
        this.config = loadJSONObject(configPath);
        
        this.levels = config.getJSONArray("levels");

        // for loop to iterate through the contents in levels from the file config.json
        for (int i = 0; i < levels.size(); i++) {
            JSONObject level = levels.getJSONObject(i);
            outlays.add(level.getString("outlay"));
            goals.add(level.getDouble("goal"));
            JSONArray enemies = level.getJSONArray("enemies");
            enemies_ls.add(enemies);
        }
        try {
            readLevel(currentLevel);
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        
    }
    public static boolean playerDead;

    
    /** 
     * @param level
     * @throws IOException
     */
    // Method to read level and load map
    public void readLevel(int level) throws IOException{
        FileReader fr = new FileReader(this.outlays.get(level));
        BufferedReader br = new BufferedReader(fr);
        filled = new boolean[32][64];
        filledYet = new int[32][64];
        loadTiles = new char[32][64];
        int index;
        int row = 0;
        int col = 0;

        // While loop to iterate through the txt file, 
        while((index=br.read())!=-1){
            //System.out.print((char)i);
            if (col == 0){
                if (((char)index) == 'X'){
                loadTiles[row][col] = (char)index;
                col++;
                continue;
                } else{
                continue;
                }
            }
            loadTiles[row][col] = (char)index;

            if(col == 63){
                col = -1;
                row++;
            }

            col++;
            if (row == 32){
                break;
            }

        }

        br.close();
        fr.close();

        currentScore=0;
        concretes =  new ArrayList<>();
        for (int i = 0; i < 32; i++){
            for (int j = 0; j < 64; j++){
                if (loadTiles[i][j]== 'X'){
                    Concrete c= new Concrete(j*App.SPRITESIZE,App.TOPBAR+ i*App.SPRITESIZE);
                    c.setSprite(this.loadImage("src/assets/concrete_tile.png"));
                    concretes.add(c);
                    filledYet[i][j]=3;
                    filled[i][j]=true;
                }
                else{
                    Dirt d = new Dirt(j*App.SPRITESIZE,App.TOPBAR+ i*App.SPRITESIZE);
                    d.setSprite(this.loadImage("src/assets/dirt.png"));
                    dirts.add(d);
                    filledYet[i][j]=0;
                    filled[i][j]=false;
                }
            }
        }
        addEnemy();
        // Spawn enemies based on the configuration
        
    }
    
    public void addEnemy(){
        for (int i = 0; i < enemies_ls.get(currentLevel).size(); i++) {
            Random rand = new Random();
            JSONObject enemyObj = enemies_ls.get(currentLevel).getJSONObject(i);
            int type = enemyObj.getInt("type");
            String spawn = enemyObj.getString("spawn");
            int x,y;
            if(!spawn.equals("random")){ 
                String[] spawnCoords = spawn.split(",");
                y = Integer.parseInt(spawnCoords[0]);
                x = Integer.parseInt(spawnCoords[1]);
            }
            else{
                x=rand.nextInt(1, 63);
                y=rand.nextInt(1, 31);
            }

            Enemy enemy;

            if (type == 0) {
                enemy = new Worm(x * App.SPRITESIZE, App.TOPBAR + y * App.SPRITESIZE);
                enemy.setSprite(loadImage("src/assets/worm.png"));
            } else if (type == 1) {
                enemy = new Beetle(x * App.SPRITESIZE, App.TOPBAR + y * App.SPRITESIZE);
                enemy.setSprite(loadImage("src/assets/beetle.png"));
            } else {
                // Handle other enemy types if needed
                continue;
            }

            // Add the enemy to the list
            enemies.add(enemy);
        }
    }
    
    public void spawnPowerUp(){
        
        Random rnd = new Random();
        boolean powerUpType = rnd.nextBoolean();
        while (true){
            int posx= rnd.nextInt(62)+1;
            int posy= rnd.nextInt(30)+1;
            
            if (loadTiles[posy][posx]==' '){
                if(powerUpType)
                    loadTiles[posy][posx]='P';
                else
                    loadTiles[posy][posx]='p';
                
                break;
            }
        }
    }
    
    public void updateFilled(){
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 64; j++) {
                if (filledYet[i][j] == 1) {
                    filled[i][j] = true;
                    filledYet[i][j] = 2;
                }
            }
        }
    }
    
    public void drawDirt(){
        for (Dirt d : dirts) {
            d.draw(this);
        }
    }
    
    public void drawConcrete(){
        for (Concrete c : concretes) {
            c.draw(this);
        }
    }
    
    public void drawTopBar(){
        timer++;
        background(112,84,62);
        textSize(25);

        text("Level " + (currentLevel+1), 1160,60);

        // First update maps and all game objects
        currentScore=0;

        text("Timer " + (timer/FPS), 100,60);

        int displayscore=(int)((currentScore) / (64*32.0-concretes.size()) * 100);
        text( displayscore + "%/" + (int)(goals.get(currentLevel)*100)+"%", 800, 30); 

        if( displayscore >= goals.get(currentLevel)*100){
            currentLevel++;
            if (currentLevel< goals.size()){
                try {
                    //Thread.sleep(2000);
                    readLevel(currentLevel);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    /**
     * Draw all elements in the game by current frame.
    */
    public void drawPlayer(){
        image(loadImage("src/assets/ball.png"), playerX*App.SPRITESIZE,App.TOPBAR+ playerY*App.SPRITESIZE);
    }
    public void updatePlayer(){
        if(moveCounter >= moveDelay){
            if (move == 1 && playerY > 0 && upPressed) {
                if (filledYet[playerY-1][playerX]==0) {
                    playerY--;
                    filledYet[playerY][playerX] = 1;
                } else {
                    playerY--;
                }
            } else if (move == 2 && playerY < 32 - 1 && downPressed) {
                if (filledYet[playerY+1][playerX]==0) {
                    playerY++;
                    filledYet[playerY][playerX] = 1;
                } else {
                    playerY++;
                }
            } else if (move == 3 && playerX > 0 && leftPressed) {
                if (filledYet[playerY][playerX-1]==0) {
                    playerX--;
                    filledYet[playerY][playerX] = 1;
                } else {
                    playerX--;
                }
            } else if (move == 4 && playerX < 64 - 1 && rightPressed) {
                if (filledYet[playerY][playerX+1]==0) {
                    playerX++;
                    filledYet[playerY][playerX] = 1;
                } else {
                    playerX++;
                }
            } else {
                move = 0;
                upPressed = false;
                leftPressed = false;
                downPressed = false;
                rightPressed = false;
            }
            moveCounter = 0;
        }
        else{
            moveCounter++;
        }
        
        if(filledYet[playerY][playerX]>1){
            int dx = 0, dy = 0;
            for (Enemy e : enemies) {
                drop(e.getX()/20,(e.getY()-80)/20);
            }
            for (int i = 0; i < 32; i++) {
                for (int j = 0; j < 64; j++) {
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
    
    public void drop(int x, int y){
       if(filledYet[y][x]==0){
           filledYet[y][x]=-1;
       }
       if(filledYet[y][x-1]==0){
           drop(x-1,y);
       }
       if(filledYet[y][x+1]==0){
           drop(x+1,y);
       }
       if(filledYet[y-1][x]==0){
           drop(x,y-1);
       }
       if(filledYet[y+1][x]==0){
           drop(x,y+1);
       }
   }
    public void keyPressed(){
        if (key == 'w' && playerY > 0) {
            if(filledYet[playerY][playerX]==0){
                move = 1;
                upPressed = true;    
            }
            else if(move != 2){
                move = 1;
                upPressed = true;  
            }
        } else if (key == 's' && playerY < 32 - 1) {
            
            if(filledYet[playerY][playerX]==0){
                move = 2;
                downPressed = true;    
            }
            else if(move != 1){
                move = 2;
                downPressed = true;  
            }
        } else if (key == 'a' && playerX > 0) {
            if(filledYet[playerY][playerX]==0){
                move = 3;
                leftPressed = true;    
            }
            else if(move != 4){
                move = 3;
                leftPressed = true;  
            }
        } else if (key == 'd' && playerX < 64 - 1) {
            if(filledYet[playerY][playerX]==0){
                move = 4;
                rightPressed = true;    
            }
            else if(move != 3){
                move = 4;
                rightPressed = true;  
            }
        }
    }
    
    
    public void drawEnemy(){
        for (Enemy e : enemies) {
            e.draw(this);
        }
    }
    
    public void updateEnemy()
    {
        for (Enemy e : enemies) {
            e.move(filledYet);
        }
    }
    
    public void drawGrid(){
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 64; j++) {
                if (filledYet[i][j] == 1) {
                    fill(150);
                    rect(j * 20, i * 20 + 80, 20, 20);
                }
                else if(filledYet[i][j]==2){
                    fill(0);
                    rect(j * 20, i * 20 + 80, 20, 20);
                }
            }
        }
    }
    
    public void draw() {
        drawTopBar();
        
        drawGrid();
        drawConcrete();
//        drawDirt();
        drawPlayer();
        updatePlayer();
        drawEnemy();
        updateEnemy();
            //this = player.getWorld();
    }
    
    /**
     * Called every frame if a key is down.
     */
    
    
    /**
     * Run App
     */
    public static void main(String[] args) {
        PApplet.main("pacxon.App");
    }
}
