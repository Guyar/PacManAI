package gr.pacman.game;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import gr.pacman.utils.Point;

public class ImageExtractor {
	Robot robot;
	Rectangle area;
	
	Point pacCoordinates;
	Point fruitCoords;
	public Point[] ghostsCoords;
	private int noOfEdibleGhosts;
	
	Mat wallMask;
	
	//these are all bgr
	static Scalar blinky = new Scalar(0, 0, 255);//red
	static Scalar pinky = new Scalar(219, 178, 255);//pink
	static Scalar inky = new Scalar(219, 255, 0);//cyan
	static Scalar clyde = new Scalar(54, 178, 255);//orange
	static Scalar pacman = new Scalar(0, 255, 255);//yellow
	static Scalar edible = new Scalar(219, 13, 13);//blue
	static Scalar edibleFlash = new Scalar(219, 219, 219);//white ghost
	static Scalar cherryStrawberry = new Scalar(0, 0, 255);//area cherry = 182.5
		  
	static ArrayList<Scalar> ghostColours = new ArrayList<Scalar>(); 
	static {
		ghostColours.add(blinky);
		ghostColours.add(pinky);
		ghostColours.add(inky);
		ghostColours.add(clyde);	  
	}
	static ArrayList<Scalar> edibleColours = new ArrayList<Scalar>(); 
	static {   
		edibleColours.add(edible);
		edibleColours.add(edibleFlash);
	}
	static ArrayList<Scalar> itemColours = new ArrayList<Scalar>(); 
	static {   
		itemColours.add(cherryStrawberry);
	}
	
	Mat src,mask,hierarchy;
	ArrayList<MatOfPoint> contours;
	private List<String> fruitData;
	public String fruitName;
	
	public ImageExtractor(Rectangle area) throws AWTException, IOException, URISyntaxException {
		this.area = area;
		robot = new Robot();
		ghostsCoords = new Point[4];
		pacCoordinates = new Point(0,0);
		fruitCoords = new Point(0,0);
		fruitData = Files.readAllLines(Paths.get(getClass().getResource("/FruitData.csv").toURI()));
	}

	public void findPacmanGhosts(BufferedImage bufferedImage, Ghost[] ghosts, Mat wallMask) throws Exception {	
		src = img2Mat(bufferedImage);
		mask = new Mat();
		hierarchy = new Mat();
		
		//update pacman
		//creates a mask for the colour of pacman
		Mat pacManMask = new Mat();
		Core.inRange(src, pacman, pacman, pacManMask);
		contours = new ArrayList<MatOfPoint>();	  	
		Imgproc.findContours(pacManMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		for( int i = 0; i < contours.size();i++){		
			Rect pacRect = Imgproc.boundingRect(contours.get(i));
			
			pacCoordinates = new Point((pacRect.x + (pacRect.width/2)) ,(pacRect.y + (pacRect.height/2)));		
		}
		
		//update ghosts
		ghostsCoords = new Point[4];
		for(int i = 0; i < ghostColours.size(); i++) {
			Core.inRange(src, ghostColours.get(i), ghostColours.get(i), mask);

			contours = new ArrayList<MatOfPoint>();	  	
			Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
			
			for( int j = 0; j < contours.size();j++){
				Rect rect = Imgproc.boundingRect(contours.get(j));
				
				Point coordinates = new Point((rect.x + (rect.width/2)) ,(rect.y + (rect.height/2)));
				double contourArea = Imgproc.contourArea(contours.get(j));
					
				if (isGhost(coordinates,contourArea)){
					ghostsCoords[i] = coordinates;
					ghosts[i].setEdible(false);
				}
				
			}
		}
		
		//update edible ghosts
		noOfEdibleGhosts = 0;
		for(int i = 0; i < edibleColours.size(); i++) {
			Core.inRange(src, edibleColours.get(i), edibleColours.get(i), mask);
			
			Core.subtract(mask,wallMask,mask);//removes the walls from the mask as they are the same colour as edible ghosts
			
			contours = new ArrayList<MatOfPoint>();	  	
			Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
			
			for( int j = 0; j < contours.size();j++){	
				Rect rect = Imgproc.boundingRect(contours.get(j));		
				Point coordinates = new Point((rect.x + (rect.width/2)) ,(rect.y + (rect.height/2)));
				double contourArea = Imgproc.contourArea(contours.get(j));

				if(isEdibleGhost(coordinates, contourArea)){
					for(int k = 0; k<4; k++){
						if(ghostsCoords[k] == null){
							ghostsCoords[k] = coordinates;
							noOfEdibleGhosts+=1;
							break;
						}
					}				
				}
			}
		}
	}
	
	public void findFruit(BufferedImage bufferedImage, int level) {
		src = img2Mat(bufferedImage);
		mask = new Mat();
		hierarchy = new Mat();
		
		if(level >= 13){
			level = 13;
		}
		
		String line = fruitData.get(level);
		String[] data = line.split(",");
		
		fruitName = data[1];		
		Double fruitArea = Double.parseDouble(data[2]);	
		Scalar fruitColour = new Scalar(Double.parseDouble(data[3]), Double.parseDouble(data[4]), Double.parseDouble(data[5]));
		
		//finds the fruits coordinates
		Mat pacManMask = new Mat();
		Core.inRange(src, fruitColour, fruitColour, pacManMask);
		contours = new ArrayList<MatOfPoint>();	  	
		Imgproc.findContours(pacManMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		for( int i = 0; i < contours.size();i++){
			Rect rect = Imgproc.boundingRect(contours.get(i));
			Point coordinates = new Point((rect.x + (rect.width/2)) ,(rect.y + (rect.height/2)));		
			double contourArea = Imgproc.contourArea(contours.get(i));
			if(contourArea == fruitArea){
				fruitCoords = coordinates;
			}
		}
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
	
	public boolean isGhost(Point coords,Double area) {
		//adds check for item depending on what level it is?
		
		return coords.getY() < 490.0 && area > 400.0 && area < 600.0;// area != 93.0 stops the pink barrier in ghost spawn
		//maybe add checks for the items too? like if coordinates are this and size is this dont include
		//93.0 is pink bar in ghost prison
		//182.5 is cherry
		//264.0 is strawberry
		//17.0 is red
		//9.0 is red
		//432.0 is min size of ghost
		//586.0 is max size of ghost
	}
	
	private boolean isEdibleGhost(Point coords, double area) {
		return coords.getY() < 490.0 && area > 40.0;//so it doesn't include the eyes
		//565 and 560 are islands that the game thinks are edible ghosts
	}
	
	public int getNumEdibleGhosts(){
		return noOfEdibleGhosts;
	}



	

}
