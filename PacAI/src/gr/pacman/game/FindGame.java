package gr.pacman.game;

import java.awt.Rectangle;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT; 

public class FindGame {
	public FindGame() {
	}
	
	public Rectangle windowArea() {
		String windowName = "ARCADE GAME SERIES: PAC-MAN";
		HWND hwnd = User32.INSTANCE.FindWindow(null, windowName);
		
		RECT rect = new RECT();
		Rectangle result = null;
		boolean rectOK = User32.INSTANCE.GetWindowRect(hwnd, rect);
		if (rectOK) {
	        int x = rect.left;
	        int y = rect.top;
	        int width = rect.right - rect.left;
	        int height = rect.bottom - rect.top;
	        
	        result = new Rectangle(x, y, width, height);
	    }
		return result;
	}
}
