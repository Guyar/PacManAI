package gr.PacManAI;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import gr.PacManAI.Tile.type;

public class GameState implements Constants{
	static ArrayList<Tile> pills; 
	static ArrayList<Tile> powerPills;
	static HashMap<Integer,Point2D> ghostLoc = new HashMap<Integer,Point2D>();
	
	Point2D closestPill;
	Point2D cur, prev, tmp;
	int currentDirection;
	int move;
	int blocksize = 16;
	int pillColour = -1;
	
	static Point2D[] directions = {
            new Point2D.Double(0, -1),
            new Point2D.Double(1, 0),
            new Point2D.Double(0, 1),
            new Point2D.Double(-1, 0),
    };
	
	public GameState() {
		prev = new Point2D.Double(0, 0);
    	cur = new Point2D.Double(0, 0);
    	tmp = new Point2D.Double(0, 0);	
					
		pills = new ArrayList<Tile>(); 
		powerPills = new ArrayList<Tile>(); 
		closestPill = null;
		
    }
	
	public void updateLocations(int type, Point2D coordinates, double contourArea, BufferedImage image) {	   			
		if (isPacMan(type, coordinates, contourArea)) {
            updatePacMan(coordinates);
            
        } else if (isGhost(contourArea)) {     	
	        if (type == 0) {//blinky
	            // need to update the state of the ghost distance
	        	ghostLoc.put(0, coordinates);
	        } else if (type == 1) {
	        	ghostLoc.put(1, coordinates);
	        } else if (type == 2) {
	        	ghostLoc.put(2, coordinates);
	        } else if (type == 3) {
	        	ghostLoc.put(3, coordinates);
	        }
        }
    }
	
	public void updatePills(BufferedImage image) {
		for (Iterator<Tile> iterator = pills.iterator(); iterator.hasNext();) {
		    Tile pill = iterator.next();
		    int centerX = (pill.x*blocksize)+8;
    		int centerY = (pill.y*blocksize)+8;
    		
    		int rgb = image.getRGB(centerX,centerY);
    		if(rgb != pillColour){//make sure this is rbg not bgr
    			//System.out.print(pill.x + " " + pill.y + " ");
	    	    //System.out.println();
    			pill.type = type.Bkgnd;
    			iterator.remove();
    		}/* else {
    			updateClosestPill();
    		}*/
		}
		System.out.println("pills" + pills.size());
		
	}
	public void updatePowerPills(BufferedImage image) {
		for (Tile tile : powerPills){
    		type type = tile.type;
    		
    		int centerX = (tile.x*blocksize)+8;
    		int centerY = (tile.y*blocksize)+8;
    		
    		int rgb = image.getRGB(centerX,centerY);
    		if(rgb != pillColour){
    			tile.type = type.Bkgnd;
    			pills.remove(tile);
    		}
    	}
	}	
	
    public void updateClosestPill(Point2D coord) {
    	if (closestPill == null) {	
    		closestPill = new Point2D.Double(coord.getX(),coord.getY());
        } else if (coord.distance(cur) < closestPill.distance(cur)) {   	
        	closestPill.setLocation(coord);//checks if the pill is closer than the current closest pill   	
        }
	}
    
    public int closestPillDir() {
    	int closestPillDir = -1;
    	double best = 100000;
    	
        
    	for (int i = 0; i < directions.length; i++) {
			tmp.setLocation((cur.getX()+directions[i].getX()),(cur.getY()+directions[i].getY()));

			//double curScore = (cur.getX() - tmp.getX()) + (cur.getY() - tmp.getY());
			//System.out.println(closestPill);
            double curScore = tmp.distance(closestPill);
            if (curScore < best) {
            	closestPillDir = i;
                best = curScore;
            }
        }
    	closestPillDir += 1; //so it matches directions 	
    	if (closestPillDir == currentDirection) {
    		closestPillDir = NEUTRAL;
        }
    	return closestPillDir;
	}
    
	public void updatePacMan(Point2D coord) {
    	prev.setLocation(cur);
    	//System.out.println("prev = " + prev);
        cur.setLocation(coord.getX(), coord.getY());
        //System.out.println("cur = "+ cur);
        
        updateDirection(prev,cur);//updates pacmans current direction
        //System.out.println(currentDirection);
        
    }
    
    public void updateDirection(Point2D prev, Point2D cur) {   	
        tmp.setLocation(cur);
        tmp.setLocation(round(tmp.getX()-prev.getX()), round(tmp.getY()-prev.getY()));
        
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
    
    public boolean isPacMan(int type, Point2D coords, double area) {		
		return coords.getY() < 495.0 && type == 4;
        //only do it if coordinates aren't outside the game
    }
    
    public boolean isGhost(double area) {
    	return area > 346.0 && area < 431.0;
    }
    
    private double round(double value){
    	if (value == -1.0 || value == 1.0){
    		return 0.0;
    	}
		return value;
    	
    }
    
	public void reset() {
		closestPill = null;
		
	}

}
