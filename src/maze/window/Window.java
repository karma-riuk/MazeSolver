package maze.window;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Window {
    // Swing components
    private final java.awt.Canvas canvas;
    private final JFrame frame;

    // State information
	private volatile boolean closeRequested;

    /**
     * Creat a window with a JFrame and a Canvas
     * @param title (String): the title of the window that is getting created
     * @param width (int): the width of the window
     * @param height (int): the height of the window
     */
    public Window(String title, int width, int height){
        // Create Swing canvas
        canvas = new java.awt.Canvas();
        canvas.setFocusable(true);
		canvas.setFocusTraversalKeysEnabled(false);
		canvas.setIgnoreRepaint(true);
		canvas.setBackground(Color.WHITE);

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

    /**
     * Getter for the canvas, used in  maze.main.Main.java in order to add images to the
     * window
     * @return this.canvas (Canvas)
     */
    public java.awt.Canvas getCanvas(){
        return this.canvas;
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
