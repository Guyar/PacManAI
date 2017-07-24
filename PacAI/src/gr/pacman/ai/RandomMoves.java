package gr.pacman.ai;

import java.util.List;
import java.util.Random;

import gr.pacman.game.GameState;

public class RandomMoves {
	public Random rand;
	
	public RandomMoves() {
		rand = new Random();
	}
	
	/*
	 * Chooses a random move for pacman
	 */
	public int getAction(GameState gs) throws InterruptedException {
		List<Integer> posDirections = gs.pacman.getLegalDirectionsNoReverse();
		int i = rand.nextInt(posDirections.size());
		
		int direction = posDirections.get(i);
		Thread.sleep(20);
		return direction;
	}
}
