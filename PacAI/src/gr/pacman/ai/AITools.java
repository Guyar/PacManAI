package gr.pacman.ai;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import gr.pacman.game.Grid;
import gr.pacman.utils.Constants;
import gr.pacman.game.PacMan;
import gr.pacman.game.Ghost;

public class AITools implements Constants{
	
	public AITools() {
	}
	
	/*
	 * Finds path using dijkstra
	 */
    public List<Grid> getPath(Grid start, Grid finish) {
    	Map<Grid, Boolean> vis = new HashMap<Grid, Boolean>();

	    Map<Grid, Grid> prev = new HashMap<Grid, Grid>();
    	
    	List<Grid> directions = new LinkedList<Grid>();
        Queue<Grid> q = new LinkedList<Grid>();
        Grid current = start;
        q.add(current);
        vis.put(current, true);
        while(!q.isEmpty()){
            current = q.remove();
            
            if (current.equals(finish)){
                break;
            }else{
                for(Grid Grid : current.getLegalNeighbours()){
            		if(!vis.containsKey(Grid)){
                        q.add(Grid);
                        vis.put(Grid, true);
                        prev.put(Grid, current);
                    }
                }
            }
        }
        if (!current.equals(finish)){
            System.out.println("can't reach destination");
        }
        
        for(Grid Grid = finish; Grid != null; Grid = prev.get(Grid)) {
            directions.add(Grid);
    			
        }
        Collections.reverse(directions);	
        
        return directions;
    }
    
	/*
	 * Finds path using ghosts path finding method
	 */
	public List<Grid> getGhostPath(Ghost ghost, Grid target, int pathLength) {
		Ghost tempGhost = ghost.clone();
		
		List<Grid> path = new LinkedList<Grid>();
		
		Grid nextTile = null;
		Grid current = tempGhost.mazeLocation;
		path.add(current);
		for(int i= 0; i < pathLength; i++){	
			double best = Double.POSITIVE_INFINITY;
			if(current == target){
				return path;
			}
			
			List<Grid> legalMoves = tempGhost.getLegalMoves();

			for(Grid move : legalMoves){	
				double curScore = move.dist(target);
				if (curScore < best) {
                	nextTile = move;
                    best = curScore;
                } 		  
			}
			tempGhost.setState(nextTile);
			path.add(nextTile);
			current = nextTile;	
		}
		return path;
	}
	
	/*
	 * Returns the nearest ghost to pacman
	 */
	public Ghost getNearestGhost(Ghost[] ghosts) {
		double best = Double.POSITIVE_INFINITY;
		Ghost closestGhost = null;
		for(Ghost ghost:ghosts){
			if(ghost.spawned){
				if(ghost.distanceFromPac < best){
					closestGhost = ghost;
					best = ghost.distanceFromPac;
				}
			}
		}
		return closestGhost;
	}
	
	/*
	 * Returns the nearest non edible ghost to pacman
	 */
	public Ghost getNearestNonEdibleGhost(Ghost[] ghosts) {
		double best = Double.POSITIVE_INFINITY;
		Ghost closestGhost = null;
		for(Ghost ghost:ghosts){
			if(ghost.spawned && !ghost.edible){
				if(ghost.distanceFromPac < best){
					closestGhost = ghost;
					best = ghost.distanceFromPac;
				}
			}
		}
		return closestGhost;
	}
	
	/*
	 * Returns the nearest edible ghost to pacman
	 */
	public Ghost getNearestEdibleGhost(Ghost[] ghosts) {
		double best = Double.POSITIVE_INFINITY;
		Ghost closestGhost = null;
		for(Ghost ghost:ghosts){
			if(ghost.spawned && ghost.edible){
				if(ghost.distanceFromPac < best){
					closestGhost = ghost;
					best = ghost.distanceFromPac;
				}
			}
		}
		return closestGhost;
	}
	
	/*
	 * Returns the direction that thats away from ghost
	 */
	public int getMoveAwayFromGhost(PacMan pac, Ghost nearestGhost) {
		List<Grid> ghostPath2Pac = getGhostPath(nearestGhost,pac.mazeLocation,15);
		int pathSize = ghostPath2Pac.size();
		int directionOfGhostAttack = 0;
		
		if(pathSize == 1){
			return pac.direction;
		} else {
			directionOfGhostAttack = pac.mazeLocation.getDirection(ghostPath2Pac.get(ghostPath2Pac.size()-2));
		}
		
		for(int legalDirection : pac.getLegalDirections()){
			if(legalDirection != directionOfGhostAttack){
				return legalDirection;
			}
		}
		return 0;
	}
	
	/*
	 * Returns the direction towards ghost
	 */
	public int getMoveTowardsGhost(PacMan pac, Ghost nearestGhost) {
		List<Grid> Path2Ghost = getPath(pac.mazeLocation,nearestGhost.mazeLocation);
		int pathSize = Path2Ghost.size();
		int directionOfGhost = 0;
		
		if(pathSize == 1){
			directionOfGhost = nearestGhost.direction;
			return pac.direction;
		} else {
			directionOfGhost = pac.mazeLocation.getDirection(Path2Ghost.get(1));
		}
		return directionOfGhost;
	}
	
	/*
	 * Returns the direction thats towards a target
	 */
	public int getMoveTowards(PacMan pac, Grid target) {
		List<Grid> Path2Ghost = getPath(pac.mazeLocation,target);
		int pathSize = Path2Ghost.size();
		if(pathSize == 1){
			return pac.direction;
		}
		
		return pac.mazeLocation.getDirection(Path2Ghost.get(1));
	}
}
