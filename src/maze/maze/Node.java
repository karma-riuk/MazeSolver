package maze.maze;

import maze.math.Coordinates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    // position
    private Coordinates position;
    // Boolean to know if this cell has been visited or not
    private boolean hasBeenVisited = false;
    // other connections
    private Node connectionNorth;
    private Node connectionSouth;
    private Node connectionEast;
    private Node connectionWest;
    private Node[] children;

    public Node(Coordinates coordinates){
        this.position = coordinates;

        children = new Node[4];
        children[0] = connectionNorth;
        children[1] = connectionWest;
        children[2] = connectionSouth;
        children[3] = connectionEast;
    }

    public List<Node> getChildren(){
        List<Node> ret = new ArrayList<>();
        for (Node node: children){
            if (node != null && node.hasBeenVisited())
                ret.add(node);
        }
        return ret;
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

    public Coordinates getPosition() {
        return position;
    }

    @Override
    public String toString() {
        String ret = "Node at "+position+" conntected to ";
        for (Node n : children){
            if (n != null)
                ret += n.getPosition() + ", ";
        }
        return ret+" | ";
    }
}

