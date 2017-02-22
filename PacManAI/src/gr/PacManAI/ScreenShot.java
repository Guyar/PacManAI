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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

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
import org.opencv.imgproc.Imgproc;

public class ScreenShot  {
	Robot robot;
	Rectangle area;
	public ScreenShot(int x, int y, int width, int height) throws Exception {
		robot = new Robot();
		area = new Rectangle(x, y, width, height);
	    
	}
	
	public void TakeScreenShot() throws Exception {
		
	    BufferedImage bufferedImage = robot.createScreenCapture(area);
	    
	    Mat src = img2Mat(bufferedImage);
        //Mat hsvImage = new Mat();
        //Imgproc.cvtColor(src, hsvImage, Imgproc.COLOR_BGR2HSV);
        
        Mat mask = new Mat();
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        for(int x = 0; x<colours.size();x++){
        	Core.inRange(src, colour[x], colour[x], mask);
        	
    		Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
    		System.out.println(contours.size());
    		
    		for( int i = 0; i<contours.size();i++){
    			Rect rect = Imgproc.boundingRect(contours.get(i));
    			Point2D coordinates = new Point2D.Double(rect.x, rect.y);
    			System.out.println(coordinates);
    			//objects.add(type, coordinates);
    		}
        }
        
		
	    
	    //File outputfile = new File("image.png");
		//ImageIO.write(bufferedImage, "png", outputfile);
		BufferedImage img2 = toBufferedImage(mask);
		ImageIcon icon=new ImageIcon(img2);
	    JFrame frame=new JFrame();
	    frame.setLayout(new FlowLayout());        
	    frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
	    JLabel lbl=new JLabel();
	    lbl.setIcon(icon);
	    frame.add(lbl);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	protected Mat img2Mat(BufferedImage in) {
        Mat out;
        byte[] data;
        int r, g, b;

        if (in.getType() == BufferedImage.TYPE_INT_RGB) {
            out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
            data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
            for (int i = 0; i < dataBuff.length; i++) {
                data[i * 3] = (byte) ((dataBuff[i] >> 0) & 0xFF);
                data[i * 3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
                data[i * 3 + 2] = (byte) ((dataBuff[i] >> 16) & 0xFF);
            }
        } else {
            out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC1);
            data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
            for (int i = 0; i < dataBuff.length; i++) {
                r = (byte) ((dataBuff[i] >> 0) & 0xFF);
                g = (byte) ((dataBuff[i] >> 8) & 0xFF);
                b = (byte) ((dataBuff[i] >> 16) & 0xFF);
                data[i] = (byte) ((0.21 * r) + (0.71 * g) + (0.07 * b));
            }
        }
        out.put(0, 0, data);
        return out;
    }
	
	public BufferedImage toBufferedImage(Mat m){
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
	/*
	public ArrayList createObjects(Set<Integer> colours) {
		
		// find contours
		
		

		ArrayList pills = new ArrayList();
		
		
		thingy = (object type, coordinates)
		if ((-2434305 & 0xFFFFFF) != BG && colours.contains(pix[p])) {
			pills.add(coordinates);
		}
		for(type  each object type in objects {//eg clyde
			colour = object.colour
			mask = cv2.inRange(bufferedImage, colour, colour);
			Imgproc.findContours(maskedImage, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
			Rect rect = Imgproc.boundingRect(contours.get(idx));
			coordinates = (rect.x, rect.y);
			objects.add(type, coordinates);
		}
	}
	*/

}