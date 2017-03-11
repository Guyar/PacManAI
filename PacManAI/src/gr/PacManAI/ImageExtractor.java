package gr.PacManAI;

import java.awt.AWTException;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import gr.PacManAI.Tile.type;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import java.awt.Dimension;
import java.awt.Color;

public class ImageExtractor {
	Robot robot;
	Rectangle area;
	
	Point2D pacCoordinates;
	HashMap<Integer,Point2D> ghostLoc = new HashMap<Integer,Point2D>();
	HashMap<Integer,Point2D> edibleGhostLoc = new HashMap<Integer,Point2D>();
	
    int tilewidth = 28;
    int tileheight = 31;
    int blocksize = 16;
    
    
	Tile[][] Tiles;
	ArrayList<Tile> pills; 
	ArrayList<Tile> powerPills;
    
    public ImageExtractor(Rectangle area) throws Exception {   	
    	this.area = area;
    	robot = new Robot();
        
        Tiles = new Tile[tilewidth][tileheight]; 
        pills = new ArrayList<Tile>(); 
		powerPills = new ArrayList<Tile>(); 
    }
    
    public void surroundingTiles() {
    	for (int x = 0; x < tilewidth; x++) {//number of tiles in x direction 
        	for(int y = 0; y < tileheight; y++) {//number of tiles in y direction
        		
        		if(y > 0) {
        			Tiles[x][y].up = Tiles[x][y-1];
        		}
        		if( x < tilewidth-1){
        			Tiles[x][y].right = Tiles[x+1][y];
        		}
        		if( y < tileheight-1){
        			Tiles[x][y].down = Tiles[x][y+1];
        		}
        		if( x > 0){
        			Tiles[x][y].left = Tiles[x-1][y];
        		}
        	}
    	}
    	
    	//for tunnels
    	Tiles[14][0].left = Tiles[27][14];
    	Tiles[14][27].right = Tiles[0][14];
    	
    	//fix maze
    	Tiles[13][11].type = type.Bkgnd;
    	Tiles[14][11].type = type.Bkgnd;
    	//creates wall above ghost pen
    	Tiles[13][12].type = type.Wall;
    	Tiles[14][12].type = type.Wall;
    	//this fixes that the createmaze views ready message as power pills
    	Tiles[9][16].type = type.Bkgnd;
    	Tiles[18][16].type = type.Bkgnd;
    	for(int x = 9;x<=18;x++){
    		Tiles[x][17].type = type.Bkgnd;
    	}
	}
    
    
    public void createMaze(ArrayList<Scalar> mazeColours) {
    	BufferedImage bufferedImage = robot.createScreenCapture(area);
    	Mat src = img2Mat(bufferedImage);
        
        Mat mask = new Mat();
        Mat hierarchy = new Mat();
        //split image into 28,29 16x16 tiles
        
    	for (int x = 0; x < tilewidth; x++) {//number of tiles in x direction 
        	for(int y = 0; y < tileheight; y++) {//number of tiles in y direction
        		
        		Tiles[x][y] = new Tile(x, y, type.Bkgnd);
        		
        		Rect roi = new Rect((x*blocksize), (y*blocksize),blocksize, blocksize);	
        		//pills 
        		Core.inRange(src, mazeColours.get(0), mazeColours.get(0), mask);
        		Mat aaa = mask.submat( roi );
        		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        		Imgproc.findContours(aaa, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        		for(MatOfPoint contour : contours){
    				double contourArea = Imgproc.contourArea(contour);
    				if(contourArea == 1.0){
    					Tiles[x][y].type = type.Pill;
    					pills.add(Tiles[x][y]);//is this okay to do?
    				} else {
    					Tiles[x][y].type = type.Power;
    				}
        		}
        		//walls
        		Core.inRange(src, mazeColours.get(1), mazeColours.get(1), mask);
        		aaa = mask.submat( roi );
        		contours = new ArrayList<MatOfPoint>();
        		Imgproc.findContours(aaa, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        		if(contours.size() > 0) {
        			Tiles[x][y] = new Tile(x, y, type.Wall);
        		}
        	}
    	}
	}
    public void printSurroundingTiles(){
    	for (int x = 0; x < tilewidth; x++) {//number of tiles in x direction 
        	for(int y = 0; y < tileheight; y++) {//number of tiles in y direction
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
    public void printMaze(){
    	//testing
    	System.out.print("\t");
    	for (int r = 0; r<tilewidth;r++){
    		if(r<10){
    			System.out.print(" " + r + " ");
    		} else {
    			System.out.print(" " + r);
    		}

    	}
    	System.out.println();
    	for (int c = 0; c <tileheight;c++){
    		System.out.print(c + "\t");
	    	for (int r = 0; r<tilewidth;r++){   		
    	    	if(Tiles[r][c].type == type.Wall){
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
 
    public void updatePacmanGhosts(ArrayList<Scalar> colours) throws Exception {	
    	BufferedImage bufferedImage = robot.createScreenCapture(area);
    	/*
	    File outputfile = new File("image.png");
	    ImageIO.write(bufferedImage, "png", outputfile);
	    */
	    Mat src = img2Mat(bufferedImage);
        
        Mat mask = new Mat();
        Mat combMask = null;//testing

        Mat hierarchy = new Mat();
		
		for(int x = 0; x < colours.size(); x++) {
        	Core.inRange(src, colours.get(x), colours.get(x), mask);
        	
        	ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();      	
    		Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    		
    		for( int i = 0; i < contours.size();i++){
    			Rect rect = Imgproc.boundingRect(contours.get(i));
    			
    			Point2D coordinates = new Point2D.Double((rect.x + (rect.width/2)) ,(rect.y + (rect.height/2)));
    			double contourArea = Imgproc.contourArea(contours.get(i));	
    			
    			if(x == 5){
    				//System.out.println("edible ghosts");
    				if(isGhost(coordinates, contourArea) && !inSpawn(coordinates)){
    					edibleGhostLoc.put(i, coordinates);
    					//empty list after... dont really need to
    				}
    			} else {
    				if (isPacMan(x, coordinates)) {
        	            pacCoordinates = coordinates;
        			} else if (isGhost(coordinates, contourArea) && !inSpawn(coordinates)) {     	
    		            // need to update the state of the ghost distance
        				System.out.println("hey");
    		        	ghostLoc.put(x, coordinates);
        			} else if (isGhost(coordinates, contourArea) && inSpawn(coordinates)){
        				ghostLoc.remove(x);
        				//ghostLoc.put(x, null);
        			}
    			}
    			
			}
    		
    		//testing
    		if(x == 0){
    			combMask = mask.clone();
    		} else {
    			Core.add(combMask,mask,combMask);
    		}
        }
		
		//showImage(combMask);		
		//System.out.println("image extraction done");		
	}
    
    JFrame frame=new JFrame();//testing
    JLabel lbl=new JLabel();//testing

    public void showImage(Mat combMask){
    	
    	BufferedImage img2 = mat2img(combMask);
		ImageIcon icon=new ImageIcon(img2);
		
	    frame.setIconImage(img2);
	    frame.getContentPane().setLayout(new FlowLayout());        
	    frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
	     
	    lbl.setIcon(icon);
	    frame.getContentPane().add(lbl);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setFocusableWindowState(false);
    }
    
    public void findMaze(){
    	
    	BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
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

    public boolean isPacMan(int x, Point2D coords) {		
		return x == 4 && coords.getY() < 495.0;
        //only do it if coordinates aren't outside the game
    }
    
    public boolean isGhost(Point2D coords, double area) {
    	return coords.getY() < 495.0 && area > 346.0 && area < 431.0;
    }
    public boolean inSpawn(Point2D coords) {
    	
    	int tileX = (int) (coords.getX()/16);
    	int tileY = (int) (coords.getY()/16);
    	System.out.println("spawn : " + tileX + " "+ tileY);
    	return 10 <= tileX && tileX <= 17 && 12 <= tileY && tileY <= 16;
    }

	


}
