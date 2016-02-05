package controls;

import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;
import platformermapdesigner.MapPane;

public class MouseWheelListener implements EventHandler<ScrollEvent>{
    private final MapPane mp;
    
    public MouseWheelListener(MapPane mp){
        this.mp = mp;
    }

    @Override
    public void handle(ScrollEvent e) {
        if (e.getDeltaY() < 0){
            //if the currentTile is less than the total number of tiles
            if(mp.currentTile < mp.numTileRows*mp.numTileColumns-1){
                mp.currentTile++;
            }
        }
        else if(e.getDeltaY() > 0){
            //if the currentTile isn't the first tile
            if (mp.currentTile > 0){
                mp.currentTile--;
            }
        }
        mp.updateCurrentTile();
    }
    
}