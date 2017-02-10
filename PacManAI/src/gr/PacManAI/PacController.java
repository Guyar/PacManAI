package gr.PacManAI;

import java.awt.*;
import java.awt.event.KeyEvent;

public class PacController {
	Robot robot;
    int curKey;
    
    static int[] keys = {-1, KeyEvent.VK_UP, KeyEvent.VK_RIGHT,
            KeyEvent.VK_DOWN, KeyEvent.VK_LEFT};
    //up=1,right=2,down=3,left=4
    public PacController() {
        try {
            robot = new Robot();
            curKey = -1;
        } catch(Exception e) {}
    }
    
    public void move(int direction) {
    	System.out.println("aaa");   
        if (direction > 0 && direction < keys.length) {
			curKey = keys[direction];

            robot.keyPress(KeyEvent.VK_UP);
            robot.keyRelease(KeyEvent.VK_UP);
            robot.waitForIdle();
        }
    }
}
