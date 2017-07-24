package gr.pacman.ui;

import java.net.URL;
import java.util.ResourceBundle;

import gr.pacman.game.Controller;
import gr.pacman.game.GameState;
import gr.pacman.game.Ghost;
import gr.pacman.game.PacMan;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class GUIController implements Initializable {
	Boolean gameRunning;
	
	@FXML
	private ImageView mazeView;
	
	@FXML
    private ComboBox<String> comboBox;
	
	@FXML
	private HBox settingsBox;
	
	@FXML
	private CheckBox pillCheck,avoidCheck;
	
	@FXML
	private ImageView pacManImg,blinkyImg,pinkyImg,inkyImg,clydeImg;
	
	@FXML
	private Label levelTime,lives,pillsLeft,powerPillsLeft,powerPillEaten,level;
	
	@FXML
	private Label pacState,pacLoc;
	
	@FXML
	private Label blinkyState,pinkyState,inkyState,clydeState,blinkyLoc,pinkyLoc,inkyLoc,clydeLoc;
	
	@FXML
	private Label ghostsInMaze,edibleGhostsInMaze;
	
	@FXML
	private Label gamestate;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gameRunning = false;
	}
	
	@FXML
	private void startGame() {
		Controller.StartGame();
		gameRunning = true;
	}
	
	@FXML 
	public void updateMazeView(Image image) {
		mazeView.setImage(image);
	}
	
	@FXML 
	public void updateGamestate(String state) {
		gamestate.setText(state);
	}
	
	@FXML 
	public void updateGhostsLoc(Ghost[] ghosts) {
		if(ghosts[0].spawned){
			blinkyState.setText(ghosts[0].strategy);
			String xcoord = Integer.toString(ghosts[0].mazeLocation.x);
			String ycoord = Integer.toString(ghosts[0].mazeLocation.y);
			blinkyLoc.setText("(" + xcoord + "," + ycoord + ")");	
		} else {
			blinkyState.setText("In spawn");
		}
		
		if(ghosts[1].spawned){
			pinkyState.setText(ghosts[1].strategy);
			String xcoord = Integer.toString(ghosts[1].mazeLocation.x);
			String ycoord = Integer.toString(ghosts[1].mazeLocation.y);
			pinkyLoc.setText("(" + xcoord + "," + ycoord + ")");
		} else {
			pinkyState.setText("In spawn");
		}
		
		if(ghosts[2].spawned){
			inkyState.setText(ghosts[2].strategy);
			String xcoord = Integer.toString(ghosts[2].mazeLocation.x);
			String ycoord = Integer.toString(ghosts[2].mazeLocation.y);
			inkyLoc.setText("(" + xcoord + "," + ycoord + ")");
		} else {
			inkyState.setText("In spawn");
		}
		
		if(ghosts[3].spawned){
			clydeState.setText(ghosts[3].strategy);
			String xcoord = Integer.toString(ghosts[3].mazeLocation.x);
			String ycoord = Integer.toString(ghosts[3].mazeLocation.y);
			clydeLoc.setText("(" + xcoord + "," + ycoord + ")");
		} else {
			clydeState.setText("In spawn");
		}
	}
	
	@FXML 
	public void updatePacman(PacMan pacman) {		
		if(pacman.mazeLocation != null){
			String xcoord = Integer.toString(pacman.mazeLocation.x);
			String ycoord = Integer.toString(pacman.mazeLocation.y);
			pacLoc.setText("(" + xcoord + "," + ycoord + ")");
		} else {
			pacLoc.setText("(" + 0 + "," + 0 + ")");
		}
	}
	
	@FXML 
	public void updateGUI(GameState gs) {
		updatePacman(gs.pacman);
		updateGhostsLoc(gs.ghosts);
		int spawnedGhosts = 0;
		for(Ghost ghost: gs.ghosts){
			if(ghost.spawned){
				spawnedGhosts+=1;
			}
		}
		setDirections(gs);
		
		ghostsInMaze.setText(Integer.toString(spawnedGhosts));
		edibleGhostsInMaze.setText(Integer.toString(gs.numEdibleGhosts));
		
		lives.setText(Integer.toString(gs.lives));
		level.setText(Integer.toString(gs.level));
		pillsLeft.setText(Integer.toString(gs.pillsLeft));
		powerPillsLeft.setText(Integer.toString(gs.powerPillsLeft));
		powerPillEaten.setText(Boolean.toString(gs.energiser));
		levelTime.setText(Double.toString(gs.timer));
	}
	
	@FXML 
	public void setDirections(GameState gs) {//this be in here? i feel like it should be in model not controller
		Image img = null;	
		switch (gs.pacman.direction) {
		case 1: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/pacmanUp.png"));
			break;
		case 2: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/pacmanRight.png"));
			break;
		case 3: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/pacmanDown.png"));
			break;
		case 4: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/pacmanLeft.png"));
			break;
		}
		pacManImg.setImage(img);
		
		img = null;
		switch (gs.red.direction) {
		case 1:
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/blinkyUp.png"));
			break;
		case 2: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/blinkyRight.png"));
			break;
		case 3: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/blinkyDown.png"));
			break;
		case 4: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/blinkyLeft.png"));
			break;
		}
		blinkyImg.setImage(img);
		
		img = null;
		switch (gs.pink.direction) {
		case 1: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/pinkyUp.png"));
			break;
		case 2: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/pinkyRight.png"));
			break;
		case 3: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/pinkyDown.png"));
			break;
		case 4: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/pinkyLeft.png"));
			break;
		}
		pinkyImg.setImage(img);
		
		img = null;
		switch (gs.blue.direction) {
		case 1: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/inkyUp.png"));
			break;
		case 2: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/inkyRight.png"));
			break;
		case 3: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/inkyDown.png"));
			break;
		case 4: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/inkyLeft.png"));
			break;
		}
		inkyImg.setImage(img);
		
		img = null;
		switch (gs.orange.direction) {
		case 1: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/clydeUp.png"));
			break;
		case 2: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/clydeRight.png"));
			break;
		case 3: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/clydeDown.png"));
			break;
		case 4: 
			img = new Image(this.getClass().getResourceAsStream("/images/guiImages/clydeLeft.png"));
			break;
		}
		clydeImg.setImage(img);
	}
}
