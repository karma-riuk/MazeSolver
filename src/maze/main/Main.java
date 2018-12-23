package maze.main;

import maze.window.Window;

import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.Graphics;

public class Main {
    public static void main(String[] args) {
        int width, height;
        width = 750;
        height = 750;


        Window window = new Window("Main panel", width, height);
        Graphics graphics = window.getCanvas().getGraphics();

        BufferedImage img = null;
        try{
            img = ImageIO.read(new File("res/maze.png"));
        }catch (Exception e){
            System.out.println("ERROR: image not found");
        }

        int dx, dy; 
        dx = (int) width / 2-img.getWidth()/2;
        dy = (int) height / 2-img.getHeight()/2;

        graphics.drawImage(img, dx, dy, null);
        try{
            
            while(!window.isCloseRequested()){
            }
        }
        finally{
            window.dispose();
        }
    }
}
