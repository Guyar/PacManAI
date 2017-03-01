package gr.PacManAI;

public class Tile {
	public int x, y;
	public type type;
	public Tile up;
	public Tile right;
	public Tile down;
	public Tile left;
	
	public Tile(int x, int y, type type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}
	public enum type {
		Pill, Power, Wall, Bkgnd
	}
	
	public Tile NextTo(int d)
    {
		if(d == 1){
			return up;
		} else if(d == 2){
			return right;
		} else if(d == 3){
			return down;
		} else if(d == 4){
			return left;
		}
		return null;//this may mess up with tunnel + other stuff
    }
	
	
	public int dist(Tile tile){
		return Math.abs(x-tile.x) + Math.abs(y-tile.y);
	}
	
	public double dist(double xcoord, double ycoord){
		xcoord = Math.floor(xcoord/16);
		ycoord = Math.floor(ycoord/16);
		return Math.abs(x-xcoord) + Math.abs(y-ycoord);
		
	}
	
}
