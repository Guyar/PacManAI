package gr.pacman.game;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import gr.pacman.game.Grid;
import gr.pacman.game.Grid.type;
import gr.pacman.utils.Constants;

public class Maze implements Constants{
	Robot robot;
	Rectangle area;
	
	private Mat wallMask;

	static Scalar pill = new Scalar(142, 178, 255);//odd pink
    static Scalar powerPill = new Scalar(142, 178, 255);//odd pink
    static Scalar wall = new Scalar(219, 13, 13);//blue
    static Scalar background = new Scalar(0, 0, 0);//black
    
	static ArrayList<Scalar> mazeColours = new ArrayList<Scalar>(); 
    static {
    	mazeColours.add(pill);
    	mazeColours.add(wall);
    	mazeColours.add(background);
    }
    
    public Grid[][] Tiles;
	public ArrayList<Grid> pills,powerPills; 
	List<Grid> nonWallTiles;
	//HashMap<Grid, HashMap<Grid, Integer>> lookupTable;

	public Maze(Rectangle area) throws AWTException{
		this.area = area;		
		robot = new Robot();
        
        Tiles = new Grid[MAZE_WIDTH][MAZE_HEIGHT]; 
        pills = new ArrayList<Grid>(); 
		powerPills = new ArrayList<Grid>(); 
	}

	public void createMaze() {
    	BufferedImage bufferedImage = robot.createScreenCapture(area);
        
    	Mat src = img2Mat(bufferedImage);    	
        
        Mat pillsMask = new Mat();
        wallMask = new Mat();
        
        Mat hierarchy = new Mat();
        
        //split image into 28,31 16x16 tiles
        //pills 
		Core.inRange(src, mazeColours.get(0), mazeColours.get(0), pillsMask);
		//walls
		Core.inRange(src, mazeColours.get(1), mazeColours.get(1), wallMask);
        
    	for (int x = 0; x < MAZE_WIDTH; x++) {//number of tiles in x direction 
        	for(int y = 0; y < MAZE_HEIGHT; y++) {//number of tiles in y direction
        		Tiles[x][y] = new Grid(x, y, type.Bkgnd);
        		
        		Rect roi = new Rect((x*BLOCKSIZE), (y*BLOCKSIZE), BLOCKSIZE, BLOCKSIZE);	
        		Mat subMat = pillsMask.submat( roi );
        		
        		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        		Imgproc.findContours(subMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        		for(MatOfPoint contour : contours){
    				double contourArea = Imgproc.contourArea(contour);
    				if(contourArea == 9.0){
    					Tiles[x][y].type = type.Pill;
    					pills.add(Tiles[x][y]);
    				} else {
    					Tiles[x][y].type = type.Power;
    					powerPills.add(Tiles[x][y]);
    				}
        		}
        		
        		subMat = wallMask.submat( roi );
        		contours = new ArrayList<MatOfPoint>();
        		Imgproc.findContours(subMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        		if(contours.size() > 0) {
        			Tiles[x][y] = new Grid(x, y, type.Wall);
        		}
        	}
    	}
	}
	
	
	
	public void surroundingTiles() {
    	for (int x = 0; x < MAZE_WIDTH; x++) {//number of tiles in x direction 
        	for(int y = 0; y < MAZE_HEIGHT; y++) {//number of tiles in y direction
        		
        		if(y > 0) {
        			Tiles[x][y].up = Tiles[x][y-1];
        		}
        		if( x < MAZE_WIDTH-1){
        			Tiles[x][y].right = Tiles[x+1][y];
        		}
        		if( y < MAZE_HEIGHT-1){
        			Tiles[x][y].down = Tiles[x][y+1];
        		}
        		if( x > 0){
        			Tiles[x][y].left = Tiles[x-1][y];
        		}
        		
        		/*
        		if surrounding tiles are routes and not walls. if it has more than two directions it is a junction
        		if(Tiles[x][y].up.type ==  )
        		*/
        		
        	}
    	}
    	
    	//create tunnels
    	Tiles[0][14].left = Tiles[27][14];
    	Tiles[27][14].right = Tiles[0][14];
    	for(int x = 0;x<=5;x++){
    		Tiles[x][14].tunnel = true;
    	}
    	for(int x = 22;x<=27;x++){
    		Tiles[x][14].tunnel = true;
    	}
    	
    	//creates wall above ghost pen
    	Tiles[13][12].type = type.Wall;
    	Tiles[14][12].type = type.Wall;
    	for(int x = 11;x<=16;x++){
    		for(int y = 13;y<=15;y++){
    			Tiles[x][y].type = type.Bkgnd;
    		}
    	}
    	for(int x = 3;x<=24;x++){
			if(Tiles[x][3].type == type.Bkgnd){
				Tiles[x][3].type = type.Wall;
			}
    	}
    	for(int x = 0;x<=4;x++){
    		for(int y = 10;y<=12;y++){
    			Tiles[x][y].type = type.Wall;
    		}
    	}
    	for(int x = 0;x<=4;x++){
    		for(int y = 16;y<=18;y++){
    			Tiles[x][y].type = type.Wall;
    		}
    	}
    	for(int x = 23;x<=27;x++){
    		for(int y = 10;y<=12;y++){
    			Tiles[x][y].type = type.Wall;
    		}
    	}
    	for(int x = 23;x<=27;x++){
    		for(int y = 16;y<=18;y++){
    			Tiles[x][y].type = type.Wall;
    		}
    	}

	}
	
	public void createLookupTable(){
		nonWallTiles = new ArrayList<Grid>();
	    for (int x = 0; x < MAZE_WIDTH; x++) {//number of tiles in x direction 
        	for(int y = 0; y < MAZE_HEIGHT; y++) {//number of tiles in y direction
        		if(x>10 && x<17 && y>12 && y<16){
        		}else if(Tiles[x][y].type != type.Wall){
        			nonWallTiles.add(Tiles[x][y]);
        		}
        	}
		}
	    
	    //lookupTable = new HashMap<Grid,HashMap<Grid,Integer>>();
	    for(Grid tile:nonWallTiles){
	    	for(Grid tile2:nonWallTiles){
	    		tile.lookupTable.put(tile2, getPath(tile,tile2).size());
	    	}
	    }
	    
	    /*
	    lookupTable = new HashMap<Grid,HashMap<Grid,Integer>>();
	    for(Grid tile:nonWallTiles){
	    	lookupTable.put(tile, new HashMap<Grid,Integer>());
	    	for(Grid tile2:nonWallTiles){
	    		lookupTable.get(tile).put(tile2, getPath(tile,tile2).size());
	    	}	
	    }*/
	}
	
	public Mat getWallMask(){
		return this.wallMask;	
	}
	
	public BufferedImage mat2img(Mat m){
	      int type = BufferedImage.TYPE_BYTE_GRAY;
	      if ( m.channels() > 1 ) {
	          type = BufferedImage.TYPE_3BYTE_BGR;
	      }
	      int bufferSize = m.channels()*m.cols()*m.rows();
	      byte [] b = new byte[bufferSize];
	      m.get(0,0,b); // get all the pixels
	      BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
	      final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	      System.arraycopy(b, 0, targetPixels, 0, b.length);  
	      return image;
	}
	
	protected Mat img2Mat(BufferedImage in) {
        Mat out;
        byte[] data;
        	
        out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
        data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
        int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
        for (int i = 0; i < dataBuff.length; i++) {
            data[i * 3] = (byte) ((dataBuff[i] >> 0) & 0xFF);
            data[i * 3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
            data[i * 3 + 2] = (byte) ((dataBuff[i] >> 16) & 0xFF);
        }

        out.put(0, 0, data);
        return out;
    }
	
	public void printMaze(){
    	//testing
    	System.out.print("\t");
    	for (int r = 0; r<MAZE_WIDTH;r++){
    		if(r<10){
    			System.out.print(" " + r + " ");
    		} else {
    			System.out.print(" " + r);
    		}

    	}
    	System.out.println();
    	for (int c = 0; c < MAZE_HEIGHT;c++){
    		System.out.print(c + "\t");
	    	for (int r = 0; r<MAZE_WIDTH;r++){  
	    		if(Tiles[r][c].tunnel){
	    			System.out.print(" T ");
	    		}else if(Tiles[r][c].type == type.Wall){
    	    		System.out.print(" # ");
    	    	} else if(Tiles[r][c].type == type.Bkgnd){
    	    		System.out.print("   ");
    	    	} else if(Tiles[r][c].type == type.Pill){
    	    		System.out.print(" o ");
    	    	} else if(Tiles[r][c].type == type.Power){
    	    		System.out.print(" O ");
    	    	}
    	    }
    	    System.out.println();
    	}
    }
	public void printSurroundingTiles(){
    	for (int x = 0; x < MAZE_WIDTH; x++) {//number of tiles in x direction 
        	for(int y = 0; y < MAZE_HEIGHT; y++) {//number of tiles in y direction
				System.out.println("Tile[" + x + "][" + y + "]'s surrounding Tiles: ");
				if(Tiles[x][y].up != null){
					System.out.println("Up = " + "Tile[" + Tiles[x][y].up.x + "][" + Tiles[x][y].up.y + "]");
				}
				if(Tiles[x][y].right != null){
					System.out.println("Right = " + "Tile[" + Tiles[x][y].right.x + "][" + Tiles[x][y].right.y + "]");
				}
				if(Tiles[x][y].down != null){
					System.out.println("Down = " + "Tile[" + Tiles[x][y].down.x + "][" + Tiles[x][y].down.y + "]");
				}
				if(Tiles[x][y].left != null){
					System.out.println("Left = " + "Tile[" + Tiles[x][y].left.x + "][" + Tiles[x][y].left.y + "]");
				}       		
				System.out.println();
        	}
    	}
	}
	
	//Finds path using dijkstra
    public List<Grid> getPath(Grid start, Grid finish) {
    	Map<Grid, Boolean> vis = new HashMap<Grid, Boolean>();

	    Map<Grid, Grid> prev = new HashMap<Grid, Grid>();
    	
    	List<Grid> directions = new LinkedList<Grid>();
        Queue<Grid> q = new LinkedList<Grid>();
        Grid current = start;
        q.add(current);
        vis.put(current, true);
        while(!q.isEmpty()){
            current = q.remove();
            
            if (current.equals(finish)){
                break;
            }else{
                for(Grid Grid : current.getLegalNeighbours()){
            		if(!vis.containsKey(Grid)){
                        q.add(Grid);
                        vis.put(Grid, true);
                        prev.put(Grid, current);
                    }
                }
            }
        }
        if (!current.equals(finish)){
            System.out.println("can't reach destination");
        }
        
        for(Grid Grid = finish; Grid != null; Grid = prev.get(Grid)) {
            directions.add(Grid);
    			
        }
        Collections.reverse(directions);	
        
        return directions;
    }
}
