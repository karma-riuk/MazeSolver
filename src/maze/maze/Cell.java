package maze.maze;

import maze.math.Coordinates;

public class Cell{
    // Coordinates
    private Coordinates coordinates;
    // Type
    private CellType cellType;
    // Node, if any
    private Node node = null;

    public Cell(Coordinates coordinates, CellType cellType){
        this.coordinates = coordinates;
        this.cellType = cellType;
    }


    public enum CellType {
        WALL,
        PATH;

//        @Override
//        public String toString() {
//            if (this.equals(WALL))
//                return "#";
//            else
//                return " ";
//        }
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public CellType getCellType() {
        return cellType;
    }

    public Coordinates getCoordinates() {
        return new Coordinates(coordinates);
    }

    @Override
    public String toString() {
        return this.cellType+" at "+this.coordinates;
    }
}
