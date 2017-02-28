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
		return null;
    }
	
	
	//dist(tile, tile)
	//dist(point x, point y, tile)
}
