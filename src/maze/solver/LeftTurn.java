package maze.solver;

import maze.math.Coordinates;
import maze.math.Node;
import maze.maze.Maze;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LeftTurn extends Solver{
    private List<Node> previousNodes;
    private List<Orientation> previousOrientation;

    public LeftTurn(){
        previousNodes = new ArrayList<>();
        previousOrientation = new ArrayList<>();
    }

    private enum Orientation{
        NORTH(0), WEST(1), SOUTH(2), EAST(3);

        private int i;
        Orientation(int i){
            this.i = i;
        }

        public int toNumber(){
            return i;
        }

    }
    @Override
    public void solve() {
        System.out.println(nodes.size());
        // start by going left after the start
        goLeft(start, Orientation.SOUTH);

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

}
