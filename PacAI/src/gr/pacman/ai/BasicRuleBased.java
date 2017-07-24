package gr.pacman.ai;

import java.io.IOException;
import java.net.URISyntaxException;

import gr.pacman.game.GameState;
import gr.pacman.game.Ghost;

public class BasicRuleBased {
	AITools tools;
	public BasicRuleBased() throws IOException, URISyntaxException {
		tools = new AITools();
	}
	
	public int getAction(GameState gs){	
		//avoid ghosts
		Ghost nearestGhost = tools.getNearestNonEdibleGhost(gs.ghosts);
		if(nearestGhost != null && nearestGhost.distanceFromPac < 10){
			return tools.getMoveAwayFromGhost(gs.pacman, nearestGhost);
		}
		
		//chase edible ghosts
		nearestGhost = tools.getNearestEdibleGhost(gs.ghosts);
		if(nearestGhost != null && nearestGhost.distanceFromPac < 10){
			return tools.getMoveTowardsGhost(gs.pacman, nearestGhost);
		}

		//eat powerPill
		if(gs.getPowerPillsLeft() > 0 && !gs.energiser && gs.closestPowerPillDist < 10){
			return tools.getMoveTowards(gs.pacman, gs.closestPowerPill);
		}

		//collect fruit
		if(gs.fruitSpawned && gs.fruitMazeLocation.dist(gs.pacman.mazeLocation)<10){
			return tools.getMoveTowards(gs.pacman, gs.fruitMazeLocation);
		}

		//collect pills
		return tools.getMoveTowards(gs.pacman, gs.closestPill);
	}
}


