package gr.pacman.ai;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import gr.pacman.game.GameState;
import gr.pacman.game.Grid;
import gr.pacman.game.PacMan;

public class TestAI {
	AITools tools;
	public TestAI() throws IOException, URISyntaxException {
		tools = new AITools();
	}
	public int getAction(GameState gs) throws Exception {
		double a = 1.6;
		double b = 1.3;
		System.out.println((int) a + " " + (int) b);
		
		
		
		PacMan clonePac = new PacMan(gs.pacman);
		System.out.println(gs.pacman.location.toString());
		System.out.println(clonePac.location.toString());
		clonePac.location.set(100,100);
		System.out.println(gs.pacman.location.toString());
		System.out.println(clonePac.location.toString());

		System.out.println(gs.pacman.mazeLocation.toString());
		clonePac.mazeLocation.x = 10000;
		System.out.println(gs.pacman.mazeLocation.toString());
		System.out.println(clonePac.mazeLocation.toString());
		
		
		
		SimState sim = new SimState(gs);
		System.out.println("-------Starting State-------");
		sim.printState();
		List<Grid> legalMoves = sim.pacman.mazeLocation.getLegalNeighbours();
		for (Grid move : legalMoves) {
			SimState next = new SimState(sim);
			next.generateState(4, move);
			System.out.println("-------Starting Move-------" + move.toString());
			next.printState();
        }

		
		
		/*
		sim.generateState(4, legalMoves.get(0));
		
			
		for(int i = 0; i<sim.ghosts.length;i++){
			List<Grid> ghostMoves = sim.ghosts[i].getLegalMoves();
    		sim.generateState(i, ghostMoves.get(0));
    		
		}
		System.out.println("-------End State-------");
		sim.printState();
		
		//System.out.println(sim.evaluateState());
		
		*/
		
		return 0;
	}
	

}
