package gr.pacman.utils;

public class Point implements Cloneable,Constants{
    private double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public double scalarProduct(Point p) {
        return x * p.x + y * p.y;
    }
    
    public int getDirection(Point p) {
    	int direction = 0;
    	double dx = p.x - this.x;
		double dy = p.y - this.y;
		
    	double ang = (Math.toDegrees(Math.atan2(dy,dx)));
		if(ang < 0){
	        ang += 360;
	    }
		long dir = (Math.round(ang/90));
		
	    if (dir == 0) {
	    	direction = RIGHT;
	    }else if (dir == 1) {
	    	direction = DOWN;
	    } else if (dir == 2) {
	    	direction = LEFT;
	    } else if (dir == 3) {
	    	direction = UP;
	    }
	    return direction;
    }


    public void set(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public void set(double xcoord, double ycoord) {
        this.x = xcoord;
        this.y = ycoord;
    }

	public double distSq(Point p){
		double dx = this.x-p.x;
		double dy = this.y-p.y;
		return (dx*dx)+(dy*dy);
	}
	
	public double distSq(double xcoord, double ycoord){
		double dx = this.x-xcoord;
		double dy = this.y-ycoord;
		return (dx*dx)+(dy*dy);
		
	}

    public double dist(Point p) {
        return Math.sqrt(distSq(p));
    }
    
    public boolean equals(Point p){
    	return this.x == p.x && this.y == p.y;
    }
    
    public double getX(){
    	return this.x;
    }
    
    public double getY(){
    	return this.y;
    }
    
    public String toString() {
		String position =  "(" + Double.toString(x) + "," + Double.toString(y) + ")";
		return position;
	}
	
    @Override
	public Point clone(){  
	    try{  
	        return (Point) super.clone();  
	    }catch(Exception e){ 
	        return null; 
	    }
	}
}
