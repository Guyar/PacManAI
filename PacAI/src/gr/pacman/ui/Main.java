package gr.pacman.ui;

import gr.pacman.game.PacManInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class Main extends Application {
	PacManInterface pac;
	static GUIController gc;
	String selectedAI;
	int mode;
	Display display;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		pac = new PacManInterface();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("PacGUI.fxml"));
		AnchorPane page = (AnchorPane) loader.load();
		
		Scene scene = new Scene(page);
		
		int topRightX = pac.getWindow().x + pac.getWindow().width;
		int topRightY = pac.getWindow().y;
		
		primaryStage.setX(topRightX);
		primaryStage.setY(topRightY);
		primaryStage.setScene(scene);
		primaryStage.setTitle("PacMan AI");	
		
		primaryStage.show();
		gc = (GUIController)loader.getController();
		
		display = new Display(pac.gs);
		
		new Thread() {		
            public void run() {
				try {
					while(true) {
						if(gc.gameRunning){
							pac.play();
						}
						Thread.sleep(10);
	                }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
            }
        }.start();
        
        new Thread() {		
            // runnable for that thread
            public void run() {	
				try {
					while(true) {
						if(pac.currentState == "Playing"){
							display.updateImage(pac.gs);
		            		display.combineImage();
						}
						Thread.sleep(20);
	                }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
            }
        }.start();
         
        
        Task<Void> task = new Task<Void>() {
        	  @Override
        	  public Void call() throws Exception {
        	    while (true) {
        	      Platform.runLater(new Runnable() {
        	        @Override
        	        public void run() {
        	        	gc.updateGamestate(PacManInterface.currentState);
        	        	gc.updateGUI(pac.gs);
    	        		gc.updateMazeView(display.maze);
        	        }
        	      });
        	      Thread.sleep(20);
        	    }
        	  }
        	};
        	Thread th = new Thread(task);
        	th.setDaemon(true);
        	th.start();  
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
