package controls;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import platformermapdesigner.MapPane;

/**
 *
 * @author DP
 */
public class KeyReleasedListener implements EventHandler<KeyEvent>{
    private final MapPane mp;
    
    public KeyReleasedListener(MapPane mp){
        this.mp = mp;
    }

    @Override
    public void handle(KeyEvent k) {
        if (k.getCode() == KeyCode.CONTROL){
            mp.ctrl = false;
        }
        if (k.getCode() == KeyCode.SHIFT){
            mp.shift = false;
        }
        if (k.getCode() == KeyCode.ALT){
            mp.alt = false;
        }
    }
    
}
