package gr.pacman.game;

import java.util.ArrayList;
import java.util.List;

import gr.pacman.game.Grid.type;
import gr.pacman.utils.Constants;
import gr.pacman.utils.Point;

public class Ghost implements Constants, Cloneable{
	public int direction;
	public Point location,prevLocation;
	public Grid mazeLocation,prevMazeLocation;
	public double speed;
	
	public String strategy;
	public boolean spawned,edible;
	public colour colour;
	public Grid target,scatterTarget;
	
	public double distanceFromPac;
	public Grid spawnLocation;
	
	public Ghost(colour colour) {
		this.location = new Point(0,0);
		this.prevLocation = new Point(0,0);
		this.direction = 4;
		this.speed = 0.0;
		
		this.strategy = "Scatter";
		this.spawned = false;
		this.colour = colour;
		
	}
	
	public Ghost(Ghost oldGhost) {
		this.location = oldGhost.location.clone();
		this.prevLocation = oldGhost.prevLocation.clone();
		this.mazeLocation = oldGhost.mazeLocation.clone();
		this.prevMazeLocation = oldGhost.prevMazeLocation.clone();
		this.direction = oldGhost.direction;
		this.speed = oldGhost.speed;
		this.strategy = oldGhost.strategy;
		this.spawned = oldGhost.spawned;
		this.edible = oldGhost.edible;
		this.distanceFromPac = oldGhost.distanceFromPac;
		this.spawnLocation = oldGhost.spawnLocation.clone();
		//this.target = oldGhost.target.clone();
		//this.scatterTarget = oldGhost.scatterTarget.clone();
		this.colour = oldGhost.colour;
	}

	public enum colour {
		Red, Pink, Blue, Orange, Edible
	}

	public void setState(Point coord, Grid tile) {
		if(mazeLocation!=null){
			this.prevMazeLocation = this.mazeLocation;
		}else{
			this.prevMazeLocation = tile;
		}
		
		this.mazeLocation = tile;
		
		if(this.location.getX() == 0 && this.location.getY() == 0){
			this.prevLocation.set(coord);
		}else{
			this.prevLocation.set(this.location);
		}
		
		this.location.set(coord);		
		
		//find new direction else uses old
		if(this.prevMazeLocation != this.mazeLocation){
			this.direction = this.prevMazeLocation.getDirection(this.mazeLocation);
		}//if not moving and edible but pacman is moving reset the ghost
	}
	
	public void setState(Grid tile) {
		this.prevMazeLocation = this.mazeLocation;
		this.mazeLocation = tile;

		if(this.prevMazeLocation != this.mazeLocation){
			this.direction = this.prevMazeLocation.getDirection(this.mazeLocation);
		}
	}
	
	public void setMazeLoc(Grid loc){
		this.mazeLocation = loc;
	}
	
	public void setEdible(boolean edible) {
		this.edible =  edible;
	}
	public void setSpawned(boolean spawned) {
		this.spawned = spawned;
	}
	
	public List<Grid> getLegalMoves(){
		List<Grid> legalMoves = new ArrayList<Grid>();
		if(getOppositeDirection() != UP && mazeLocation.up != null && mazeLocation.up.type != type.Wall){
			legalMoves.add(mazeLocation.up);
		}
		if(getOppositeDirection() != RIGHT && mazeLocation.right != null && mazeLocation.right.type != type.Wall){
			legalMoves.add(mazeLocation.right);
		}
		if(getOppositeDirection() != DOWN && mazeLocation.down != null && mazeLocation.down.type != type.Wall){
			legalMoves.add(mazeLocation.down);
		}
		if(getOppositeDirection() != LEFT && mazeLocation.left != null && mazeLocation.left.type != type.Wall){
			legalMoves.add(mazeLocation.left);
		}
		
		return legalMoves;
		
	}
	
	public List<Integer> getLegalDirections(){
		List<Integer> legalDirections = new ArrayList<Integer>();
		if(getOppositeDirection() != UP && mazeLocation.up != null && mazeLocation.up.type != type.Wall){
			legalDirections.add(UP);
		}
		if(getOppositeDirection() != RIGHT && mazeLocation.right != null && mazeLocation.right.type != type.Wall){
			legalDirections.add(RIGHT);
		}
		if(getOppositeDirection() != DOWN && mazeLocation.down != null && mazeLocation.down.type != type.Wall){
			legalDirections.add(DOWN);
		}
		if(getOppositeDirection() != LEFT && mazeLocation.left != null && mazeLocation.left.type != type.Wall){
			legalDirections.add(LEFT);
		}
		
		return legalDirections;
		
	}
	
	public int getOppositeDirection(){
    	int opposite = 0;
    	if(direction == UP){
        	opposite = DOWN;
        } else if (direction == RIGHT){
        	opposite = LEFT;
        } else if (direction == DOWN){
        	opposite = UP;
        } else if (direction == LEFT){
        	opposite = RIGHT;
        }
    	return opposite;
    	
    }

	public colour getColour() {
		return this.colour;
	}
	
	
	public double getGhostSpeed(){
		return this.speed;
	}
	
	public void setDistanceFromPac(Point pacman) {
		
		this.distanceFromPac = location.dist(pacman);
	}

	public void setConsumed() {
		this.mazeLocation = this.spawnLocation;
		setEdible(false);
		setSpawned(false);	
	}
	
	@Override
	public Ghost clone(){  
	    try{  
	        return (Ghost) super.clone();  
	    }catch(Exception e){ 
	        return null; 
	    }
	}
}
