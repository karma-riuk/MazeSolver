package maze.solver;

import maze.math.Node;
import maze.maze.Maze;

import java.io.IOException;
import java.util.List;

public abstract class Solver {
    List<Node> nodes;
    Node start;
    Node end;
    Maze maze;
    public Solver(){
    }

//    protected Solver(List<Node> maze){
//        this.maze = new Node[maze.size()];
//        for (Node node : maze) {
//            this.maze[maze.indexOf(node)] = node;
//        }
//    }

    public abstract void solve();

    public void initialiaze(Maze maze){
        this.maze = maze;
        System.out.println("Transforming maze into nodes...");
        maze.transform();
        System.out.println("Done transforming.");
        nodes = maze.getNodes();
        start = nodes.get(0);
        end = nodes.get(nodes.size()-1);
    }

    boolean culDeSac(Node node){
        if (node == start || node == end)
            return false;
        for (Node child : node.getChildren()) {
            if (child != null &&  !child.hasBeenVisited()){
                return false;
            }
        }
        return true;
    }

    public abstract List<String> getIntel();
}
