package maze.main;

import maze.exceptions.MazeException;
import maze.maze.Maze;
import maze.window.Window;
import maze.solver.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        /*---------------- Variable initialisation ----------------*/
        // info on the window
        int windowWidth = 500;
        int windowHeight = 500;

        // the size of the maze on the screen
        int mazeSize = 300;

        // creating the window
        Window window = new Window("Main panel", windowWidth, windowHeight);

        // creating the maze
        Maze maze = new Maze("tiny");

        // initializing the solver
        Solver solver = new LeftTurn();

        /*------------------------- Code --------------------------*/
        try {
            solver.initialiaze(maze);

            solver.solve();

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
