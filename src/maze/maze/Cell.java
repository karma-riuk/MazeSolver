package maze.maze;

import maze.math.Coordinates;
import maze.math.Node;

import java.awt.*;

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
        WALL(new Color(0, 0, 0)),
        PATH(new Color(255, 255, 255));

//        @Override
//        public String toString() {
//            if (this.equals(WALL))
//                return "#";
//            else
//                return " ";
//        }

        private Color color;
        CellType(Color color){
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public int getInt(){
            return color.getRGB();
        }
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
