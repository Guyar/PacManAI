package gr.PacManAI;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import gr.PacManAI.Tile.type;

public class GameState implements Constants{
	Robot robot;
	ImageExtractor ie;
	Rectangle area;
	
	Tile closestPill;
	int closestPillDir;
	Point2D pacManLoc, prev, tmp;
	int currentDirection;
	int move;
	int blocksize = 16;
	int bkgndColour = -205;
	int pacColour = -16777216;
	
	int width = 450;
	int height = 550;
	
	
	static Point2D[] directions = {
            new Point2D.Double(0, -1),
            new Point2D.Double(1, 0),
            new Point2D.Double(0, 1),
            new Point2D.Double(-1, 0),
    };
	
	public GameState() throws Exception {	
		area = new Rectangle(737, 138, width, height);
		ie = new ImageExtractor(area);
		
		ie.createMaze(PacManInterface.mazeColours);
        ie.surroundingTiles();
        //ie.printSurroundingTiles();
        //ie.printMaze();
        
        
		robot = new Robot();
		prev = new Point2D.Double(0, 0);
		pacManLoc = new Point2D.Double(14, 23);
		
    	tmp = new Point2D.Double(0, 0);			
		closestPill = null;
		
    }
	
	public void update() throws Exception {
		BufferedImage bufferedImage = robot.createScreenCapture(area);
		ie.updatePacmanGhosts(PacManInterface.colours);
		
		updatePacMan(ie.pacCoordinates);
		updatePills(bufferedImage);
    	updatePowerPills(bufferedImage);
    	
    	
    	//updateGhosts(ie.ghostLoc); 
    	closestPillDir();
    }
	
	private void updateGhosts(HashMap<Integer, Point2D> ghostLoc) {
		
	}

	public void updatePills(BufferedImage im) {
		
		for (Iterator<Tile> iterator = ie.pills.iterator(); iterator.hasNext();) {
		    Tile pill = iterator.next();
		    int centerX = (pill.x*blocksize)+8;
    		int centerY = (pill.y*blocksize)+8;
    		
    		
    		int rgb = im.getRGB(centerX,centerY);
    		if(rgb == bkgndColour || rgb == pacColour){//make sure this is rbg not bgr
    			//System.out.println(pill.x + " " + pill.y + " ");
	    	    //System.out.println();
    			pill.type = type.Bkgnd;
    			iterator.remove();
    		} else {
    			//System.out.println(pill.x + " " + pill.y + " ");
    			updateClosestPill(pill);
    		}
		}
		//System.out.println("pills " + pills.size());
		
	}
	public void updatePowerPills(BufferedImage im) {
		for (Iterator<Tile> iterator = ie.powerPills.iterator(); iterator.hasNext();) {
		    Tile powerPill = iterator.next();
		    int centerX = (powerPill.x*blocksize)+8;
    		int centerY = (powerPill.y*blocksize)+8;
    		
    		int rgb = im.getRGB(centerX,centerY);
    		
    		if(rgb == bkgndColour || rgb == pacColour){//make sure this is rbg not bgr || rgb == pacColour
    			//System.out.println(pill.x + " " + pill.y + " ");
	    	    //System.out.println();
    			powerPill.type = type.Bkgnd;
    			iterator.remove();
    		}
		}
	}	
	
    public void updateClosestPill(Tile pill) {
    	if (closestPill == null) {	
    		closestPill = pill;
        } else if (pill.dist(pacManLoc.getX(),pacManLoc.getY()) < closestPill.dist(pacManLoc.getX(),pacManLoc.getY())) {   
        	closestPill = pill;//checks if the pill is closer than the current closest pill
        	//System.out.println(pill.dist(cur.getX(),cur.getY()));
        }
	}
    
    public void closestPillDir() {
    	//System.out.println(closestPill.x + " " + closestPill.y);
    	closestPillDir = -1;
    	double best = 100000;
    	//System.out.println("pill: " + closestPill.x + " " + closestPill.y);
    	//System.out.println("pacman: " + cur.getX() + " " + cur.getY());//Math.floor(xcoord/16);
    	for (int i = 0; i < directions.length; i++) {
			//tmp.setLocation((cur.getX()+directions[i].getX()),(cur.getY()+directions[i].getY()));
    		double tempx = pacManLoc.getX()+directions[i].getX();
    		double tempy = pacManLoc.getY()+directions[i].getY();
    		
    		double curScore = closestPill.dist(tempx,tempy);
			//double curScore = (cur.getX() - tmp.getX()) + (cur.getY() - tmp.getY());
    		//System.out.println("x: " + tempx + " y: "+ tempy);
    		//if(ie.Tiles[(int)(tempx)][(int)(tempy)].type != type.Wall){//this may make it move back and forth
    			if (curScore < best) {
                	closestPillDir = i;
                    best = curScore;
                }
        	//}     
        }
    	closestPillDir += 1; //so it matches directions 

    	//System.out.println(closestPillDir);
	}
    
	public void updatePacMan(Point2D coord) {
    	prev.setLocation(pacManLoc);
    	//System.out.println("prev = " + prev);
    	double curx = Math.floor(coord.getX()/16);
		double cury = Math.floor(coord.getY()/16);
		//System.out.println("pacManCoords: " + curx + " " + cury);
		pacManLoc.setLocation(curx, cury);
        //System.out.println("cur = "+ cur);
        
        updateDirection(prev,pacManLoc);//updates pacmans current direction
        //System.out.println(currentDirection);
        
    }
    
    public void updateDirection(Point2D prev, Point2D cur) {   	
        tmp.setLocation(cur);
        tmp.setLocation(tmp.getX()-prev.getX(), tmp.getY()-prev.getY());
        if (tmp.equals(NEUTRAL)){
        	currentDirection = NEUTRAL;
        } else if (tmp.equals(Up)) {
        	currentDirection = UP;
        } else if (tmp.equals(Right)) {
        	currentDirection = RIGHT;
        } else if (tmp.equals(Down)) {
        	currentDirection = DOWN;
        } else if (tmp.equals(Left)) {
        	currentDirection = LEFT;
        }
        
        
        /*javafx code
        //subtract them from eachother
        tmp = cur.subtract(prev);
        
        if (tmp.equals(NEUTRAL)){
        	currentDirection = NEUTRAL;
        } else if (tmp.dotProduct(vUp) > 0) {
        	currentDirection = UP;
        } else if (tmp.dotProduct(vRight) > 0) {
        	currentDirection = RIGHT;
        } else if (tmp.dotProduct(vDown) > 0) {
        	currentDirection = DOWN;
        } else if (tmp.dotProduct(vLeft) > 0) {
        	currentDirection = LEFT;
        }
        // could use case???
        */


    } 
    
	public void reset() {
		closestPill = null;
		
	}
}
