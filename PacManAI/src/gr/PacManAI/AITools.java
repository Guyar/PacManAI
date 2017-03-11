package gr.PacManAI;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import gr.PacManAI.Tile.type;
	

public class AITools {
	public AITools() {
		
	}

    public List<Tile> getDirections(Tile start, Tile finish){
    	//System.out.println("directions from " +  start.x + " " + start.y +" to " + finish.x + " " + finish.y);
		Map<Tile, Boolean> vis = new HashMap<Tile, Boolean>();

	    Map<Tile, Tile> prev = new HashMap<Tile, Tile>();
    	
    	List<Tile> directions = new LinkedList<Tile>();
        Queue<Tile> q = new LinkedList<Tile>();
        Tile current = start;
        q.add(current);
        vis.put(current, true);
        while(!q.isEmpty()){
            current = q.remove();
            if (current.equals(finish)){
                break;
            }else{
                for(Tile tile : current.getNeighbours()){
                	//System.out.println("X = " + tile.x + " y = " + tile.y);
                	if(tile.type != type.Wall){
                		if(!vis.containsKey(tile)){
                            q.add(tile);
                            vis.put(tile, true);
                            prev.put(tile, current);
                        }
                	}
                    
                }
            }
        }
        if (!current.equals(finish)){
            System.out.println("can't reach destination");
        }
        
        for(Tile tile = finish; tile != null; tile = prev.get(tile)) {
            directions.add(tile);
        }
        Collections.reverse(directions);
        
        
        for(Tile tile : directions){
    		System.out.println("x = " + tile.x + ", y = " + tile.y);
    	}
    	
        
        return directions;
    }
}
