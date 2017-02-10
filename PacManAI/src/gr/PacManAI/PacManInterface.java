package gr.PacManAI;

import java.awt.Robot;

public class PacManInterface {
	Robot robot;
	
	public static void main(String[] args) throws Exception {
		PacManInterface pac = new PacManInterface();
		PacController controller = new PacController();
		
	}
	
	public PacManInterface() throws Exception {
        robot = new Robot();
        //pixels = new int[width * height];
        //ce = new SimpleExtractor(width, height);
        //sd = new SimpleDisplay(width, height);
        //new JEasyFrame(sd, "Extracted", true);*/
        ScreenShot a = new ScreenShot(734,120);
    }

}
