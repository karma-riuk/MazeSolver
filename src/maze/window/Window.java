package maze.window;

import maze.exceptions.ImageException;
import maze.maze.Maze;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Window {
    // Swing components
    private final java.awt.Canvas canvas;
    private final JFrame frame;

    // Frame information
    private final int width;
    private final int height;

    // State information
	private volatile boolean closeRequested;

    /**
     * Create a window with a JFrame and a Canvas
     * @param title (String): the title of the window that is getting created
     * @param width (int): the width of the window
     * @param height (int): the height of the window
     */
    public Window(String title, int width, int height){
        // Setting attributes
        this.width = width;
        this.height = height;

        // Create Swing canvas
        canvas = new java.awt.Canvas();
        canvas.setFocusable(true);
		canvas.setFocusTraversalKeysEnabled(false);
		canvas.setIgnoreRepaint(true);
		canvas.setBackground(Color.decode("#555555"));

        // Create Swing frame
        frame = new JFrame(title);
        frame.add(canvas);

        // Handle close request
        final WindowAdapter windowAdapter = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we){
                closeRequested = true;
            }
        };
        frame.addWindowListener(windowAdapter);
        
        // Show frame
        frame.pack();
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public void begin(Maze maze) throws ImageException{
        int dx, dy;

        maze.setScaleFactor(15);

        BufferedImage img = maze.getImage();

        dx =  width / 2-img.getWidth()/2;
        dy =  height / 2-img.getHeight()/2;

        canvas.getGraphics().drawImage(img, dx, dy, null);


    }


    /**
     * Getter to know if the close request is true or false.
     * @return closeRequested (boolean)
     */
    public boolean isCloseRequested(){
        return closeRequested;
    }
    
    /**
     * Disposes the window, used to end the program when the window was
     * closed, used in maze.main.Main.java
     */
    public void dispose(){
        frame.dispose();
    }
}
