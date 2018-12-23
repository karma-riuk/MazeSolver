package maze.solver;

import maze.math.Coordinates;
import maze.math.Node;
import maze.math.Orientation;

import java.util.ArrayList;
import java.util.List;

public class LeftTurn extends Solver{
    private List<Node> previousNodes;
    private List<Orientation> previousOrientation;

    public LeftTurn(){
        previousNodes = new ArrayList<>();
        previousOrientation = new ArrayList<>();
    }

    @Override
    public void solve() {
        System.out.println("Possible nodes to explore: "+nodes.size());
        // start by going left after the start
//        goLeft(start, Orientation.SOUTH);

        previousNodes.add(start);
        previousOrientation.add(Orientation.SOUTH);

        Node current = start.getChildren()[2];
        Orientation curOrientation = leftOf(Orientation.SOUTH);
        previousNodes.add(current);
        previousOrientation.add(curOrientation);

//        curOrientation = Orientation.EAST;
//        previousOrientation.add(curOrientation);
        Node leftNode;
        Node lastNode;
        Orientation lastOrientation;

        while (true){
            current.setHasBeenVisited(true);
            if (previousNodes.size() > 0) {
                lastOrientation = previousOrientation.get(previousOrientation.size()-1);
                lastNode = previousNodes.get(previousNodes.size()-1);
            }else{
                lastNode = null;
                lastOrientation = null;
            }

            leftNode = current.getChildren()[curOrientation.toNumber()];

            if (current == end){
                previousNodes.add(current);
                break;
            }

            if (culDeSac(current)){
                previousNodes.remove(lastNode);
                previousOrientation.remove(lastOrientation);
                current = lastNode;
                curOrientation = leftOf(lastOrientation);
                continue;
            }
            if (lastNode != current){
                previousNodes.add(current);
                previousOrientation.add(curOrientation);
            }
            else{
                previousOrientation.remove(lastOrientation);
                previousOrientation.add(curOrientation);
            }
            if (leftNode == null || leftNode.hasBeenVisited()){
                curOrientation = leftOf(curOrientation);
                continue;
            }
            current = leftNode;
            curOrientation = leftOf(curOrientation);
        }

        System.out.println("Nodes from start to end: "+previousNodes.size());

        // add the solution to the maze
        List<Coordinates> sol = new ArrayList<>();
        for (Node node : previousNodes) {
            sol.add(node.getPosition());
        }
        maze.addToSolution(sol);
        maze.makeFullSolution();
    }


    private  Orientation leftOf(Orientation o){
        for (Orientation orientation : Orientation.values()) {
            if ((o.toNumber()+1)%4 == orientation.toNumber()){
                return orientation;
            }
        }
        return null;
    }

    private boolean goLeft(Node node, Orientation orientation){
        assert orientation != null && node != null;

        node.setHasBeenVisited(true);
        Node leftNode = node.getChildren()[orientation.toNumber()];
        Node lastNode;
        Orientation lastOrientaion;

        if (previousNodes.size() > 0) {
            lastNode = previousNodes.get(previousNodes.size() - 1);
            lastOrientaion = previousOrientation.get(previousOrientation.size() - 1);
        }else{
            lastNode = null;
            lastOrientaion = null;
        }

        if (leftNode != null && leftNode.equals(end)) {
            previousNodes.add(leftNode);
            return true;
        }
        else{
            if (culDeSac(node)) {
                previousNodes.remove(lastNode); // remove last node since it's a cul-de-sac
                previousOrientation.remove(lastOrientaion); // remove last orientation since it's a cul-de-sac
                return goLeft(lastNode, leftOf(lastOrientaion));
//                return false;
            }
            else {
                if (lastNode != node) {
                    previousNodes.add(node);
                    previousOrientation.add(orientation);
                }
                if (lastNode == node) { // if we are on the same node again, it means we are trying a different orientation
                    previousOrientation.remove(lastOrientaion);
                    previousOrientation.add(orientation);
                }
                if (leftNode == null || leftNode.hasBeenVisited()) // if you can't go left, try the next orientation
                    return goLeft(node, leftOf(orientation));
                return goLeft(leftNode, leftOf(orientation));
            }
        }
    }

    private boolean culDeSac(Node node){
        for (Node child : node.getChildren()) {
            if (child != null && !child.hasBeenVisited()){
               return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Left-Turn";
    }
}
