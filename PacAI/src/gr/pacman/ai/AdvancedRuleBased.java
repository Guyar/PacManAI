package gr.pacman.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.pacman.game.GameState;
import gr.pacman.game.Ghost;
import gr.pacman.game.Grid;

public class AdvancedRuleBased {
	AITools tools;
	public AdvancedRuleBased() {
		tools = new AITools();
	}
	
	public int getAction(GameState gs){
		int direction = 0;
		List<Integer> legalDirections = gs.pacman.getLegalDirections();
	
		Map<Integer, Integer> availableDirections = new HashMap<Integer, Integer>();
		for(int legalDirection:legalDirections){
			availableDirections.put(legalDirection, 0);
		}
		
		Ghost nearestGhost = tools.getNearestNonEdibleGhost(gs.ghosts);
		Ghost nearestEdibleGhost = tools.getNearestEdibleGhost(gs.ghosts);
		
		//eat powerPill
		if(nearestGhost != null && gs.getPowerPillsLeft() > 0){
			if(gs.closestPowerPillDist<=7 && gs.closestPowerPillDist < nearestGhost.distanceFromPac && nearestGhost.distanceFromPac<15){
				if(gs.closestPowerPillDist<3){
					if(nearestGhost.mazeLocation.lookupTable.get(gs.closestPowerPill)>2 && nearestGhost.distanceFromPac>4){
						return gs.pacman.getOppositeDirection();
					}else{
						return tools.getMoveTowards(gs.pacman, gs.closestPowerPill);
					}
				}else{
					return tools.getMoveTowards(gs.pacman, gs.closestPowerPill);
				}
			}
		}
		
		//eat ghosts
		if(nearestEdibleGhost != null && nearestGhost == null && gs.energiserTimer>0.7 && gs.energiser && nearestEdibleGhost.distanceFromPac < 13){
			return tools.getMoveTowardsGhost(gs.pacman, nearestEdibleGhost);
		}
		
		//eat ghosts, if reachable
		if(nearestEdibleGhost != null && nearestGhost != null && gs.energiserTimer>0.7 && gs.energiser && nearestEdibleGhost.distanceFromPac < 10 && nearestGhost.distanceFromPac>(nearestEdibleGhost.distanceFromPac+1)){
			return tools.getMoveTowardsGhost(gs.pacman, nearestEdibleGhost);
		}
		
		//avoid Ghosts while favouring directions that bring it closer to power pills or normal pills
		if(nearestGhost != null && nearestGhost.distanceFromPac<7){
			for(Ghost ghost : gs.ghosts) {
				int directionOfGhostAttack;
				int danger;
				if(ghost.spawned && !ghost.edible){	
					List<Grid> path = tools.getGhostPath(ghost, gs.pacman.mazeLocation,10);
					if(path.get(path.size()-1)==gs.pacman.mazeLocation){
						danger = 10/path.size();
						if(path.size() == 1){
							directionOfGhostAttack = ghost.getOppositeDirection();
						} else {
							directionOfGhostAttack = gs.pacman.mazeLocation.getDirection(path.get(path.size()-2));	
						}
						
						if(danger > availableDirections.get(directionOfGhostAttack)){	
							availableDirections.put(directionOfGhostAttack, danger);
						}	
					}
				}
			}
			
			if(gs.closestPowerPill != null){
				List<Grid> path2PowerPill = tools.getPath(gs.pacman.mazeLocation,gs.closestPowerPill);
				int powerPillDirection = gs.pacman.mazeLocation.getDirection(path2PowerPill.get(1));
				availableDirections.put(powerPillDirection, availableDirections.get(powerPillDirection)-1);
			}else if(gs.closestPill!=null){
				List<Grid> path2Pill = tools.getPath(gs.pacman.mazeLocation,gs.closestPill);
				int pillDirection = gs.pacman.mazeLocation.getDirection(path2Pill.get(1));
				availableDirections.put(pillDirection, availableDirections.get(pillDirection)-1);
			}
			
			int best = 1000;		
			for(int legalDirection:legalDirections){
				if (availableDirections.get(legalDirection) < best) {
	            	direction = legalDirection;
	                best = availableDirections.get(legalDirection);
				}
			}
			return direction;
		}
		
		//eat fruit
		if(gs.fruitSpawned==true && gs.pacman.mazeLocation.lookupTable.get(gs.fruitMazeLocation)<10){
			return tools.getMoveTowards(gs.pacman, gs.fruitMazeLocation);
		}
		
		//eat closest pill
		if(gs.closestPill!=null){
			return tools.getMoveTowards(gs.pacman, gs.closestPill);
		}
		return direction;
	}
}


