package gr.PacManAI;

import java.awt.*;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Scalar;

public class PacManInterface {
	static int delay = 10;
	
	Robot robot;

	ImageExtractor ie;

	public static int width = 448;
    public static int height = 550;
        
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
        colours.add(pill);
        colours.add(wall);
        colours.add(background);
    }
	
	public static void main(String[] args) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		PacManInterface pac = new PacManInterface();
		Controller c = new Controller();
		SimpleAI ai = new SimpleAI();//goes towards the closest pill
		
		//determineState();
		while(true) {				
			pac.analyseComponents();
			
			int action = ai.getAction(pac.ie.gs);

			c.move(action);
			
			Thread.sleep(delay);
		}
	}
	public void determineState() {

		
	}
	public void analyseComponents() throws Exception {
		ie.TakeScreenShot(colours);
        ie.gs.reset();
    }
	
	public PacManInterface() throws Exception {
        robot = new Robot();
        ie = new ImageExtractor(737, 138, width, height);
        //ie.createMaze(wall);//should only make this run when game starts

	}
	
}
