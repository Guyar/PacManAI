package gr.pacman.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gr.pacman.utils.Constants;
import gr.pacman.utils.Point;

public class Grid implements Cloneable, Constants {
	public int x, y;
	public type type;
	public Grid up,right,down,left;
	public boolean tunnel;
	public HashMap<Grid, Integer> lookupTable;
	
	public Grid(int x, int y, type type) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.lookupTable = new HashMap<Grid, Integer>();
	}
	
	public Grid(Grid clone) {
		this.x = clone.x;
		this.y = clone.y;
		this.type = clone.type;
		this.tunnel = clone.tunnel;
	}
	
	public enum type {
		Pill, Power, Wall, Bkgnd
	}
	
	public List<Grid> getLegalNeighbours() {
		List<Grid> neighbours = new ArrayList<Grid>();
		if(this.up != null && this.up.type != type.Wall){
			neighbours.add(this.up);
		}
		if(this.right != null && this.right.type != type.Wall){
			neighbours.add(this.right);
		}
		if(this.down != null && this.down.type != type.Wall){
			neighbours.add(this.down);
		}
		if(this.left != null && this.left.type != type.Wall){
			neighbours.add(this.left);
		}
		
		return neighbours;
		
	}
	
	public List<Grid> getNeighbours() {
		List<Grid> neighbours = new ArrayList<Grid>();
		if(this.up != null){
			neighbours.add(this.up);
		}
		if(this.right != null){
			neighbours.add(this.right);
		}
		if(this.down != null){
			neighbours.add(this.down);
		}
		if(this.left != null){
			neighbours.add(this.left);
		}
		return neighbours;
	}
	
	public Grid nextTo(int d)
    {
		if(d == 1){
			return this.up;
		} else if(d == 2){
			return this.right;
		} else if(d == 3){
			return this.down;
		} else if(d == 4){
			return this.left;
		}
		return null;
    }
	
	public List<Integer> getLegalDirections(){
		List<Integer> legalDirections = new ArrayList<Integer>();
		if(this.up != null && this.up.type != type.Wall){
			legalDirections.add(UP);
		}
		if(this.right != null && this.right.type != type.Wall){
			legalDirections.add(RIGHT);
		}
		if(this.down != null && this.down.type != type.Wall){
			legalDirections.add(DOWN);
		}
		if(this.left != null && this.left.type != type.Wall){
			legalDirections.add(LEFT);
		}
		return legalDirections;	
	}
	
	public int getDirection(Grid finish){
		int direction = 0;
		int dx = finish.x - this.x;
		int dy = finish.y - this.y;

	    if((finish.x == 27 || finish.x == 0) && this.tunnel){
	    	if (dx < 0) {
	    		direction = RIGHT;
	    	}else if (dx > 0){
	    		direction = LEFT;
	    	}
	    	return direction;
	    }
	    
	    if (dx == 0 && dy == 0){
	    	direction = NEUTRAL;
	    } else if (dy < 0) {
	    	direction = UP;
	    } else if (dx > 0) {
	    	direction = RIGHT;
	    } else if (dy > 0) {
	    	direction = DOWN;
	    } else if (dx < 0) {
	    	direction = LEFT;
	    }
		return direction;
	}
	
	public double tableDist(Grid tile){
		return lookupTable.get(tile);
	}
	
	public double distSq(Grid tile){
		double dx = this.x-tile.x;
		double dy = this.y-tile.y;
		return (dx*dx)+(dy*dy);
	}
	
	public double distSq(Point point){
		double dx = this.x-point.getX();
		double dy = this.y-point.getY();
		return (dx*dx)+(dy*dy);
	}
	
	public double dist(Grid tile) {
		double dx = this.x-tile.x;
		double dy = this.y-tile.y;
		return Math.sqrt(dx*dx)+(dy*dy);
	}
	
	public double dist(Point point) {
		double dx = this.x-point.getX();
		double dy = this.y-point.getY();
		return Math.sqrt(dx*dx)+(dy*dy);
	}
	
	public int distManhatton(Grid tile) {
		int dx = Math.abs(this.x-tile.x);
		int dy = Math.abs(this.y-tile.y);
		return dx + dy;
	}
	public double distManhatton(Point point) {
		double dx = Math.abs(this.x-point.getX()); 
		double dy = Math.abs(this.y-point.getY());
		return dx + dy;
	}
	
	public boolean equals(Grid tile){
		return tile.x == this.x && tile.y == this.y;
	}
	
	public String toString() {
		String position =  "(" + Integer.toString(x) + "," + Integer.toString(y) + ")";
		return position;
	}
	
	@Override
	public Grid clone(){  
	    try{  
	        return (Grid) super.clone();  
	    }catch(Exception e){ 
	        return null; 
	    }
	}
}
