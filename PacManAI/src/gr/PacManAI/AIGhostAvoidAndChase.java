package gr.PacManAI;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import gr.PacManAI.Tile.type;

public class AIGhostAvoidAndChase implements Constants{
	static Point2D[] directions = {
            new Point2D.Double(0, -1),
            new Point2D.Double(1, 0),
            new Point2D.Double(0, 1),
            new Point2D.Double(-1, 0),
    };
	AITools tools;
	Tile ghostTile;
	Tile edibleGhostTile;
	public AIGhostAvoidAndChase(){
		tools = new AITools();
	}
	
    public int getAction(GameState gs) {
    	System.out.println("--------------------");
    	int direction = 0;
    	System.out.println("pacManCoords: " + gs.pacManLoc.getX() + " " + gs.pacManLoc.getY());
    	Tile pacMan = gs.ie.Tiles[(int) gs.pacManLoc.getX()][(int) gs.pacManLoc.getY()];
    	
    	
    	List<Tile> directionsList = tools.getDirections(pacMan, gs.closestPill);
    	
    	/*System.out.println("directions to pill:");
    	for(Tile tile : directionsList){
    		System.out.println("x = " + tile.x + ", y = " + tile.y);
    	}*/
    	System.out.println("hi");
    	if(directionsList.size()>1){
    		if (directionsList.get(1) != null){
        		direction = pacMan.getDirection(directionsList.get(1));//gs.closestPillDir;
        		System.out.println("direction = " + direction);
        	}
    	}
    	
    	int oppositeDirection = oppositeDirection(direction);
    	
    	
    	
    	if(powerPill is not active){	
    	
	    	for (Point2D coord : gs.ie.ghostLoc.values()) {
	    		
	    		ghostTile = gs.ie.Tiles[(int) coord.getX()/16][(int) coord.getY()/16];
		
	    		/*System.out.println("ghostTile neighbours: ");
	    		
	    		for(Tile tile : ghostTile.getNeighbours()){
	    			if(tile.type != type.Wall){
	    				System.out.println("X = " + tile.x + " y = " + tile.y);
	    			}
	            	
	                
	            }*/
	    		System.out.println();
	    		System.out.println("ghostTile: " + ghostTile.x + " " + ghostTile.y);
	    		System.out.println("pacManTile: " + pacMan.x + " " + pacMan.y);
	    		
	    		System.out.println("directions from ghost to pacman:");
	    		List<Tile> directionsGhost2PacMan = tools.getDirections(ghostTile,pacMan);	
	    				
	    		int distance = directionsGhost2PacMan.size();
	    		
	    		//if continue on path will i get closer to ghost? or further? if further continue if closer figure out next best move
	    		//maybe that is bad and that prediction of where the ghost will go should be included in the calculation of the best path
	    		
	    		
	    		/*
	    		for(Tile tile : directionsGhost2PacMan){
	        		System.out.println("x = " + tile.x + ", y = " + tile.y);
	        	}*/
	    		
	    		//double distance = pacMan.dist(ghostTile);//this comes out wrong
	
	    		//this avoids them but gets confused when they are blue
	    		int best = 0;
	    		int newDirection = -1;
	    	    System.out.println("Ghost Distance: " + distance);
	    	    if(distance < 8){
	    	    	if(directionsGhost2PacMan.size()>1){
	    	    		//check every direction pacman
	    	    		for (int i = 0; i < directions.length; i++) {
	    	    			Tile newPacMan = gs.ie.Tiles[pacMan.x + (int) directions[i].getX()][pacMan.y + (int) directions[i].getY()]; //are these the right way around? maybe directions is wrong?
	    	    			
	    	    			List<Tile> directionsGhost2NewPacMan = tools.getDirections(ghostTile,newPacMan);
	    	    			int curScore = directionsGhost2NewPacMan.size();
	    	    			System.out.println("Direction: " + i + " results in Ghost Distance: " + curScore);
	    	        	
		        			if (curScore > best) {
		                    	newDirection = i;
		                        best = curScore;
		                    }   
	    	            }
	    	    		newDirection += 1;
	    	    		direction = newDirection;
	    	    	}
	    	    }
	    	    
	    	}
    	}
        
        if(powerPill){
    		for (Point2D coord : gs.ie.edibleGhostLoc.values()) {//actually should record both as edible and none edible ghosts can be out at the same time
    			edibleGhostTile = gs.ie.Tiles[(int) coord.getX()/16][(int) coord.getY()/16];
    			List<Tile> directionsEdibleGhost2PacMan = tools.getDirections(ghostTile,pacMan);	
				
	    		int distance = directionsEdibleGhost2PacMan.size();
	    		if(distance<10){
	    			if(directionsEdibleGhost2PacMan.size()>1){
	    	    		if (directionsEdibleGhost2PacMan.get(1) != null){
	    	        		direction = pacMan.getDirection(directionsList.get(1));//gs.closestPillDir;
	    	        		System.out.println("direction = " + direction);
	    	        	}
	    	    	}
	    			chaseGhost
	    		} else { 
	    			go to closest pill
	    		}
    		}
    	}
        
        
        
        
        if (direction == gs.currentDirection) {
    		direction = NEUTRAL;
        } //else if (oppositeDirection == gs.currentDirection){
        	//direction = gs.currentDirection;
        //}
        
        return direction;


        
    }
    
    public int oppositeDirection(int direction){
    	int opposite = 0;
    	if(direction == UP){
        	opposite = DOWN;
        } else if (direction == RIGHT){
        	opposite= LEFT;
        } else if (direction == DOWN){
        	opposite = UP;
        } else if (direction == LEFT){
        	opposite = RIGHT;
        }
    	return opposite;
    	
    }
    
}
