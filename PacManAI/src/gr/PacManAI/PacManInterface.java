package gr.PacManAI;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Scalar;

public class PacManInterface {
	static int delay = 30;
	
	Robot robot;

	ImageExtractor ie;

	public static int width = 450;
    public static int height = 550;
    
    static String currentState;
        
    //these are all bgr
    static Scalar blinky = new Scalar(0, 0, 221);//red
    static Scalar pinky = new Scalar(153, 153, 255);//pink
    static Scalar inky = new Scalar(255, 255, 102);//cyan
    static Scalar clyde = new Scalar(0, 153, 255);//orange
    static Scalar pacman = new Scalar(51, 255, 255);//yellow
    static Scalar edible = new Scalar(255, 33, 33);//blue
    static Scalar pill = new Scalar(255, 255, 255);//odd pink
    static Scalar powerPill = new Scalar(255, 255, 255);//
    static Scalar wall = new Scalar(255, 51, 0);//wall + edible ghost colour
    static Scalar background = new Scalar(0, 0, 0);//
        
    static ArrayList<Scalar> colours = new ArrayList<Scalar>(); 
    static {
        colours.add(blinky);
        colours.add(pinky);
        colours.add(inky);
        colours.add(clyde);
        colours.add(pacman);
        colours.add(edible);
    }
    
    static ArrayList<Scalar> mazeColours = new ArrayList<Scalar>(); 
    static {
    	mazeColours.add(pill);
    	mazeColours.add(wall);
    	mazeColours.add(background);
    }
	
	public static void main(String[] args) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		PacManInterface pac = new PacManInterface();
		Controller c = new Controller();
		SimpleAI ai = new SimpleAI();//goes towards the closest pill
		
		
		while(true) {	
			pac.determineState();
			if(currentState == "StartScreen") {
				c.StartGame();
			}

			pac.updateGameState();
			
			int action = ai.getAction(pac.ie.gs);
			
			c.move(action);
			
			Thread.sleep(delay);
		}
	}
	
	public void determineState() throws Exception {
		
		Color color = robot.getPixelColor(960, 505);
		int rgb = color.getRGB();
		if( rgb == -3355444) {
			currentState = "StartScreen";		
			return;
		}
		color = robot.getPixelColor(950, 405);
		rgb = color.getRGB();
		if(rgb == -1) {//this will may set off if a ghost goes through?
			currentState = "Ready";
			
			return;
			
		}
		currentState = "Playing";
		
	}

	public void updateGameState() throws Exception {
		ie.gs.reset();
		ie.update(colours);
		
    }
	
	public PacManInterface() throws Exception {
        robot = new Robot();
        ie = new ImageExtractor(737, 138, width, height);
        ie.createMaze(mazeColours);

	}
	
}
