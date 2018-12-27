package maze.main;

import maze.exceptions.MazeException;
import maze.maze.Maze;
import maze.window.Window;
import maze.solver.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // TODO: add a "maze-nodes.something" to create nodes faster (I don't know if it will though...)


        /*---------------- Variable initialisation ----------------*/
        // info on the window
//        int windowWidth = ,00;
//        int windowHeight = 900;
        long absStartTime = System.nanoTime();

        // The maze we are solving
        String mazeName = "combo400";

        // the size of the maze on the screen
//        float mazeScaleFactor = .1f;

        // boolean to know if we reduce the quantity of nodes or not
        boolean reduce = false;

        // creating the maze
        Maze maze = new Maze(mazeName, reduce);

        // initializing the solver
        Solver solver = new BreadthFirst();

        // creating the window
        Window window = new Window(mazeName+" with "+solver+" (reduce: "+reduce+")", maze.getNewW()+25, maze.getNewH()+50);

        // List of information to write in the file
        List<String> intel = new ArrayList<>();

        // Variables to know how much time the program took to run
        long startTime;
        long endTime;

        /*------------------------- Code --------------------------*/
        try {
            // Initializing
            System.out.println("Initializing maze...");
            startTime = System.nanoTime();

            solver.initialiaze(maze);

            endTime = System.nanoTime();
            System.out.println("Time taken to initialize the maze: "+((endTime-startTime)/1000000000));
            intel.add("Node count: "+maze.getNodes().size());
            intel.add("");
            intel.add("Time taken to initialize the maze: "+((endTime-startTime)/1000000000)+" seconds");

            // Solving
            System.out.println("Solving...");
            startTime = System.nanoTime();

            solver.solve();

            endTime = System.nanoTime();
            System.out.println("Time taken to solve the maze: "+((endTime-startTime)/1000000000));
            intel.add("Time taken to solve the maze: "+((endTime-startTime)/1000000000)+" seconds");

            // Creating image of solution
            System.out.println("Creating image of solution...");
            startTime = System.nanoTime();

            maze.makeSolvedImage();

            endTime = System.nanoTime();
            System.out.println("Time taken to create the image of the solution: "+((endTime-startTime)/1000000000));
            intel.add("Time taken to create the image of the solution: "+((endTime-startTime)/1000000000)+" seconds");

            File sol = new File("res/"+mazeName+"-solved.png");
            File renamedSol = new File("res/"+mazeName+"/"+solver+".png");
            if (!sol.renameTo(renamedSol))
                sol.delete();

            long absEndTime = System.nanoTime();
            System.out.println("\nTime taken to do every thing: "+ ((absEndTime-absStartTime)/1000000000));
            intel.add("Total time elapsed: "+((absEndTime-absStartTime)/1000000000));

            intel.add("");
            intel.addAll(solver.getIntel());
            String fileName = "res/"+mazeName+"/"+solver+".log";
            File file = new File(fileName);
            file.delete();
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true));
            for (String s : intel) {
                bw.write(s);bw.newLine();
            }
            bw.close();



            while (!window.isCloseRequested()) {
                window.draw(maze);
                TimeUnit.MILLISECONDS.sleep(10);
            }
        }
        catch (InterruptedException e){
            System.out.println("Interuption occured.");
        }
        catch (IOException e){
            System.out.println("File has not been created");
        }
        finally{
            window.dispose();
        }
    }
}
