package maze.maze;

import maze.exceptions.ImageException;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Maze {
    /// Maze information
    // his name
    private String name;
    // width
    private int width;
    // height
    private int height;
    // scale factor
    private int scaleFactor = 1;
    // image
    private BufferedImage image;

    public Maze(String name) throws ImageException{
        this.name = name;

        try{
            image = ImageIO.read(new File("res/"+name+".png"));
        }catch (IOException e){
            throw new ImageException();
        }
        width = image.getWidth();
        height = image.getHeight();
    }

    /**
     * Getter for the representation of the maze scale by the scaleFactor
     * @return image (BufferedImage)
     */
    public BufferedImage getImage(){
        int newW = width*scaleFactor;
        int newH = height*scaleFactor;
        Image tmp = image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    public void setScaleFactor(int scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
}
