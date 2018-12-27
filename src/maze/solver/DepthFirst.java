package maze.solver;

import maze.math.Coordinates;
import maze.math.Node;
import maze.math.Orientation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DepthFirst extends Solver{
    private List<Node> previousNodes;
    private List<Orientation> previousOrientation;
    private int exploredNodes;
    private List<Coordinates> sol;

    public DepthFirst(){
        this.previousNodes = new ArrayList<>();
        this.previousOrientation = new ArrayList<>();
        this.exploredNodes = 0;
        this.sol = new ArrayList<>();
    }


    @Override
    public void solve(){
        Node curent = start;

        while (curent != end) {
//            System.out.println(curent);
            curent.setHasBeenVisited(true);
            if (curent != start && curent != end && culDeSac(curent)) {
                previousNodes.remove(curent);
                curent = previousNodes.get(previousNodes.size() - 1);
            }
            for (Node child: curent.getChildren()){
                if (child != null && !child.hasBeenVisited()) {
                    if (child == end){
                        previousNodes.add(curent);
                        previousNodes.add(child);
                        exploredNodes++;
                        exploredNodes++;
                        curent = end;
                        break;
                    }
                    previousNodes.add(curent);
                    exploredNodes++;
                    curent = child;
                    break;
                }
            }
        }

        for (Node node : previousNodes) {
            sol.add(node.getPosition());
        }

        System.out.println("Nodes explored: "+exploredNodes);
        System.out.println("Solution length: "+ sol.size());
        maze.addToSolution(sol);
        maze.makeFullSolution();
    }

    private boolean goDeep(Node node) {
        System.out.println("Trying "+node);
        previousNodes.add(node);
        node.setHasBeenVisited(true);
        if (node == end)
            return true;
        if (culDeSac(node)) {
            System.out.println("Didn't work");
            previousNodes.remove(node);
            return false;
        }

        for (Node child : node.getChildren()) {
            if (child != null && !child.hasBeenVisited()) {
                if (goDeep(child))
                    return true;
                else
                    previousNodes.remove(child);
            }
        }
        return false;
    }

    public List<String> getIntel(){
        List<String> ret = new ArrayList<>();
        ret.add("Solution size: "+sol.size());
        ret.add("Nodes explored: "+exploredNodes);
        return ret;
    }

    @Override
    public String toString() {
        return "Depth-First";
    }
}
