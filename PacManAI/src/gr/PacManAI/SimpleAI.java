package gr.PacManAI;

import java.awt.geom.Point2D;


public class SimpleAI implements Constants{
    public int getAction(GameState gs) {


        int direction = gs.closestPillDir();
        System.out.println("Action choosen");
        //System.out.println(direction);
        return direction;


        
    }
    
}
