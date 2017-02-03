package gr.PacManAI;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ScreenShot {

	public ScreenShot(int x, int y) throws Exception {
		Robot robot = new Robot();

	    int width = 200;
	    int height = 200;
	    Rectangle area = new Rectangle(x, y, width, height);
	    BufferedImage bufferedImage = robot.createScreenCapture(area);
	    
	    File outputfile = new File("image.jpg");
		ImageIO.write(bufferedImage, "jpg", outputfile);

	}

}

