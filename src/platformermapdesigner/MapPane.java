package platformermapdesigner;

import controls.KeyPressedListener;
import controls.KeyReleasedListener;
import controls.MousePressedListener;
import controls.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MapPane extends Pane {
    //Functionality
    //map is the matrix telling which tile to use where
    public int[][] map; //[rows,y][col,x]
    public int mapX; //X,Y location of map on screen
    public int mapY;
    public int mapW; //W,H length of map in #tiles
    public int mapH;
    public Group constructedMap = new Group(); //all the images of the user-constructed map
    public BufferedImage origTileImage;
    public int tileSize;
    public int numTileRows;
    public int numTileColumns;
    //The matrix of tile subimages
    public BufferedImage[][] tileSet; //[rows,y][col,x]
    public int currentTile;
    public Rectangle tileOutline; //which tile is selected
    public Group tileMap = new Group(); //all the images of the tiles aligned correctly
    public Rectangle mapOutline;
    
    //Controls
    public boolean ctrl;
    public boolean shift;
    public boolean alt;
    
    public MapPane(int w, int h, Group tileMap){
        Logger.getGlobal().setLevel(Level.ALL);
        this.tileMap = tileMap;
        tileMap.setFocusTraversable(true);
        this.addEventHandler(KeyEvent.KEY_PRESSED, new KeyPressedListener(this));
        this.addEventHandler(KeyEvent.KEY_RELEASED, new KeyReleasedListener(this));
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, new MousePressedListener(this));
        this.addEventHandler(ScrollEvent.SCROLL, new MouseWheelListener(this));
        this.setFocusTraversable(true);
        this.requestFocus();
        try{
            initTiles();
            initMap();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Loads a map from file: converts the values from the passed
     * int matrix into images given by the tileSet. The value in
     * the map is the tile number. The matrix is passed to this
     * method by {@code loadMap()} in the KeyPressedListener class.
     *  
     * @param oldMap 
     *          int matrix holding tile numbers that correspond
     *          to a specific tile.
     */
    public void loadOldMap(int[][] oldMap){
        constructedMap.getChildren().clear();
        mapX = tileSize;
        mapY = tileSize;
        mapW = oldMap[0].length;
        mapH = oldMap.length;
        mapOutline.setX(mapX);
        mapOutline.setY(mapY);
        mapOutline.setWidth(mapW*tileSize);
        mapOutline.setHeight(mapH*tileSize);
        map = new int[mapH][mapW];
        
        for (int row = 0; row < mapH; row++){
            for (int col = 0; col < mapW; col++){
                currentTile = map[row][col] = oldMap[row][col];
                
                //Get the right tile from tileSet
                int tileRow = 0;
                int tileCol = 0;
                int selectionIndex = currentTile;
                while (selectionIndex > numTileColumns - 1){
                    selectionIndex = selectionIndex - numTileColumns;
                    tileRow++;
                }
                tileCol = selectionIndex;
                Image image = SwingFXUtils.toFXImage(tileSet[tileRow][tileCol], null);
                ImageView imageView = new ImageView(image);
                
                //Add the selected tile to the screen
                double imageX = mapOutline.getX() + col*tileSize;
                double imageY = mapOutline.getY() + row*tileSize;
                imageView.setX(imageX);
                imageView.setY(imageY);
                imageView.setFitWidth(tileSize);
                imageView.setFitHeight(tileSize);
                updateConstructedMap(imageView);
            }
        }
        
        currentTile = 0;
        updateCurrentTile();
    }
    
    /**
     * Updates the constructed map by performing the following operations:
     * 1) Ensures that only one image is seen per constructedMap grid unit,
     *    so it deletes the old tile image that existed in the location
     *    that the user newly clicked to add a new tile image
     * 2) Deletes images outside the map outline.
     * 
     * @param imageView
     *          Image to replace current image in constructedMap
     */
    public void updateConstructedMap(ImageView imageView){
        if (imageView instanceof ImageView){
            Iterator<Node> it = constructedMap.getChildren().iterator();
            while (it.hasNext()){
                //Add new image
                ImageView image = (ImageView) it.next();
                double newImageX = imageView.getX();
                double newImageY = imageView.getY();
                double currentImageX = image.getX();
                double currentImageY = image.getY();
                //Replace old image in same location
                if (newImageX == currentImageX && newImageY == currentImageY){
                    it.remove();
                }
            }
            constructedMap.getChildren().add(imageView);
        }
        else if (imageView == null){
            Iterator<Node> it = constructedMap.getChildren().iterator();
            while (it.hasNext()){
                ImageView image = (ImageView) it.next();
                
                //remove images outside the map
                if (image.getX() < mapOutline.getX() ||
                        image.getX() > mapOutline.getX()+mapOutline.getWidth()-tileSize ||
                        image.getY() < mapOutline.getY() ||
                        image.getY() > mapOutline.getY()+mapOutline.getHeight()-tileSize){
                    it.remove();
                }
            }
        }
        
        
        /*
        //Check if it's changing the map correctly
        System.out.println("Changing");
        for (int row = 0; row < map.length; row++){
            System.out.print("| ");
            for (int col = 0; col < map[0].length; col++){
                System.out.print(map[row][col] + " ");
            }
            System.out.print("|\n");
        }
        System.out.println();
        */
    }
    
    /**
     * Repositions the tileOutline in the tile selection pane
     * at the bottom of the screen so that the user knows which
     * tile is selected.
     */
    public void updateCurrentTile(){
        //Notice how newTileX/Y don't have to getX/Y before updating
        //That's because they're in their own pane due to the BorderPane layout
        double newTileX = ((currentTile % numTileColumns)*tileSize);
        double newTileY = (((int) currentTile/numTileColumns)*tileSize);
        tileOutline.setX(newTileX);
        tileOutline.setY(newTileY);
    }
    
    /**
     * Instantiates the tileSet images using an image selected
     * by the user. Given the number of rows that the user
     * input at the program startup, this automatically calculates
     * the positions of the individual tile images, and sets the
     * tileSize for all tiles.
     * 
     * Note: tiles must be designed to be squares.
     * 
     * @param file
     *          The image file containing what will be the map tiles
     */
    public void openTileSet(File file){
        try {
            origTileImage = ImageIO.read(file);
            tileSize = (int) origTileImage.getHeight()/numTileRows;
            //Minus 1 because tileSet matrix starts at index 0
            numTileColumns = (int) origTileImage.getWidth()/tileSize;
            tileSet = new BufferedImage[numTileRows][numTileColumns];
            for (int row = 0; row < numTileRows; row++) {
                for (int col = 0; col < numTileColumns; col++) {
                    tileSet[row][col] = origTileImage.getSubimage(
                            col * tileSize, row * tileSize,
                            tileSize, tileSize);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        //add tileSet to tileMap so that it is shown on screen
        for (int i = 0; i < numTileRows; i++){
            for (int j = 0; j < numTileColumns; j++){
                Image image = SwingFXUtils.toFXImage(tileSet[i][j], null);
                ImageView imageView = new ImageView(image);
                imageView.setX(j*tileSize);
                imageView.setY(i*tileSize);
                imageView.setFitWidth(tileSize);
                imageView.setFitHeight(tileSize);
                tileMap.getChildren().add(imageView);
            }
        }
        //Make the first tile selected
        currentTile = 0;
        double tx = ((ImageView) tileMap.getChildren().get(0)).getX();
        double ty = ((ImageView) tileMap.getChildren().get(0)).getY();
        double tw = ((ImageView) tileMap.getChildren().get(0)).getFitWidth();
        double th = ((ImageView) tileMap.getChildren().get(0)).getFitHeight();
        tileOutline = new Rectangle(tx, ty, tw, th);
        tileOutline.setStroke(Color.RED);
        tileOutline.setFill(null);
        tileMap.getChildren().add(tileOutline);
    }
    
    /**
     * Sets up the map area using the tile size initialized
     * in the {@code openTileSet()} method.
     */
    public void initMap(){
        int mapStartSize = numTileColumns - 2;
        map = new int[(int)mapStartSize/2][mapStartSize];
        for (int i = 0; i < (int)mapStartSize/2; i++){
            for (int j = 0; j < mapStartSize; j++){
                map[i][j] = 0;
            }
        }
        mapX = tileSize;
        mapY = tileSize;
        mapW = mapStartSize*tileSize;
        mapH = (int)mapStartSize/2*tileSize;
        mapOutline = new Rectangle(tileSize, tileSize, mapW, mapH);
        mapOutline.setStroke(Color.RED);
        this.getChildren().addAll(mapOutline, constructedMap);
    }
    
    /**
     * Gives the user the option to input the number of rows
     * in their image containing the map tiles and initiates
     * the creation of the tileSet.
     */
    public void initTiles(){
        int answer = JOptionPane.showConfirmDialog(null,
                "Is your tile image comprised of square tiles,\n"
                + "and does it have a blank first square?");
        if (answer != 0){
            System.exit(0);
        }
        
        numTileRows = Integer.parseInt(JOptionPane.showInputDialog("How many rows (y) in tile image?"));
        
        JFileChooser chooser = new JFileChooser("."){
            @Override
            public void approveSelection(){
                File choice = this.getSelectedFile();
                if (choice.isDirectory()){
                    this.setCurrentDirectory(choice);
                }
                else{
                    super.approveSelection();
                }
            }
        };
        
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(new FileNameExtensionFilter("Images: PNG or GIF",
                "png", "gif"));
        
        int fileChoice = chooser.showOpenDialog(null);
        if (fileChoice == JFileChooser.APPROVE_OPTION){
            openTileSet(chooser.getSelectedFile());
        }
    }
    
}//End MapPane class