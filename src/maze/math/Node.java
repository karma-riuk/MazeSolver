package maze.math;

import java.util.ArrayList;
import java.util.List;

public class Node {
    // position
    private Coordinates position;
    // Boolean to know if this cell has been visited or not
    private boolean hasBeenVisited = false;
    // other connections
    private Node connectionNorth = null;
    private Node connectionSouth = null;
    private Node connectionEast = null;
    private Node connectionWest = null;
    private Node[] children;
    // weight for A-Star solve, default to 1 so that it doesn't interfear with
    // the other solvers
    private double weight = 1d;

    public Node(Coordinates coordinates){
        this.position = coordinates;

        children = new Node[4];
//        children[0] = connectionNorth;
//        children[1] = connectionEast;
//        children[2] = connectionSouth;
//        children[3] = connectionWest;
        children[0] = connectionNorth;
        children[1] = connectionWest;
        children[2] = connectionSouth;
        children[3] = connectionEast;
    }

    public Node[] getChildren(){
//        List<Node> ret = new ArrayList<>();
//        for (Node node: children){
////            if (node != null && node.hasBeenVisited())
//                ret.add(node);
//        }
//        return ret;
        return children;
    }

    public void setHasBeenVisited(boolean hasBeenVisited) {
        this.hasBeenVisited = hasBeenVisited;
    }

    public boolean hasBeenVisited(){
        return hasBeenVisited;
    }

    public void setConnectionEast(Node connectionEast) {
        this.connectionEast = connectionEast;
        this.children[3] = connectionEast;
    }

    public void setConnectionNorth(Node connectionNorth) {
        this.connectionNorth = connectionNorth;
        this.children[0] = connectionNorth;
    }

    public void setConnectionSouth(Node connectionSouth) {
        this.connectionSouth = connectionSouth;
        this.children[2] = connectionSouth;
    }

    public void setConnectionWest(Node connectionWest) {
        this.connectionWest = connectionWest;
        this.children[1] = connectionWest;
    }

    public void removeConnectionWithIndex(int index){
        this.children[index] = null;
    }

    public Coordinates getPosition() {
        return position;
    }

    /**
     * Setter for the wieght, since it's defaulted to 1, A-Star needs to reset
     * to the accurate value
     */
    public void setWeight(double weight){
        this.weight = weight;
    }

    /**
     * Getter for the weight, used by maze.solver.AStar
     * @return weight (float)
     */
    public double getWeight(){
        return weight;
    }

    @Override
    public String toString() {
        String ret = "Node at "+position+" conntected to ";
        for (Node n : children){
            if (n != null)
                ret += n.getPosition() + " ";
        }
        return ret+" | ";
//        return position.toString();
    }
}

