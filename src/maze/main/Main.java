package maze.main;

import maze.exceptions.MazeException;
import maze.maze.Maze;
import maze.window.Window;
import maze.solver.*;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        /*---------------- Variable initialisation ----------------*/
        // info on the window
//        int windowWidth = 900;
//        int windowHeight = 900;
        long absStartTime = System.nanoTime();

        // The maze we are solving
        String mazeName = "perfect2k";

        // the size of the maze on the screen
//        float mazeScaleFactor = .1f;

        // boolean to know if we reduce the quantity of nodes or not
        boolean reduce = true;

        // creating the maze
        Maze maze = new Maze(mazeName, reduce);

        // creating the window
        Window window = new Window(mazeName+" (reduce: "+reduce+")", maze.getNewW()+25, maze.getNewH()+50);

        // initializing the solver
        Solver solver = new DepthFirst();

        // Variables to know how much time the program took to run
        long startTime;
        long endTime;

        /*------------------------- Code --------------------------*/
        try {
            System.out.println("Initializing maze...");
            startTime = System.nanoTime();
            solver.initialiaze(maze);
            endTime = System.nanoTime();
            System.out.println("Time taken to initialize the maze: "+((endTime-startTime)/1000000000));

            System.out.println("Solving...");
            startTime = System.nanoTime();
            solver.solve();
            endTime = System.nanoTime();
            System.out.println("Time taken to solve the maze: "+((endTime-startTime)/1000000000));

            System.out.println("Creating image of solution...");
            startTime = System.nanoTime();
            maze.makeSolvedImage();
            endTime = System.nanoTime();
            System.out.println("Time taken to create the image of the solution: "+((endTime-startTime)/1000000000));

            File sol = new File("res/"+mazeName+"-solved.png");
            File renamedSol = new File("res/"+mazeName+"/"+solver+".png");
            sol.renameTo(renamedSol);

            long absEndTime = System.nanoTime();
            System.out.println("\nTime taken to do every thing: "+ ((absEndTime-absStartTime)/1000000000));

            while (!window.isCloseRequested()) {
                window.draw(maze);
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
