package maze.solver;

import maze.math.Node;
import maze.math.Coordinates;
import maze.math.PriorityList;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.lang.Math;

public class AStar extends Solver{
    private PriorityList priorityList;
    private List<Node> previousNodes;
    private List<Node> queue;
    private List<Coordinates> sol;
    private int nodesExplored;

    public AStar(){
        priorityList = new PriorityList();
        previousNodes = new ArrayList<>();
        queue = new ArrayList<>();
        sol = new ArrayList<>();
        nodesExplored = 0;
    }


    @Override
    public void solve(){
        initWeights(nodes, end);
        priorityList.add(start);
        queue.add(start);
        previousNodes.add(null);
        Node current = null; 
        while (current != end){
            nodesExplored ++;
            current = priorityList.pop(0);
            current.setHasBeenVisited(true);
            for (Node child : current.getChildren()){
                if (child != null && !child.hasBeenVisited()){
                    priorityList.add(child);
                    queue.add(child);
                    previousNodes.add(current);
                }
            }
        }

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
        maze.addToSolution(sol);
        maze.makeFullSolution();
    }

    /**
     * Set weights for every node present in the parametre nodes<br>
     * It calls the "calculateWeights()" method to set the weight <br>
     * The variable endNode is simply the last node 
     *
     */
    private void initWeights(List<Node> nodes){
        initWeights(nodes, nodes.get(nodes.size()-1));

    }

    private void initWeights(List<Node> nodes, Node endNode){
        for (Node node : nodes) {
            node.setWeight(calculateWeight(node, endNode));
        }
    }

    private double calculateWeight(Node node, Node end){
        int a = end.getPosition().getX() -node.getPosition().getX();
        int b = end.getPosition().getY() -node.getPosition().getY();
        return Math.sqrt(a*a+b*b);
    }
    
    @Override
    public String toString(){
        return "A-Star";
    }
    public List<String> getIntel(){
        List<String> ret = new ArrayList<>();
        ret.add("Solution size: "+ sol.size());
        ret.add("Nodes explored: "+nodesExplored);
        return ret;
    }
}
