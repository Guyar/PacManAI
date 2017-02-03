package gr.PacManAI;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.util.concurrent.*;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
	static int delay = 10;
	MyService myService;
	public static void main(String[] args) {
		Application.launch(args);
	}
	@Override
	public void start(Stage stage) throws Exception
	{
		//BuildGUI windowMain = new BuildGUI(stage);
		myService = new MyService();
		
		//Builds the GUI
		Button btn = new Button();
        btn.setText("Start Game");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	myService.start();

            }
        });
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        Scene scene = new Scene(root, 150, 100);
        
        stage.setTitle("Start");
        stage.setScene(scene);
        stage.show();
        
		
		System.out.println("aaaa");//
		
	}
	
	private class MyService extends Service<Void> {
		 
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while(true){
                        if (isCancelled()) {
                            break;
                        }
                        ScreenShot a = new ScreenShot(50,50);
 
                        Thread.sleep(100);
                    }
                    return null;
                }
            };
        }
    }
}
