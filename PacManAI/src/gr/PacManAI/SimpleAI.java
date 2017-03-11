package gr.PacManAI;

import java.util.List;

public class SimpleAI implements Constants{
	AITools tools;

	public SimpleAI(){
		tools = new AITools();
	}
	
    public int getAction(GameState gs) {
    	Tile pacMan = gs.ie.Tiles[(int) gs.pacManLoc.getX()][(int) gs.pacManLoc.getY()];

    	System.out.println();
    	List<Tile> directionsList = tools.getDirections(pacMan, gs.closestPill);
    	System.out.println("directions from " +  pacMan.x + " " + pacMan.y +" to " + gs.closestPill.x + " " + gs.closestPill.y);
    	for(Tile tile : directionsList){
    		System.out.println("x = " + tile.x + " y = " + tile.y);
    	}
    	
    	//how do i choose the next direction?
    	//ghosts need to use the euclidean path?! uh oh.. wwait i can just do use euclidean in the path calc rather than manhatton?
    	
        int direction = gs.closestPillDir;

        //System.out.println(gs.currentDirection);
        //System.out.println(direction);
        //System.out.println("oppositeDirection: " + oppositeDirection);
        
        if (direction == gs.currentDirection) {
    		direction = NEUTRAL;
        } //else if (oppositeDirection == gs.currentDirection){
        	//direction = gs.currentDirection;
        //}
        //int direction = 1;
        //System.out.println("Action choosen");
        //System.out.println(direction);
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
