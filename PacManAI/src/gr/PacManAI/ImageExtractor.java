package gr.PacManAI;

import java.awt.AWTException;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Robot;
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


public class ImageExtractor {
	Robot robot;
	Rectangle area;
    GameState gs;
    
    int width;
    int height;
    int tilewidth = 28;
    int tileheight = 31;
    int blocksize = 16;
    
    JFrame frame=new JFrame();//testing
    JLabel lbl=new JLabel();//testing
	public Tile[][] Tiles;
    
    public ImageExtractor(int x, int y, int width, int height) throws Exception {
    	robot = new Robot();
		area = new Rectangle(x, y, width, height);
        gs = new GameState();
        
        Tiles = new Tile[tilewidth][tileheight];       
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
    					GameState.pills.add(Tiles[x][y]);//is this okay to do?
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
    	
    	//testing
    	for (int c = 0; c <tileheight;c++){
	    	for (int r = 0; r<tilewidth;r++){
    	    	
    	        System.out.print(r + " " + c + " ");
    	        System.out.print(Tiles[r][c].type + "\t");
    	    }
    	    System.out.println();
    	}
	}
    
    public void update(ArrayList<Scalar> colours) throws Exception {
    	BufferedImage bufferedImage = robot.createScreenCapture(area);
    	updatePacmanGhosts(bufferedImage, colours);
    	gs.updatePills(bufferedImage);
    	gs.updatePowerPills(bufferedImage);
    	/*
	    File outputfile = new File("image.png");
	    ImageIO.write(bufferedImage, "png", outputfile);
	    */
    	
    	/*//testing
    	for (int c = 0; c <tileheight;c++){
	    	for (int r = 0; r<tilewidth;r++){
    	    	
    	        System.out.print(r + " " + c + " ");
    	        System.out.print(Tiles[r][c].type + "\t");
    	    }
    	    System.out.println();
    	}*/
	}

    public void updatePacmanGhosts(BufferedImage bufferedImage, ArrayList<Scalar> colours) throws Exception {	    
	  
	    
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
    			
				gs.updateLocations(x, coordinates, contourArea, bufferedImage);
			}
    		
    		//testing
    		if(x == 0){
    			combMask = mask.clone();
    		} else {
    			Core.add(combMask,mask,combMask);
    		}
    		//
    		

        }
		
		showImage(combMask);
		
		//System.out.println("image extraction done");
		
	}
    
    
    
    public void showImage(Mat combMask){
    	BufferedImage img2 = mat2img(combMask);
		ImageIcon icon=new ImageIcon(img2);
	    frame.setIconImage(img2);
	    frame.setLayout(new FlowLayout());        
	    frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
	    
	    lbl.setIcon(icon);
	    frame.add(lbl);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setFocusableWindowState(false);
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

	


}
