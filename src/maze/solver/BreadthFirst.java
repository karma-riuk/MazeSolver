package maze.solver;

import maze.math.Coordinates;
import maze.math.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BreadthFirst extends Solver{
    private List<Node> queue;
    private List<Node> previousNodes;
    private List<Coordinates> sol;

    public BreadthFirst(){
        this.queue = new ArrayList<>();
        this.previousNodes = new ArrayList<>();
        this.sol = new ArrayList<>();
    }


    @Override
    public void solve(){
        queue.add(start);
        previousNodes.add(null);

        start.setHasBeenVisited(true);
        Node current = null;
        while (current != end) {
            current =  queue.get(queue.indexOf(current)+1);
            for (Node child : current.getChildren()) {
                if (child != null && !child.hasBeenVisited() && !culDeSac(child)){
                    child.setHasBeenVisited(true);
                    queue.add(child);
                    previousNodes.add(current);
                }
            }
        }
        System.out.println("solution found");

        LinkedList<Node> nodeSol = new LinkedList<>();
        Node curSol = end;
        while (curSol != start){
            nodeSol.addFirst(curSol);
            curSol = previousNodes.get(queue.indexOf(curSol));
        }

        nodeSol.addFirst(start);

        for (Node node : nodeSol) {
            sol.add(node.getPosition());
        }

        System.out.println("All registered nodes before find the end: "+queue.size());
        System.out.println("Nodes explored: "+queue.indexOf(end));
        System.out.println("Solution length: "+sol.size());
        maze.addToSolution(sol);
        maze.makeFullSolution();
    }

    public List<String> getIntel(){
        List<String> ret = new ArrayList<>();
        ret.add("Solution size: "+sol.size());
        ret.add("Nodes explored: "+queue.indexOf(end));
        return ret;
    }


    @Override
    public String toString() {
        return "BreadthFirst";
    }
}
