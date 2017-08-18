package controls;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import platformermapdesigner.MapPane;

public class KeyPressedListener implements EventHandler<KeyEvent>{
    private final MapPane mp;
    
    public KeyPressedListener(MapPane mp){
        this.mp = mp;
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | 
                IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.err.println(ex.getClass());
        }
    }
    
    /**
     * Adds keyboard controls to the program.
     * 
     * @param k 
     *          The key pressed
     */
    @Override
    public void handle(KeyEvent k) {
        if (k.getCode() == KeyCode.CONTROL){
            mp.ctrl = true;
        }
        if (k.getCode() == KeyCode.SHIFT){
            mp.shift = true;
        }
        if (k.getCode() == KeyCode.ALT){
            mp.alt = true;
        }
        if (k.getCode() == KeyCode.UP){
            if (mp.shift){
                growMapRowUp();
            }
            else if (mp.alt){
                shrinkMapRowUp();
            }
            else{
                moveMap("Up");
            }
        }
        else if (k.getCode() == KeyCode.RIGHT){
            if (mp.shift){
                growMapColRight();
            }
            else if (mp.alt){
                shrinkMapColRight();
            }
            else{
                moveMap("Right");
            }
        }
        else if (k.getCode() == KeyCode.DOWN){
            if (mp.shift){
                growMapRowDown();
            }
            else if (mp.alt){
                shrinkMapRowDown();
            }
            else{
                //A strange thing happens where the map gets stuck
                //after moving down until it is moved up again.
                //I haven't figured out the cause;
                moveMap("Down");
            }
        }
        else if (k.getCode() == KeyCode.LEFT){
            if (mp.shift){
                growMapColLeft();
            }
            else if (mp.alt){
                shrinkMapColLeft();
            }
            else{
                moveMap("Left");
            }
        }
        if (k.getCode() == KeyCode.O){
            if (mp.ctrl){
                loadMap();
            }
        }
        if (k.getCode() == KeyCode.S){
            if (mp.ctrl){
                saveMap();
            }
        }
        if (k.getCode() == KeyCode.E) {
            if (mp.ctrl){
                exportMapImage();
            }
        }
        if (k.getCode() == KeyCode.C) {
            if (mp.ctrl){
                mp.clear();
            }
        }
    }
    
    /**
     * Moves the entire map, including the outline and the
     * containing tiles, in the direction specified by the user.
     * 
     * @param direction 
     *          Direction that the map will move
     */
    public void moveMap(String direction){
        int dx = 0;
        int dy = 0;
        switch(direction){
            case "Up":
                dy = -1;
                break;
            case "Right":
                dx = 1;
                break;
            case "Down":
                dy = 3; //Extra large to counteract the map getting stuck
                break;
            case "Left":
                dx = -1;
                break;
            default:
                //Do nothing
                break;
        }//End switch
        mp.mapX = (int) (mp.mapOutline.getX() + dx*mp.tileSize);
        mp.mapY = (int) (mp.mapOutline.getY() + dy*mp.tileSize);
        mp.mapOutline.setX(mp.mapX);
        mp.mapOutline.setY(mp.mapY);
        for (Node n : mp.constructedMap.getChildren()){
            ImageView image = (ImageView) n;
            image.setX(image.getX() + dx*mp.tileSize);
            image.setY(image.getY() + dy*mp.tileSize);
        }
    }
    
    /**
     * Extends the map from the right.
     */
    public void growMapColRight(){
        int oldRow = mp.map.length;
        int oldCol = mp.map[0].length;
        int newRow = oldRow;
        int newCol = oldCol + 1;
        int[][] newMap = new int[newRow][newCol];
        for (int row = 0; row < oldRow; row++){
            for (int col = 0; col < oldCol; col++){
                newMap[row][col] = mp.map[row][col];
            }
            newMap[row][newCol - 1] = 0;
        }
        mp.map = newMap;
        mp.mapW = mp.mapW + mp.tileSize;
        mp.mapOutline.setWidth(mp.mapOutline.getWidth() + mp.tileSize);
    }
    
    /**
     * Extends the map from the left.
     */
    public void growMapColLeft(){
        int oldRow = mp.map.length;
        int oldCol = mp.map[0].length;
        int newRow = oldRow;
        int newCol = oldCol + 1;
        int[][] newMap = new int[newRow][newCol];
        for (int row = 0; row < oldRow; row++){
            for (int col = 0; col < oldCol; col++){
                newMap[row][col+1] = mp.map[row][col];
            }
            newMap[row][0] = 0;
        }
        mp.map = newMap;
        mp.mapW = mp.mapW + mp.tileSize;
        mp.mapOutline.setWidth(mp.mapOutline.getWidth() + mp.tileSize);
        mp.mapOutline.setX(mp.mapOutline.getX() - mp.tileSize);
    }
    
    /**
     * Extends the map from the top.
     */
    public void growMapRowUp(){
        int oldRow = mp.map.length;
        int oldCol = mp.map[0].length;
        int newRow = oldRow + 1;
        int newCol = oldCol;
        int[][] newMap = new int[newRow][newCol];
        for (int row = 0; row <= oldRow; row++){
            for (int col = 0; col < oldCol; col++){
                if (row < oldRow){
                    newMap[row+1][col] = mp.map[row][col];
                }
                else if(row == oldRow){
                    newMap[0][col] = 0;
                }
            }
        }
        mp.map = newMap;
        mp.mapH = mp.mapH + mp.tileSize;
        mp.mapOutline.setHeight(mp.mapOutline.getHeight() + mp.tileSize);
        mp.mapOutline.setY(mp.mapOutline.getY() - mp.tileSize);
    }
    
    /**
     * Extends the map from the bottom.
     */
    public void growMapRowDown(){
        int oldRow = mp.map.length;
        int oldCol = mp.map[0].length;
        int newRow = oldRow + 1;
        int newCol = oldCol;
        int[][] newMap = new int[newRow][newCol];
        for (int row = 0; row < newRow; row++){
            for (int col = 0; col < oldCol; col++){
                if (row < oldRow){
                    newMap[row][col] = mp.map[row][col];
                }
                else if (row == oldRow){
                    newMap[row][col] = 0;
                }
            }
        }
        mp.map = newMap;
        mp.mapH = mp.mapH + mp.tileSize;
        mp.mapOutline.setHeight(mp.mapOutline.getHeight() + mp.tileSize);
    }
    
    /**
     * Shrinks the map from the right.
     */
    public void shrinkMapColRight(){
        int oldRow = mp.map.length;
        int oldCol = mp.map[0].length;
        int newRow = oldRow;
        int newCol = oldCol - 1;
        int[][] newMap = new int[newRow][newCol];
        for (int row = 0; row < newRow; row++){
            for (int col = 0; col < newCol; col++){
                newMap[row][col] = mp.map[row][col];
            }
        }
        mp.map = newMap;
        mp.mapW = mp.mapW - mp.tileSize;
        mp.mapOutline.setWidth(mp.mapOutline.getWidth() - mp.tileSize);
        //delete outside tiles
        mp.updateConstructedMap(null);
    }
    
    /**
     * Shrinks the map from the left.
     */
    public void shrinkMapColLeft(){
        int oldRow = mp.map.length;
        int oldCol = mp.map[0].length;
        int newRow = oldRow;
        int newCol = oldCol - 1;
        int[][] newMap = new int[newRow][newCol];
        for (int row = 0; row < newRow; row++){
            for (int col = 0; col < newCol; col++){
                newMap[row][col] = mp.map[row][col+1];
            }
        }
        mp.map = newMap;
        mp.mapW = mp.mapW - mp.tileSize;
        mp.mapOutline.setWidth(mp.mapOutline.getWidth() - mp.tileSize);
        mp.mapOutline.setX(mp.mapOutline.getX() + mp.tileSize);
        mp.updateConstructedMap(null);
    }
    
    /**
     * Shrinks the map from the top.
     */
    public void shrinkMapRowUp(){
        int oldRow = mp.map.length;
        int oldCol = mp.map[0].length;
        int newRow = oldRow - 1;
        int newCol = oldCol;
        int[][] newMap = new int[newRow][newCol];
        for (int row = 0; row < newRow; row++){
            for (int col = 0; col < newCol; col++){
                newMap[row][col] = mp.map[row+1][col];
            }
        }
        mp.map = newMap;
        mp.mapH = mp.mapH - mp.tileSize;
        mp.mapOutline.setHeight(mp.mapOutline.getHeight() - mp.tileSize);
        mp.mapOutline.setY(mp.mapOutline.getY() + mp.tileSize);
        mp.updateConstructedMap(null);
    }
    
    /**
     * Shrinks the map from the bottom.
     */
    public void shrinkMapRowDown(){
        int oldRow = mp.map.length;
        int oldCol = mp.map[0].length;
        int newRow = oldRow - 1;
        int newCol = oldCol;
        int[][] newMap = new int[newRow][newCol];
        for (int row = 0; row < newRow; row++){
            for (int col = 0; col < newCol; col++){
                newMap[row][col] = mp.map[row][col];
            }
        }
        mp.map = newMap;
        mp.mapH = mp.mapH - mp.tileSize;
        mp.mapOutline.setHeight(mp.mapOutline.getHeight() - mp.tileSize);
        mp.updateConstructedMap(null);
    }
    
    /**
     * Gives the user the option to save their created map in
     * a location specified by them.
     */
    public void saveMap(){
        File saveFile = displayMapChooser();
        if (saveFile == null) {
            return;
        }
        String path = saveFile.getAbsolutePath();
        if (!path.endsWith(".map")) {
            path = path + ".map";
            saveFile = new File(path);
        }
        try (Writer writer = new BufferedWriter(new FileWriter(saveFile))) {
            //write row length first, then column length
            writer.write(mp.map.length + "\n");
            writer.write(mp.map[0].length + "\n");
            for (int row = 0; row < mp.map.length; row++) {
                for (int col = 0; col < mp.map[0].length; col++) {
                    writer.write(String.valueOf(mp.map[row][col]) + " ");
                }
                writer.write("\n");
            }
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Gives the user an option to load a previously-created
     * map.
     */
    public void loadMap(){
        File file = displayMapChooser();
        if (file == null) {
            return;
        }
        int[][] tempMap;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            //instantiate the map matrix
            int mapHeight = Integer.parseInt(reader.readLine());
            int mapWidth = Integer.parseInt(reader.readLine());
            tempMap = new int[mapHeight][mapWidth];

            //fill the map matrix
            for (int row = 0; row < mapHeight; row++) {
                int col = 0;
                String line = reader.readLine();
                String[] nums = line.split("\\s+");
                for (String s : nums) {
                    int num = Integer.parseInt(s);
                    tempMap[row][col] = num;
                    col++;
                }
            }
            reader.close();

            //load map into mapPane
            mp.loadOldMap(tempMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private File displayMapChooser() {
        return displayFileChooser(".map files", "map");
    }
    
    private File displayPictureChooser() {
        return displayFileChooser(".png files", "png");
    }
    
    private File displayFileChooser(String desc, String... ext) {
        JFileChooser chooser = new JFileChooser("."){
            @Override
            public void approveSelection(){
                if (this.getSelectedFile().isDirectory()){
                    this.setCurrentDirectory(this.getSelectedFile());
                }
                else{
                    super.approveSelection();
                }
            }
        };
        chooser.setApproveButtonText("Select");
        chooser.setFileFilter(new FileNameExtensionFilter(desc, ext));
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int fileChoice = chooser.showOpenDialog(null);
        if (fileChoice == JFileChooser.APPROVE_OPTION){
            return chooser.getSelectedFile();
        }
        return null;
    }
    
    public void exportMapImage() {
        File file = displayPictureChooser();
        if (file == null) {
            return;
        }
        String path = file.getAbsolutePath();
        if (!path.endsWith(".png")) {
            path += ".png";
            file = new File(path);
        }
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);  //set transparent background
        WritableImage snapshot = mp.constructedMap.snapshot(params, null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
