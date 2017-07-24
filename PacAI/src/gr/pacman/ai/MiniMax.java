package gr.pacman.ai;

import java.util.List;

import gr.pacman.game.GameState;
import gr.pacman.game.Grid;

public class MiniMax {
	private int startDepth = 2;
	private static long availableTime = 20;

	public MiniMax() throws Exception {
	}
	
	public int getAction(GameState gs) throws Exception {	
		long startTime = System.currentTimeMillis();
		long endTime = startTime + availableTime;
		
		SimState sim = new SimState(gs);
		List<Grid> legalMoves = sim.pacman.getLegalMoves();
		
		Grid lastBest = null;
		for (int depth = startDepth; System.currentTimeMillis() < endTime; depth++) { 
			Grid bestMove = null;
			double bestScore = 0;
			for (Grid move : legalMoves) {
				SimState next = new SimState(sim);
				next.generateState(4, move);
				
				double score = getMinScoreAlphaBeta(next, 0, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, endTime);
				if (System.currentTimeMillis() > endTime) {
					break;
				}
				
				if (bestMove == null || score > bestScore) {
					bestMove = move;
					bestScore = score;
				}
			}
			if (System.currentTimeMillis() < endTime) {
				lastBest = bestMove;
			} 
		}	

		int direction = 0;
		if(lastBest!=null){
			direction = gs.pacman.mazeLocation.getDirection(lastBest);
		}
		
		return direction;	
		
	}
	
	/*
	 * Consider all possible moves by the ghosts
	 */
	private double getMinScoreAlphaBeta(SimState s, int gNum, int depth, double alpha, double beta, long endTime) throws Exception {	
		double res;
		if (depth == 0 || !s.getPacManAlive()) {
			res = s.evaluateState();
		} else {
			res = Double.POSITIVE_INFINITY;
			double score = 0;
			List<Grid> legalMoves = s.spawnedGhosts.get(gNum).getLegalMoves();
			for(Grid move : legalMoves){
				if (System.currentTimeMillis() > endTime) {
					return -1000;
				}
				SimState next = new SimState(s);
				next.generateState(gNum, move);
				
				if(gNum == next.spawnedGhosts.size()-1){
					score = getMaxScoreAlphaBeta(next, depth - 1, alpha, beta, endTime);
				}else{
					score = getMinScoreAlphaBeta(next, gNum+1, depth, alpha, beta, endTime);
				}
				
				res = Math.min(res, score);
				beta = Math.min(beta, score);
				if (beta <= alpha) {
					break;
				}
			}  		
		}
		return res;
	}
	
	/*
	 * Consider all possible moves by pacman
	 */
	private double getMaxScoreAlphaBeta(SimState s, int depth, double alpha, double beta, long endTime) throws Exception {
		double res;
		if (depth == 0 || !s.getPacManAlive()) {
			res = s.evaluateState();
		} else {
			List<Grid> legalMoves = s.pacman.getLegalMoves();
			res = Double.NEGATIVE_INFINITY;
			for (Grid move : legalMoves) {
				if (System.currentTimeMillis() > endTime) {
					return -1000;
				}
				SimState next = new SimState(s);
				next.generateState(4, move);
				
				double score = getMinScoreAlphaBeta(next, 0,  depth - 1, alpha, beta, endTime);
				res = Math.max(res, score);
				alpha = Math.max(alpha, score);
				if (beta <= alpha) {
					break;
				}
			}
		}
		return res;
	}
}

	
	

	

