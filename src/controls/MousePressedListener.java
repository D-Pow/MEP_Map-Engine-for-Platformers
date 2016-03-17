package controls;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import platformermapdesigner.MapPane;

public class MousePressedListener implements EventHandler<MouseEvent>{
    private final MapPane mp;
    private final Rectangle mapOutline;
    
    public MousePressedListener(MapPane mp){
        this.mp = mp;
        mapOutline = mp.mapOutline;
    }

    @Override
    public void handle(MouseEvent e) {
        if (e.isPrimaryButtonDown()){
            //If in bounds of the map
            if (e.getX() > mp.mapOutline.getX() &&
                    e.getY() > mp.mapOutline.getY() &&
                    e.getX() < mp.mapOutline.getX() + mp.mapOutline.getWidth() &&
                    e.getY() < mp.mapOutline.getY() + mp.mapOutline.getHeight()){
                //Get the right tile from tileSet
                int tileRow = 0;
                int tileCol = 0;
                int selectionIndex = mp.currentTile;
                while (selectionIndex > mp.numTileColumns - 1){
                    selectionIndex = selectionIndex - mp.numTileColumns;
                    tileRow++;
                }
                tileCol = selectionIndex;
                Image image = SwingFXUtils.toFXImage(mp.tileSet[tileRow][tileCol], null);
                ImageView imageView = new ImageView(image);
                
                //Save the currentTile to the map
                //int type-casting rounds x/y down -> tile placement on the map "grid"
                //This overwrites any previous tile number in the map matrix
                int mapCol = (int) (e.getX() - mp.mapOutline.getX())/mp.tileSize;
                int mapRow = (int) (e.getY() - mp.mapOutline.getY())/mp.tileSize;
                mp.map[mapRow][mapCol] = mp.currentTile;
                
                //Add the selected tile to the screen
                double imageX = mp.mapOutline.getX() + mapCol*mp.tileSize;
                double imageY = mp.mapOutline.getY() + mapRow*mp.tileSize;
                imageView.setX(imageX);
                imageView.setY(imageY);
                imageView.setFitWidth(mp.tileSize);
                imageView.setFitHeight(mp.tileSize);
                mp.updateConstructedMap(imageView);
            }//End if mouse inside map
            
            //Else if mouse inside tilePane
            //Not working yet because tileMap can't gain focus
            //and MapPane loses focus upon mouse leaving
            /*else if (e.getY() > mp.getHeight() - mp.tileSize*mp.numTileRows){
                int col = (int) e.getX()/mp.tileSize;
                int row = (int) e.getY()/mp.tileSize;
                
                mp.currentTile = col + row*col;
                mp.updateCurrentTile();
            }
            */
        }//End if mouse is left button
        
        else if(e.isSecondaryButtonDown()){
            if (e.getX() > mp.mapOutline.getX() &&
                    e.getY() > mp.mapOutline.getY() &&
                    e.getX() < mp.mapOutline.getX() + mp.mapOutline.getWidth() &&
                    e.getY() < mp.mapOutline.getY() + mp.mapOutline.getHeight()){
                //Get the empty image
                Image image = SwingFXUtils.toFXImage(mp.tileSet[0][0], null);
                ImageView imageView = new ImageView(image);
                
                //Save to map
                int mapCol = (int) (e.getX() - mp.mapOutline.getX())/mp.tileSize;
                int mapRow = (int) (e.getY() - mp.mapOutline.getY())/mp.tileSize;
                mp.map[mapRow][mapCol] = 0;
                
                //Add the selected tile to the screen
                double imageX = mp.mapOutline.getX() + mapCol*mp.tileSize;
                double imageY = mp.mapOutline.getY() + mapRow*mp.tileSize;
                imageView.setX(imageX);
                imageView.setY(imageY);
                imageView.setFitWidth(mp.tileSize);
                imageView.setFitHeight(mp.tileSize);
                mp.updateConstructedMap(imageView);
            }//End if mouse inside map
        }//End else if
    }//End handle
    
}