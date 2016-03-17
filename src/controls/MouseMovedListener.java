package controls;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import platformermapdesigner.MapPane;

/**
 * Used only for testing mouse locations; not used in final program.
 *
 * @author DP
 */
public class MouseMovedListener implements EventHandler<MouseEvent>{
    private final MapPane mp;
    private int counter;
    private int print = 100;
    
    public MouseMovedListener(MapPane mp){
        this.mp = mp;
    }
    
    @Override
    public void handle(MouseEvent e) {
        if (counter == 100){
        if (e.getX() > mp.getHeight() - mp.numTileRows*mp.tileSize){
            System.out.println("Outside");
        }
        if (e.getX() < mp.getHeight() - mp.numTileRows*mp.tileSize){
            System.out.println("Inside");
        }
        counter = 0;
        }
        else{counter++;}
    }
    
}
