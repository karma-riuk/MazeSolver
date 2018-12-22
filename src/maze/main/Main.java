package maze.main;

import maze.exceptions.MazeException;
import maze.maze.Maze;
import maze.window.Window;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        /*---------------- Variable initialisation ----------------*/
        // info on the window
        int windowWidth = 500;
        int windowHeight = 500;

        int mazeSize = 300;

        Window window = new Window("Main panel", windowWidth, windowHeight);

        Maze maze = new Maze("tiny");

        /*---------------- Code ----------------*/
        try {
            maze.solve();

            window.draw(maze, mazeSize/maze.getWidth());
            while (!window.isCloseRequested()) {
                TimeUnit.MILLISECONDS.sleep(10);

            }
        }
        catch (InterruptedException e){
        }
        finally{
            window.dispose();
        }
    }
}
