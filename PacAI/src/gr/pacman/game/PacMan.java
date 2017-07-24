package gr.pacman.game;

import java.util.ArrayList;
import java.util.List;

import gr.pacman.game.Grid.type;
import gr.pacman.utils.Constants;
import gr.pacman.utils.Point;

public class PacMan implements Constants, Cloneable{
	public int direction,prevDirection;
	public Point location,prevLocation;
	public Grid mazeLocation,prevMazeLocation;
	public double speed;	
	
	public PacMan() {
		this.location = new Point(0,0);
		this.prevLocation = new Point(0,0);
		this.direction = 4;
		this.prevDirection = 4;
		this.speed = 0.0;
	}
	
	public PacMan(PacMan pac) {
		this.location = pac.location.clone();
		this.prevLocation = pac.prevLocation.clone();
		this.mazeLocation = pac.mazeLocation.clone();
		this.prevMazeLocation = pac.prevMazeLocation.clone();
		this.direction = pac.direction;
		this.prevDirection = pac.prevDirection;
		this.speed = pac.speed;
	}

	public void setState(Point coord, Grid tile) {
		if(this.mazeLocation!=null){
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
		//if(this.prevMazeLocation != this.mazeLocation){
		if(this.prevLocation != this.location){
			this.direction = this.prevLocation.getDirection(this.location);
		}
	}

	public void setState(Grid tile) {
		this.prevMazeLocation = this.mazeLocation;
		this.mazeLocation = tile;
		
		if(this.prevMazeLocation != this.mazeLocation){
			this.prevDirection = this.direction;
			this.direction = this.prevMazeLocation.getDirection(this.mazeLocation);
		}
		
	}
	
	public void setMazeLoc(Grid loc){
		this.prevMazeLocation = this.mazeLocation;
		this.mazeLocation = loc;
	}
	
	public List<Grid> getLegalMoves(){
		List<Grid> legalMoves = new ArrayList<Grid>();
		if(mazeLocation.up != null && mazeLocation.up.type != type.Wall){
			legalMoves.add(mazeLocation.up);
		}
		if(mazeLocation.right != null && mazeLocation.right.type != type.Wall){
			legalMoves.add(mazeLocation.right);
		}
		if(mazeLocation.down != null && mazeLocation.down.type != type.Wall){
			legalMoves.add(mazeLocation.down);
		}
		if(mazeLocation.left != null && mazeLocation.left.type != type.Wall){
			legalMoves.add(mazeLocation.left);
		}
		
		return legalMoves;
		
	}
	
	public List<Integer> getLegalDirections(){
		List<Integer> legalDirections = new ArrayList<Integer>();
		if(mazeLocation.up != null && mazeLocation.up.type != type.Wall){
			legalDirections.add(UP);
		}
		if(mazeLocation.right != null && mazeLocation.right.type != type.Wall){
			legalDirections.add(RIGHT);
		}
		if(mazeLocation.down != null && mazeLocation.down.type != type.Wall){
			legalDirections.add(DOWN);
		}
		if(mazeLocation.left != null && mazeLocation.left.type != type.Wall){
			legalDirections.add(LEFT);
		}
		
		return legalDirections;
		
	}
	
	public List<Grid> getLegalMovesNoReverse(){
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
	
	public List<Integer> getLegalDirectionsNoReverse(){
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
	
	@Override
	public PacMan clone(){  
	    try{  
	        return (PacMan) super.clone();  
	    }catch(Exception e){ 
	        return null; 
	    }
	}

}
