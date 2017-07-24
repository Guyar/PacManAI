package gr.pacman.ai;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gr.pacman.game.GameState;
import gr.pacman.game.Ghost;
import gr.pacman.game.Grid;
import gr.pacman.game.PacMan;
import gr.pacman.game.Ghost.colour;
import gr.pacman.utils.Constants;
import gr.pacman.game.Grid.type;

public class SimState implements Constants{	
	private AITools tools;
	
	public PacMan pacman;
	public Ghost[] ghosts;
	public Ghost red,pink,blue,orange;
	private int pillsLeft;
	private Grid[][] tiles;
	private ArrayList<Grid> pills,powerPills;
    
	private double score;
	private boolean pacManAlive;
	private int level,ghostsEaten,pillsEaten,powerPillsEaten,fruitEaten;
	private boolean energiser;

	private Grid fruitLocation;
	private boolean fruitSpawned;

	List<String> pacManSpeeds,ghostSpeeds;
	ArrayList<Ghost> spawnedGhosts;
	ArrayList<Ghost> spawnedCloseGhosts;
	private Grid closestPill,closestPowerPill;
	private double closestPillDist;
	private double closestPowerPillDist;
   
	public SimState(GameState gs) throws Exception{
		tools = new AITools();
		pacManSpeeds = Files.readAllLines(Paths.get(getClass().getResource("/PacManSpeeds.csv").toURI()));
		ghostSpeeds = Files.readAllLines(Paths.get(getClass().getResource("/GhostSpeeds.csv").toURI()));
		
		tiles = new Grid[MAZE_WIDTH][MAZE_HEIGHT]; 
		pills = new ArrayList<Grid>(); 
		powerPills = new ArrayList<Grid>(); 
		for (int x = 0; x < MAZE_WIDTH; x++) {//number of tiles in x direction 
        	for(int y = 0; y < MAZE_HEIGHT; y++) {//number of tiles in y direction
        		tiles[x][y] = gs.mazeTiles[x][y].clone();
        		if(tiles[x][y].type == type.Pill){
        			pills.add(tiles[x][y]);
        		}else if(tiles[x][y].type == type.Power){
        			powerPills.add(tiles[x][y]);
        		}
        	}
		}

    	fruitEaten = 0;
		pillsEaten = 0;
		ghostsEaten = 0;
		powerPillsEaten = 0;
		pacManAlive = true;
		
		score = 0;
		
		energiser = gs.energiser;
		level = gs.level;
		fruitLocation = gs.fruitMazeLocation;
		fruitSpawned = gs.fruitSpawned;
		closestPill = gs.closestPill.clone();
		closestPillDist = gs.closestPillDist;
		closestPowerPill = gs.closestPowerPill.clone();
		closestPowerPillDist = gs.closestPowerPillDist;
		pacman = gs.pacman.clone();
    	red = gs.red.clone();
		pink = gs.pink.clone();
		blue = gs.blue.clone();
		orange = gs.orange.clone();
		ghosts = new Ghost[4];
		ghosts[0] = red;
		ghosts[1] = pink;
		ghosts[2] = blue;
		ghosts[3] = orange;
		spawnedGhosts = new ArrayList<Ghost>();
		for(Ghost spawnedGhost:ghosts){
        	if(spawnedGhost.spawned){
        		spawnedGhosts.add(spawnedGhost);
        	}
        }
    	
		pillsLeft = pills.size();	
	}

	public SimState(SimState sim) throws Exception {
		tools = new AITools();
		pacManSpeeds = Files.readAllLines(Paths.get(getClass().getResource("/PacManSpeeds.csv").toURI()));
		ghostSpeeds = Files.readAllLines(Paths.get(getClass().getResource("/GhostSpeeds.csv").toURI()));
		
		tiles = new Grid[MAZE_WIDTH][MAZE_HEIGHT]; 
		pills = new ArrayList<Grid>(); 
		powerPills = new ArrayList<Grid>(); 
		for (int x = 0; x < MAZE_WIDTH; x++) {//number of tiles in x direction 
        	for(int y = 0; y < MAZE_HEIGHT; y++) {//number of tiles in y direction
        		tiles[x][y] = new Grid(sim.tiles[x][y]);
        		if(tiles[x][y].type == type.Pill){
        			pills.add(tiles[x][y]);
        		}else if(tiles[x][y].type == type.Power){
        			powerPills.add(tiles[x][y]);
        		}
        	}
		}
		for (int x = 0; x < MAZE_WIDTH; x++) {//number of tiles in x direction 
        	for(int y = 0; y < MAZE_HEIGHT; y++) {//number of tiles in y direction
        		
        		if(y > 0) {
        			tiles[x][y].up = tiles[x][y-1];
        		}
        		if( x < MAZE_WIDTH-1){
        			tiles[x][y].right = tiles[x+1][y];
        		}
        		if( y < MAZE_HEIGHT-1){
        			tiles[x][y].down = tiles[x][y+1];
        		}
        		if( x > 0){
        			tiles[x][y].left = tiles[x-1][y];
        		}	
        	}
    	}
    	
    	//create tunnels
    	tiles[0][14].left = tiles[27][14];
    	tiles[27][14].right = tiles[0][14];
    	
    	fruitEaten = sim.fruitEaten;
		pillsEaten = sim.pillsEaten;
		powerPillsEaten = sim.powerPillsEaten;
		pacManAlive = sim.pacManAlive;
		ghostsEaten = sim.ghostsEaten;
		score = sim.score;
		
		energiser = sim.energiser;
		level = sim.level;
		fruitLocation = sim.fruitLocation;
		fruitSpawned = sim.fruitSpawned;
		closestPill = tiles[sim.closestPill.x][sim.closestPill.y];
		closestPillDist = Double.POSITIVE_INFINITY;
		closestPowerPill = tiles[sim.closestPowerPill.x][sim.closestPowerPill.y];
		closestPowerPillDist = Double.POSITIVE_INFINITY;
		pacman = new PacMan(sim.pacman);
		red = new Ghost(sim.red);
		pink = new Ghost(sim.pink);
		blue = new Ghost(sim.blue);
		orange = new Ghost(sim.orange);
		ghosts = new Ghost[4];
		ghosts[0] = red;
		ghosts[1] = pink;
		ghosts[2] = blue;
		ghosts[3] = orange;
		spawnedGhosts = new ArrayList<Ghost>();
		for(Ghost spawnedGhost:ghosts){
        	if(spawnedGhost.spawned){
        		spawnedGhosts.add(spawnedGhost);
        	}
        }

		pillsLeft = pills.size();
	}
	
	public void generateState(int agent,Grid move){
		if(agent == 4){//pacman
			updatePacman(move);
			updatePills();
			if(fruitSpawned){
				updateFruit();
			}
		}else{//ghosts
			updateGhost(agent,move);
		}
	}
	
	private void updatePills(){
		for (Iterator<Grid> iterator = pills.iterator(); iterator.hasNext();) {
		    Grid pill = iterator.next();
			if(pill.equals(pacman.mazeLocation)){
				pillsEaten +=1;
				iterator.remove();
			}else{
				updateClosestPill(pill);
			}
		}
		for (Iterator<Grid> iterator = powerPills.iterator(); iterator.hasNext();) {
		    Grid powerPill = iterator.next();
			if(powerPill.equals(pacman.mazeLocation)){
				powerPillsEaten +=1;
				setGhostsEdible(true);
				energiser = true;
				iterator.remove();
			}else{
				updateClosestPowerPill(powerPill);
			}
		}
	}
	
	private void updateClosestPill(Grid pill) {
		
		double dist = pill.distManhatton(pacman.location);
		
		if (closestPill == null) {	
			closestPill = pill;
			closestPillDist = dist;
	    } else if (dist < closestPillDist) {
	    	closestPill = pill;//checks if the pill is closer than the current closest pill 
	    	closestPillDist = dist;
	    }
	}
	
	private void updateClosestPowerPill(Grid powerPill) {
		double dist = powerPill.distManhatton(pacman.mazeLocation);	
		if (closestPowerPill == null) {	
			closestPowerPill = powerPill;
			closestPowerPillDist = dist;
	    } else if (dist < closestPowerPillDist) {   
	    	closestPowerPill = powerPill;//checks if the pill is closer than the current closest pill 
	    	closestPowerPillDist = dist;
	    }
	}
	
	private void updateFruit(){
		if(fruitLocation.equals(pacman.mazeLocation)){
			fruitEaten +=1;
			fruitSpawned = false;
		}
	}
	
	private void updatePacman(Grid m){
		pacman.speed = getPacmanSpeed(pacman.mazeLocation);
		pacman.direction = pacman.mazeLocation.getDirection(m);

		if(pacman.direction == UP){
			pacman.location.set(pacman.location.getX(), pacman.location.getY() - pacman.speed);
		}else if(pacman.direction == RIGHT){
			pacman.location.set(pacman.location.getX() + pacman.speed, pacman.location.getY());
		}else if(pacman.direction == DOWN){
			pacman.location.set(pacman.location.getX(), pacman.location.getY() + pacman.speed);
		}else if(pacman.direction == LEFT){
			if(pacman.location.getX() - pacman.speed < 0){
				pacman.location.set(MAZE_WIDTH + (pacman.location.getX() - pacman.speed), pacman.location.getY());
			}else{
				pacman.location.set(pacman.location.getX() - pacman.speed, pacman.location.getY());
			}
		}

		pacman.setState(pacman.location, tiles[(int) pacman.location.getX()][(int) pacman.location.getY()]);
	}
	
	private void updateGhost(int agent, Grid move){	
		Ghost ghost = spawnedGhosts.get(agent);
		ghost.speed = getGhostSpeed(ghost);

		int direction = ghost.mazeLocation.getDirection(move);

		if(direction == UP){
			ghost.location.set(ghost.location.getX(), ghost.location.getY() - ghost.speed);
		}else if(direction == RIGHT){
			ghost.location.set((ghost.location.getX() + ghost.speed) % MAZE_WIDTH, ghost.location.getY());
		}else if(direction == DOWN){
			ghost.location.set(ghost.location.getX(), ghost.location.getY() + ghost.speed);
		}else if(direction == LEFT){
			if(ghost.location.getX() - ghost.speed < 0){
				ghost.location.set(MAZE_WIDTH + (ghost.location.getX() - ghost.speed), ghost.location.getY());
			}else{
				ghost.location.set(ghost.location.getX() - ghost.speed, ghost.location.getY());
			}
		}

		ghost.setMazeLoc(tiles[(int) ghost.location.getX()][(int) ghost.location.getY()]);

		List<Grid> path2Pac = tools.getPath(tiles[ghost.mazeLocation.x][ghost.mazeLocation.y],tiles[pacman.mazeLocation.x][pacman.mazeLocation.y]);//fix this
		ghost.distanceFromPac = path2Pac.size();
		
		if(ghost.edible && ghost.mazeLocation.equals(pacman.mazeLocation)){
			ghostsEaten += 1;
			ghost.setEdible(false);
			ghost.setMazeLoc(tiles[13][14]);
		}else if(!ghost.edible && ghost.mazeLocation.equals(pacman.mazeLocation)){
			pacManAlive = false;
		}
	}
	
	private double getGhostSpeed(Ghost ghost){
		if(level >= 21){
			level = 21;
		}
		String line = ghostSpeeds.get(level);
		String[] speeds = line.split(",");
		
		double speed = 0;
		Double elroy1 = Double.parseDouble(speeds[4]);	
		Double elroy2 = Double.parseDouble(speeds[6]);	
		if(ghost.colour == colour.Red && !ghost.edible && pillsLeft <= elroy1){
			if(pillsLeft <= elroy2){
				speed = Double.parseDouble(speeds[7]);	
			} else {
				speed = Double.parseDouble(speeds[5]);	
			}
			
		}else{
			if(ghost.edible){
				speed = Double.parseDouble(speeds[3]);	
			} else if(ghost.mazeLocation.tunnel){
				speed = Double.parseDouble(speeds[2]);	
			} else {
				speed = Double.parseDouble(speeds[1]);
			}
		}
		return speed;
	}

	private double getPacmanSpeed(Grid pacLoc) {
		if(level >= 21){
			level = 21;
		}
		String line = pacManSpeeds.get(level);
		String[] speeds = line.split(",");
		
		double speed = 0;	
		if(energiser){
			if(pacLoc.type == type.Bkgnd){
				speed = Double.parseDouble(speeds[3]);	
			} else if(pacLoc.type == type.Pill){
				speed = Double.parseDouble(speeds[4]);
			}
		} else {
			if(pacLoc.type == type.Bkgnd){
				speed = Double.parseDouble(speeds[1]);	
			} else if(pacLoc.type == type.Pill){
				speed = Double.parseDouble(speeds[2]);
			}
		}
		return speed;
	}
	
	public double evaluateState() {
		score = 0;

		Ghost closestGhost = tools.getNearestGhost(ghosts);

		if(!closestGhost.edible){
			score += closestGhost.distanceFromPac*5;
		}else{
			score -= closestGhost.distanceFromPac*4;
		}
		
		List<Grid> path2Pill = tools.getPath(tiles[pacman.mazeLocation.x][pacman.mazeLocation.y],tiles[closestPill.x][closestPill.y]);
		List<Grid> path2PowerPill = tools.getPath(tiles[pacman.mazeLocation.x][pacman.mazeLocation.y],tiles[closestPowerPill.x][closestPowerPill.y]);
		score -= path2Pill.size()*2;
		score += path2PowerPill.size()*2.5;
		score += ghostsEaten*100;
		score += pillsEaten*10;
		score += fruitEaten*50;
		score += powerPillsEaten*50;
		
		if(!pacManAlive){
			score -= 200;
		}
		return score;
	}
	
	public void printState(){
		System.out.println("Agents maze Locations:");
		System.out.println("	Pacmans: " + pacman.mazeLocation.toString() + " " + pacman.location.toString() + ", pacmans speed: " + pacman.speed );
		System.out.println("	pacmans current direction: " + pacman.direction + " prev: " + pacman.prevDirection);
		System.out.println("	Red: " + red.mazeLocation.toString() + " " + red.location.toString() + ", reds speed: " + red.speed);
		System.out.println("	Pink: " + pink.mazeLocation.toString() + " " + pink.location.toString() + ", pinks speed: " + pink.speed);
		System.out.println("	Blue: " + blue.mazeLocation.toString() + " " + blue.location.toString() + ", blues speed: " + blue.speed);
		System.out.println("	Orange: " + orange.mazeLocation.toString() + " " + orange.location.toString() + ", oranges speed: " + orange.speed);
		System.out.println("Other State info:");
		System.out.println("	pills eaten: " + pillsEaten);
		System.out.println("	pill Distance: " + closestPillDist);
		System.out.println("	closest pill: " + closestPill.toString());
		System.out.println("	game not over: " + pacManAlive);
		System.out.println("	Ghosts Eaten: " + ghostsEaten);
		System.out.println("	Fruit Spawned: " + fruitSpawned);
		if(fruitSpawned){
			System.out.println("	Fruit location: " + fruitLocation.toString());
			
		}
		System.out.println("	Fruit Eaten: " + fruitEaten);
	}
	
	private void setGhostsEdible(boolean edible) {
		for(Ghost ghost:ghosts){
			ghost.edible = edible;
		}	
	}
	
	public boolean getPacManAlive() {
		return pacManAlive;
	}
}
