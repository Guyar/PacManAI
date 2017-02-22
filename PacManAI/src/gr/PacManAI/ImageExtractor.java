package gr.PacManAI;

import java.awt.AWTException;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.*;

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


public class ImageExtractor {
	Robot robot;
	Rectangle area;
    GameState gs;
    
    JFrame frame=new JFrame();//testing
    JLabel lbl=new JLabel();//testing
    
    public ImageExtractor(int x, int y, int width, int height) throws Exception {
    	robot = new Robot();
		area = new Rectangle(x, y, width, height);
        gs = new GameState();
        
    }
    
   
	public ArrayList<MatOfPoint> createMaze(Scalar colour) {
		BufferedImage bufferedImage = robot.createScreenCapture(area);
		Mat src = img2Mat(bufferedImage);
		
		Mat hierarchy = new Mat();
		Mat mask = new Mat();
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();  
		
		Core.inRange(src, colour, colour, mask);
		Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		int dilation_size = 10;
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilation_size + 1, 2*dilation_size+1));
		Imgproc.dilate(mask, mask, element);
		
		
		for( int i = 1; i < contours.size();i++){
			Imgproc.drawContours(mask, contours, i, new Scalar(255, 255, 255), -1);
		}
		showImage(mask);
		
		return null;
		
	}

    
    public void TakeScreenShot(ArrayList<Scalar> colours) throws Exception {
	    BufferedImage bufferedImage = robot.createScreenCapture(area);
	    Mat src = img2Mat(bufferedImage);
        
        Mat mask = new Mat();
        Mat combMask = null;//testing

        Mat hierarchy = new Mat();
        
		Mat maze = null;//testing
		for(int x = 0; x < colours.size()-1; x++) {
        	Core.inRange(src, colours.get(x), colours.get(x), mask);
        	
        	ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();      	
    		Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    		
    		for( int i = 0; i < contours.size();i++){
    			Rect rect = Imgproc.boundingRect(contours.get(i));
    			Point2D coordinates = new Point2D.Double(rect.x, rect.y);
    			double contourArea = Imgproc.contourArea(contours.get(i));	
    			
				gs.update(x, coordinates, contourArea);				
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
