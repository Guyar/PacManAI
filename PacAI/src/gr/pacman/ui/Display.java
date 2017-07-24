package gr.pacman.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import gr.pacman.game.GameState;
import gr.pacman.game.Grid.type;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Display {
	ImageView display;
	BufferedImage[][] images;
	BufferedImage wallImg,bkgndImg,pillImg,powerPillImg,pacmanImg,blinkyImg,blinkyTargetImg,
			pinkyImg,pinkyTargetImg,inkyImg,inkyTargetImg,clydeImg,clydeTargetImg,edibleImg;
	BufferedImage result;
	Graphics g;
	public Image maze;
	
	public Display(GameState gs) throws IOException {	
        wallImg = ImageIO.read(this.getClass().getResource("/images/displayImages/wall.png"));
        bkgndImg = ImageIO.read(this.getClass().getResource("/images/displayImages/bkgnd.png"));
        pillImg = ImageIO.read(this.getClass().getResource("/images/displayImages/pill.png"));
        powerPillImg = ImageIO.read(this.getClass().getResource("/images/displayImages/powerPill.png"));
        pacmanImg = ImageIO.read(this.getClass().getResource("/images/displayImages/pacman.png"));
        blinkyImg = ImageIO.read(this.getClass().getResource("/images/displayImages/blinky.png"));
        blinkyTargetImg = ImageIO.read(this.getClass().getResource("/images/displayImages/blinkyTarget.png"));
		pinkyImg = ImageIO.read(this.getClass().getResource("/images/displayImages/pinky.png"));
		pinkyTargetImg = ImageIO.read(this.getClass().getResource("/images/displayImages/pinkyTarget.png"));
		inkyImg = ImageIO.read(this.getClass().getResource("/images/displayImages/inky.png"));
		inkyTargetImg = ImageIO.read(this.getClass().getResource("/images/displayImages/inkyTarget.png"));
		clydeImg = ImageIO.read(this.getClass().getResource("/images/displayImages/clyde.png"));
		clydeTargetImg = ImageIO.read(this.getClass().getResource("/images/displayImages/clydeTarget.png"));
		edibleImg = ImageIO.read(this.getClass().getResource("/images/displayImages/edible.png"));
		
		images = new BufferedImage[28][31];
	}
	public void updateImage(GameState gs){
		for (int x = 0; x < 28;x++){
	    	for (int y = 0; y<31;y++){
	    		if(gs.mazeTiles[x][y].type == type.Wall){
    	    		images[x][y] = wallImg;
    	    	} else if(gs.mazeTiles[x][y].type == type.Bkgnd){
    	    		images[x][y] = bkgndImg;
    	    	} else if(gs.mazeTiles[x][y].type == type.Pill){
    	    		images[x][y] = pillImg;
    	    	} else if(gs.mazeTiles[x][y].type == type.Power){
    	    		images[x][y] = powerPillImg;
    	    	}
    	    }
    	}
		
		if(gs.pacman.mazeLocation!=null){
			images[gs.pacman.mazeLocation.x][gs.pacman.mazeLocation.y] = pacmanImg;
		}
		if(gs.red.mazeLocation!=null){
			if(!gs.red.edible){
				images[gs.red.mazeLocation.x][gs.red.mazeLocation.y] = blinkyImg;
			}else{
				images[gs.red.mazeLocation.x][gs.red.mazeLocation.y] = edibleImg;
			}
		}
		if(gs.pink.mazeLocation!=null){
			if(!gs.pink.edible){
				images[gs.pink.mazeLocation.x][gs.pink.mazeLocation.y]= pinkyImg;
			}else{
				images[gs.pink.mazeLocation.x][gs.pink.mazeLocation.y] = edibleImg;
			}
		}
		if(gs.blue.mazeLocation!=null){
			if(!gs.blue.edible){
				images[gs.blue.mazeLocation.x][gs.blue.mazeLocation.y] = inkyImg;
			}else{
				images[gs.blue.mazeLocation.x][gs.blue.mazeLocation.y] = edibleImg;
			}
		}
		if(gs.orange.mazeLocation!=null){
			if(!gs.orange.edible){
				images[gs.orange.mazeLocation.x][gs.orange.mazeLocation.y] = clydeImg;
			}else{
				images[gs.orange.mazeLocation.x][gs.orange.mazeLocation.y] = edibleImg;
			}
		}
	}
	
	public void combineImage() throws IOException{	
		result = new BufferedImage(280, 310, BufferedImage.TYPE_INT_RGB);
		g = result.getGraphics();
		for (int x = 0; x < 28;x++){
	    	for (int y = 0; y < 31;y++){ 
	    		g.drawImage(images[x][y], x*10, y*10, null);
	    	}
	    }
		maze = SwingFXUtils.toFXImage(result, null);
	}
}
