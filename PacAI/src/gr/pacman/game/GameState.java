package gr.pacman.game;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gr.pacman.game.Ghost.colour;
import gr.pacman.game.Grid.type;
import gr.pacman.utils.Constants;
import gr.pacman.utils.Point;

public class GameState implements Constants{
	Robot robot;
	private ImageExtractor ie;
	private Maze maze;
	public PacMan pacman;
	public Ghost red,pink,blue,orange;
	public Ghost[] ghosts = new Ghost[4];
	
	private ArrayList<Grid> pills,powerPills;
	public Grid fruitMazeLocation;
	public Grid[][] mazeTiles;
	
	public int lives,level,pillsLeft;
	
	private Rectangle mazeArea;
	
	public Grid closestPill,closestPowerPill;
	public double closestPillDist,closestPowerPillDist;	
	
	boolean mazeCreated;
	public double timer,energiserTimer;
	public long lastTime,dt;
	private long edibleTime;
	private boolean timerReset,pacmanMoved,ghostsMoved,pacManAlive;
	public boolean energiser;
	
	List<String> chaseTimes, edibleTimes;
	public String chaseStrategy;

	public boolean fruitSpawned;
	
	public int numEdibleGhosts;
	public int powerPillsLeft;
	private Point tmp;
	
	public GameState(Rectangle gameArea) throws Exception {	
		robot = new Robot();	
		
		this.chaseTimes = Files.readAllLines(Paths.get(getClass().getResource("/ChaseTimes.csv").toURI()));
		this.edibleTimes = Files.readAllLines(Paths.get(getClass().getResource("/EdibleTimes.csv").toURI()));
		
		this.mazeCreated = false;
		this.mazeArea = new Rectangle(gameArea.x, gameArea.y+48, MAZE_WIDTH_PIXELS, MAZE_HEIGHT_PIXELS);
		maze = new Maze(mazeArea);
		
		ie = new ImageExtractor(mazeArea);
		
		this.tmp = new Point(0,0);
		this.pacman = new PacMan();
		this.red = new Ghost(colour.Red);
		this.pink = new Ghost(colour.Pink);
		this.blue = new Ghost(colour.Blue);
		this.orange = new Ghost(colour.Orange);
		ghosts[0] = this.red;
		ghosts[1] = this.pink;
		ghosts[2] = this.blue;
		ghosts[3] = this.orange;
			
		closestPill = null;
		closestPowerPill = null;
		level = 0;
		lives = 5;
		pillsLeft = 0;
		powerPillsLeft = 0;
		timer = 0;
		timerReset = true;
		energiser = false;
		fruitSpawned = false;
		setPacManAlive(false);
    }
	
	public void update(String currentState) throws Exception {
		if(timerReset){
			lastTime = System.currentTimeMillis();
			timerReset = false;
			energiser = false;
		}else{
			dt = (System.currentTimeMillis()-lastTime);
			if(pacmanMoved && ghostsMoved){
				if(!energiser){
					timer += dt/1000.0f;
				}else {
					energiserTimer += dt/1000.0f;
					
					if(energiserTimer>edibleTime){
						energiser = false;
					}
				}
			} else{
				checkIfAlive();	
			}
		}
		lastTime = System.currentTimeMillis();
	
		BufferedImage bufferedImage = robot.createScreenCapture(mazeArea);
		
		ie.findPacmanGhosts(bufferedImage,ghosts,maze.getWallMask());
		numEdibleGhosts = ie.getNumEdibleGhosts();
		
		//System.out.println(pillsLeft);
		if(pillsLeft == 69 || pillsLeft == 169){
			//System.out.println("Looking for fruit");
			ie.findFruit(bufferedImage, level);
			setFruitLocation(ie.fruitCoords);
			fruitSpawned = true;
		}
		
		
		
		//Sets the ghosts current strategy based on time/i should change this so
		//it checks the time is near not if time runs out
		setChaseStrategy();
		
		updatePacMan(ie.pacCoordinates);
		updateGhosts(ie.ghostsCoords);

		updatePills(bufferedImage);
		updatePowerPills(bufferedImage);
		if(fruitSpawned){
			updateFruit(bufferedImage);
		}
	}

	private void updatePacMan(Point coord) {
		pacmanMoved = false;	
	
		double curx = coord.getX()/BLOCKSIZE;//test without floor
		double cury = coord.getY()/BLOCKSIZE;
		
		//coord.set(curx, cury);//replace this with tmp
		tmp.set(curx, cury);
		if(mazeTiles[(int) curx][(int) cury].type != type.Wall){
			pacman.setState(tmp, mazeTiles[(int) curx][(int) cury]);
		}

		if(!pacman.prevLocation.equals(pacman.location)){
			pacmanMoved = true;
		}
	}
	
	private void updateGhosts(Point[] ghostLoc) throws Exception {
		ghostsMoved = false;
		
		for(int i = 0; i < 4; i++){	
			if(ghostLoc[i] != null){// occasionally a ghost wont be found when it touches the wall of a maze in which case it will return null
				//converts the pixel coordinates to a tile
				double curx = ghostLoc[i].getX()/BLOCKSIZE;
				double cury = ghostLoc[i].getY()/BLOCKSIZE;
				
				//tmp.set(curx, cury);
				Point coord = new Point(curx, cury);//replace this with tmp
				
				if(mazeTiles[(int) curx][(int) cury].type != type.Wall){//fix for imageextractor returning wrong coordinates in certain situations
					ghosts[i].setState(coord, mazeTiles[(int) curx][(int) cury]);
				}
				
				if(!ghosts[i].prevLocation.equals(ghosts[i].location)){
					ghostsMoved = true;
				}
				
				ghosts[i].strategy = chaseStrategy;		
				
				if(inSpawn(ghosts[i].mazeLocation)){
					if(ghosts[i].edible && ghosts[i].spawned){//been eaten
						//ghosts[i].edible = false;
					}
					ghosts[i].setSpawned(false);
					ghosts[i].distanceFromPac = Double.POSITIVE_INFINITY;
				} else {
					if(ghosts[i].mazeLocation.lookupTable.get(pacman.mazeLocation)!=null){
						ghosts[i].distanceFromPac = ghosts[i].mazeLocation.lookupTable.get(pacman.mazeLocation);
					}
					ghosts[i].setSpawned(true);		
				}
			}
		}
		
		if(chaseStrategy == "Chase" && orange.mazeLocation.dist(pacman.mazeLocation)<=8){
			orange.strategy = "Scatter";
		}
	}
	
	//If none of the agents have moved check which agent has been killed
	private void checkIfAlive() {
		if((!pacmanMoved && !ghostsMoved)){
			Ghost closestGhost = null;
			double best = Double.POSITIVE_INFINITY;
			for(Ghost ghost:ghosts){
				//find the closest ghost
				if(ghost.distanceFromPac < best){
					best = ghost.distanceFromPac;
					closestGhost = ghost;
				}
			}
			double exactDist = closestGhost.location.dist(pacman.location);
			if(!closestGhost.edible && exactDist<=1.5){
				System.out.println("Pacman is dead");
				setPacManAlive(false);
				timerReset = true;
				timer = 0;
			}else if(closestGhost.edible && exactDist<=1.5){
				
				closestGhost.distanceFromPac = 100;
				
				closestGhost.setState(mazeTiles[14][14]);
				//closestGhost.setEdible(false);
				System.out.println("edibleGhost Removed");
			}	
		}
	}

	private void updatePills(BufferedImage im) {
		for (Iterator<Grid> iterator = pills.iterator(); iterator.hasNext();) {
		    Grid pill = iterator.next();
		    int centerX = (pill.x*BLOCKSIZE)+7;
			int centerY = (pill.y*BLOCKSIZE)+7; 		
			int rgb = im.getRGB(centerX,centerY);
			
			if(pacman.mazeLocation == pill || rgb == BKGND_COLOUR || rgb == PAC_COLOUR){
				pillsLeft-=1;	
				pill.type = type.Bkgnd;
				iterator.remove();
			} else {
				updateClosestPill(pill);
			}		
		}	
	}

	private void updatePowerPills(BufferedImage im) {
		for (Iterator<Grid> iterator = powerPills.iterator(); iterator.hasNext();) {
		    Grid powerPill = iterator.next();
		    int centerX = (powerPill.x*BLOCKSIZE)+7;
			int centerY = (powerPill.y*BLOCKSIZE)+7;
			
			int rgb = im.getRGB(centerX,centerY);
			
			if(rgb == PAC_COLOUR){
				powerPillsLeft-=1;
				pillsLeft-=1;
				powerPill.type = type.Bkgnd;
				iterator.remove();
				energiser = true;
				energiserTimer = 0;
				setGhostsEdible(true);
			}else{
				updateClosestPowerPill(powerPill);
			}
		}
	}
	
	private void updateClosestPill(Grid pill) {
		//double dist = pill.distManhatton(pacman.location);
		//int dist = maze.lookupTable.get(pill).get(pacman.mazeLocation);
		int dist = pill.lookupTable.get(pacman.mazeLocation);
		if (closestPill == null) {	
			closestPill = pill;
			closestPillDist = dist;
	    } else if (dist < closestPillDist) {
	    	closestPill = pill;//checks if the pill is closer than the current closest pill 
	    	closestPillDist = dist;
	    }
	}

	private void updateClosestPowerPill(Grid powerPill) {
		//double dist = powerPill.distManhatton(pacman.mazeLocation);	
		//int dist = maze.lookupTable.get(powerPill).get(pacman.mazeLocation);
		int dist = powerPill.lookupTable.get(pacman.mazeLocation);
		if (closestPowerPill == null) {	
			closestPowerPill = powerPill;
			closestPowerPillDist = dist;
	    } else if (dist < closestPowerPillDist) {   
	    	closestPowerPill = powerPill;//checks if the pill is closer than the current closest pill 
	    	closestPowerPillDist = dist;
	    }
	}
	
	private void updateFruit(BufferedImage im) {
	    int centerX = (fruitMazeLocation.x*BLOCKSIZE)+7;
		int centerY = (fruitMazeLocation.y*BLOCKSIZE)+7; 		
		int rgb = im.getRGB(centerX,centerY);
		
		if(rgb == BKGND_COLOUR){
			fruitSpawned = false;
		}
	}
	
	private void setFruitLocation(Point coord) {
		double curx = Math.floor(coord.getX()/BLOCKSIZE);
		double cury = Math.floor(coord.getY()/BLOCKSIZE);
		fruitMazeLocation = mazeTiles[(int) curx][(int) cury];
	}

	public void initialiseNewLevel(){
		level += 1;

		maze.createMaze();
		maze.surroundingTiles();
	    maze.printMaze();
	    //if(lookupTableCreated == false){
	    	maze.createLookupTable();
	    	System.out.println("done");
	    	//lookupTableCreated = true;	
	    //}

	    mazeTiles = maze.Tiles;
	    pills = maze.pills;
	    powerPills = maze.powerPills;
	    
	    pillsLeft = maze.pills.size() + maze.powerPills.size();
	    powerPillsLeft = maze.powerPills.size();
	    timer = 0;
		timerReset = true;
		mazeCreated = true;
		fruitSpawned = false;
		
		setGhostEdibleTime();
	}
	
	public void initialiseAgents(){
		//pacman.setMazeLoc(mazeTiles[14][23]);
	    //red.setMazeLoc(mazeTiles[13][11]);
		//pink.setMazeLoc(mazeTiles[14][14]);
		//blue.setMazeLoc(mazeTiles[12][14]);
		//orange.setMazeLoc(mazeTiles[16][14]);
		red.scatterTarget = mazeTiles[24][3];
		pink.scatterTarget = mazeTiles[3][3];
		blue.scatterTarget = mazeTiles[24][28];
		orange.scatterTarget = mazeTiles[3][28];
		
		Grid ghostSpawnLocation = mazeTiles[14][14];
		red.spawnLocation = ghostSpawnLocation;
		pink.spawnLocation = ghostSpawnLocation;
		blue.spawnLocation = ghostSpawnLocation;
		orange.spawnLocation = ghostSpawnLocation;
		
		red.strategy = "Scatter";
		pink.strategy = "Scatter";
		blue.strategy = "Scatter";
		orange.strategy = "Scatter";
		red.direction = LEFT;
		pink.direction = LEFT;
		blue.direction = LEFT;
		orange.direction = LEFT;
		pacman.direction = LEFT;
	}

	private void setGhostEdibleTime() {
		if(level >= 21){
			level = 21;
		}
		String line = edibleTimes.get(level);
		String[] times = line.split(",");
		edibleTime = Long.parseLong(times[1]);
	}

	private void setChaseStrategy() {
		if(level >= 5){
			level = 5;
		}
		String line = chaseTimes.get(level);
		String[] times = line.split(",");
		long time = 0;
		for(int i = 1;i<8;i++){
			time = Long.parseLong(times[i]);
			if(timer < time){//timer/1000.0f
				if(i%2==0){
					chaseStrategy = "Chase";
					return;
				}else{
					chaseStrategy = "Scatter";
					return;
				}
			}
		}
		chaseStrategy = "Chase";
		return;
	}
	
    public boolean inSpawn(Grid ghost) {
    	int tileX = ghost.x;
    	int tileY = ghost.y;
    	return 10 < tileX && tileX < 17 && 12 <= tileY && tileY < 16;
    }
    
	private void setGhostsEdible(boolean edible) {
		for(Ghost ghost:ghosts){
			ghost.edible = edible;
		}	
	}

	public Grid getPacManLoc() {
		return pacman.mazeLocation;
	}
    
    public boolean getPacManAlive() {
		return pacManAlive;
	}

	public void setPacManAlive(boolean pacManAlive) {
		this.pacManAlive = pacManAlive;
	}
	
	public void reset() {
		closestPill = null;
		closestPillDist = Double.POSITIVE_INFINITY;
		
		closestPowerPill = null;
		closestPowerPillDist = Double.POSITIVE_INFINITY;
	}
	
	public int getPillsLeft() {
		return pills.size();
	}

	public int getPowerPillsLeft() {
		return powerPills.size();
	}
	/*
	public HashMap<Grid, HashMap<Grid, Integer>>  getLookupTable() {
		return maze.lookupTable;
	}*/
}
