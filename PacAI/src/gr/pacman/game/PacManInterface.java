package gr.pacman.game;

import java.awt.*;
import java.awt.image.BufferedImage;

import org.opencv.core.Core;

import gr.pacman.ai.BasicRuleBased;
import gr.pacman.ai.MiniMax;
import gr.pacman.ai.AdvancedRuleBased;
import gr.pacman.ai.TestAI;

public class PacManInterface {
	Robot robot;

	public static String currentState;
	public GameState gs;
	Controller c;
	FindGame fg;

	//RandomMoves ai;
	//BasicRuleBased ai;
	AdvancedRuleBased ai;
	//MiniMax ai;
	
	Rectangle window,gameArea;
	
	static final int WIDTH = 448;
	static final int HEIGHT = 575;

	private boolean gameStarted,mazeLoaded,readyShowing;
	
    public PacManInterface() throws Exception {
    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        robot = new Robot();
        
        fg = new FindGame(); 
        setWindow(fg.windowArea());
        gameArea = new Rectangle(getWindow().x + 419,getWindow().y + 98,WIDTH,HEIGHT); 
	    
	    gameStarted = false;
	    
        c = new Controller(getWindow().x + 419,getWindow().y + 98);
        
    	gs = new GameState(gameArea);
    	
    	//ai = new RandomMoves();
    	//ai = new BasicRuleBased();
    	//ai = new ANAI();
    	//ai = new SlightlyBetterAI();
    	ai = new AdvancedRuleBased();
    	//ai = new MiniMax();
    	//ai = new MonteCarlo();
    	//ai = new TestAI();
	}	
    
    public void play() throws Exception{
    	int action = 0;
    	
    	BufferedImage bufferedImage = robot.createScreenCapture(gameArea);
    	determineState(bufferedImage);
    	
		if(currentState == "Playing"){
			updateGameState();
			action = ai.getAction(gs);
		}
		c.move(action);
    }

    private void determineState(BufferedImage img) throws Exception {	
    	gs.lives = findLives(img);
    	
    	//nothing uses this?
    	int rgb = img.getRGB(227, 230);
    	if(!gameStarted){// 
    		if(rgb == -16711717){
    			currentState = "Game Started!";	
    			gameStarted = true;
    			return;	
    		}
    	}
    	
    	rgb = img.getRGB(205, 7);
    	if(rgb != -2368549){
    		currentState = "Playing Animation";	//between levels
    		return;
    	}

    	rgb = img.getRGB(220, 325);//coordinates of ready message
    	if(gs.pillsLeft == 0) {//this runs when a level has ended
    		mazeLoaded = false;
    		
        	if(rgb == -256) {//waits until ready message appears to initialise new level
        		gs.setPacManAlive(true);
        		gs.initialiseNewLevel();
        		gs.initialiseAgents();
        		currentState = "Ready!";
        		return;
        	} else {
        		currentState = "Loading";
        		return;
        	}
    	}
    	
    	if(!mazeLoaded){
    		if(rgb != -256){
    			currentState = "Playing";
    			mazeLoaded = true;
    			return;
    		}
    	}
    	
    	if(!gs.getPacManAlive()){//if pacman dies reset agents to spawn
    		gs.initialiseAgents();
    		if(rgb == -256){
        		readyShowing = true;
        	}
    		if(rgb != -256 && readyShowing){
    			readyShowing = false;
    			currentState = "Playing";
    			mazeLoaded = true;
    			gs.setPacManAlive(true);
    			return;
    		}
    		currentState = "Loading";
    		return;
    	}
    	if(mazeLoaded){
    		currentState = "Playing";
    	}
	}
    
    //counts the lives at the bottom of game
    private int findLives(BufferedImage img) {
    	int rgb;
    	int lives = 0;
    	for(int x = 0; x<5;x++){
    		rgb = img.getRGB(53+(x*32), 557);
    		if(rgb == -256){
    			lives += 1;
    		}
    	}
    	return lives;
	}

	public void updateGameState() throws Exception {
		gs.reset();
		gs.update(currentState);
    }

	public Rectangle getWindow() {
		return window;
	}

	public void setWindow(Rectangle window) {
		this.window = window;
	}
}
