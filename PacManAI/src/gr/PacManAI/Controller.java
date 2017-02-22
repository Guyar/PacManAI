package gr.PacManAI;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class Controller {
	Robot robot;
    int curKey;
    static int autoDelay = 20;
    
    static int[] keys = {-1, KeyEvent.VK_UP, KeyEvent.VK_RIGHT,
            KeyEvent.VK_DOWN, KeyEvent.VK_LEFT};

    public Controller() {
        try {
            robot = new Robot();
            robot.setAutoWaitForIdle(false);
            robot.setAutoDelay(autoDelay);
            curKey = -1;
        } catch(Exception e) {}
    }
    
    public void move(int direction) {
        if (direction > 0 && direction < keys.length) {
			curKey = keys[direction];

            robot.keyPress(curKey);
            robot.keyRelease(curKey);
            robot.waitForIdle();
        }
    }
    public void StartGame() {
    	robot.mouseMove(955,480);
    	robot.mousePress( InputEvent.BUTTON1_MASK );
    	robot.mouseRelease( InputEvent.BUTTON1_MASK );

	}
}
