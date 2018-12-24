package maze.solver;

import maze.math.Coordinates;
import maze.math.Node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BreadthFirst extends Solver{
    private List<Node> queue;
    private List<Node> previousNodes;
//    private List<BreadthNode> bNodes;
//    private BreadthNode start;
//    private BreadthNode end;

    public BreadthFirst(){
        this.queue = new ArrayList<>();
        this.previousNodes = new ArrayList<>();
//        this.bNodes = new ArrayList<>();
    }


    @Override
    public void solve(){
//        transformNodes();
//
//        queue.add(start);
//        start.setHasBeenVisited(true);
//        BreadthNode current = null;
//        while (current != end) {
//            current =  queue.get(queue.indexOf(current)+1);
//            System.out.println("currently on: "+current);
//            for (BreadthNode child : current.getChildren()) {
//                System.out.println(child);
//                if (child != null && !child.hasBeenVisited()){
//                    child.setHasBeenVisited(true);
//                    child.setParent(current);
//                    queue.add(child);
//                }
//            }
//            if (current == end)
//                break;
//        }
//        List<BreadthNode> sol = new LinkedList<>();
//        BreadthNode asdf = end;
//        while(asdf != start){
//            sol.add(asdf.getParent());
//            asdf = asdf.getParent();
//        }
//        System.out.println("solution found");
//        System.out.println(sol);

        queue.add(start);
        previousNodes.add(null);

        start.setHasBeenVisited(true);
        Node current = null;
        int count = 0;
        while (current != end) {
            count++;
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

        List<Coordinates> sol = new ArrayList<>();
        for (Node node : nodeSol) {
            sol.add(node.getPosition());
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("res/"+maze.getName() + "/" + this + ".log", true));
//            bw.write("Nodes explored: "+queue);
            bw.write("Solution length: "+sol.size()); bw.newLine();
            bw.close();
        }
        catch (IOException e){
        }

        System.out.println("Nodes explored: "+queue.size());
        System.out.println("Solution length: "+sol.size());
        maze.addToSolution(sol);
        maze.makeFullSolution();
    }

    @Override
    public String toString() {
        return "BreadthFirst";
    }
}
