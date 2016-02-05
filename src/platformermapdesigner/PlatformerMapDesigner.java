package platformermapdesigner;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author DP
 */
public class PlatformerMapDesigner extends Application{
    public int WIDTH = 800;
    public int HEIGHT = 500;
    
    @Override
    public void start(Stage stage) throws Exception {
        Group tileMap = new Group();
        //tileMap.setFocusTraversable(true);
        MapPane mp = new MapPane(WIDTH, HEIGHT, tileMap);
        
        BorderPane root = new BorderPane();
        root.setCenter(mp);
        root.setBottom(tileMap);
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
        
        stage.setScene(scene);
        stage.setTitle("MEP: Map Engine for Platformers");
        stage.show();
    }
    
    @Override
    public void stop(){
        System.exit(0);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}//End class