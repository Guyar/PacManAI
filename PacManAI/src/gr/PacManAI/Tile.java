package gr.PacManAI;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Tile implements Constants {
	public int x, y;
	public type type;
	public Tile up;
	public Tile right;
	public Tile down;
	public Tile left;
	
	public Tile(int x, int y, type type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}
	public enum type {
		Pill, Power, Wall, Bkgnd
	}
	
	public List<Tile> getNeighbours() {
		List<Tile> neighbours = new ArrayList<Tile>();
		if(up != null){
			neighbours.add(up);
		}
		if(right != null){
			neighbours.add(right);
		}
		if(down != null){
			neighbours.add(down);
		}
		if(left != null){
			neighbours.add(left);
		}
		return neighbours;
	}
	
	public Tile nextTo(int d)
    {
		if(d == 1){
			return up;
		} else if(d == 2){
			return right;
		} else if(d == 3){
			return down;
		} else if(d == 4){
			return left;
		}
		return null;
    }
	
	public int getDirection(Tile finish){
		
		int direction = 0;
		int dx = finish.x - x;
		int dy = finish.y - y;

		Point2D tmp = new Point2D.Double(dx, dy);
	    
	    if (tmp.equals(NEUTRAL)){
	    	direction = NEUTRAL;
	    } else if (tmp.equals(Up)) {
	    	direction = UP;
	    } else if (tmp.equals(Right)) {
	    	direction = RIGHT;
	    } else if (tmp.equals(Down)) {
	    	direction = DOWN;
	    } else if (tmp.equals(Left)) {
	    	direction = LEFT;
	    }
		return direction;
	}
	
	public int dist(Tile tile){
		return Math.abs(x-tile.x) + Math.abs(y-tile.y);
	}
	
	public double dist(double xcoord, double ycoord){
		return Math.abs(x-xcoord) + Math.abs(y-ycoord);
		
	}
	
}
