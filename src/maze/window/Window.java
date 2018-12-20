package maze.window;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Window {
    // Swing components
    private final java.awt.Canvas canvas;
    private final JFrame frame;

    public Window(String title, int width, int height){
        // Create Swing canvas
        canvas = new java.awt.Canvas();
        canvas.setFocusable(true);
		canvas.setFocusTraversalKeysEnabled(false);
		canvas.setIgnoreRepaint(true);
		canvas.setBackground(Color.WHITE);

        // Create Swing frame
        JFrame frame = new JFrame(title);
        frame.add(canvas);

        // Handle close request
        final WindowAdapter windowAdapter = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we){
                closeRequested = true;
            }
        }
        frame.addWindowListener(windowAdapter);
        
        // Show frame
        frame.pack();
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setVisible(true);
    }

}
