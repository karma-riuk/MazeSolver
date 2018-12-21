package maze.main;

import maze.maze.Maze;
import maze.window.Window;

public class Main {
    public static void main(String[] args) {
        /*---------------- Variable declaration ----------------*/
        // info on the window
        int width, height;
        Window window;
        Maze maze;

        /*---------------- Variable initialisation ----------------*/
        width = 500;
        height = 500;

        window = new Window("Main panel", width, height);

        maze = new Maze("tiny");

        /*---------------- Code ----------------*/
        try{
            window.begin(maze);

            while(!window.isCloseRequested()){
                int a=0;
            }
        }
        finally{
            window.dispose();
        }
    }
}
