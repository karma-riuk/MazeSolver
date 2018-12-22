package maze.maze;

import maze.exceptions.ImageException;
import maze.exceptions.MazeException;
import maze.math.Coordinates;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Maze {
    /// Maze information
    // his name
    private String name;
    // width
    private int width;
    // height
    private int height;
    // scale factor
    private int scaleFactor = 1;
    // image
    private BufferedImage image;
    // maze structure with cells
    private Cell[][] maze;
    // The solution in coordinates
    private List<Cell> solution;
    // nodes
    private List<Node> nodes;


    public Maze(String name) throws ImageException{
        this.name = name;
        this.solution = new ArrayList<>();
        this.nodes = new ArrayList<>();

        try{
            image = ImageIO.read(new File("res/"+name+".png"));
        }catch (IOException e){
            throw new ImageException();
        }
        width = image.getWidth();
        height = image.getHeight();

        // create a cell version of the image, to be easier to manipulate
        maze = new Cell[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (image.getRGB(x, y) == Color.BLACK.getRGB())
                    maze[y][x] = new Cell(new Coordinates(x, y), Cell.CellType.WALL);
                else
                    maze[y][x] = new Cell(new Coordinates(x, y), Cell.CellType.PATH);
            }
        }
    }

    /**
     * Checks if maze satisfies the criteria that the we have 1 entrance at the top, 1 exit at the bottom
     * and only wall on the edge of the maze (besides the entrance and the exit)
     * @return (boolean)
     */
    private boolean checkMaze(){
        int entryCount, exitCount, leftPathCount, rightPathCount;
        entryCount = exitCount = leftPathCount = rightPathCount = 0;
        for (int i = 0; i < maze.length; i++) {
            if (maze[0][i].getCellType() == Cell.CellType.PATH)
                entryCount++;
            if (maze[maze.length-1][i].getCellType() == Cell.CellType.PATH)
                exitCount++;
            if (maze[i][0].getCellType() == Cell.CellType.PATH)
                leftPathCount++;
            if (maze[i][maze[i].length-1].getCellType() == Cell.CellType.PATH)
                rightPathCount++;
        }
        return (entryCount == 1 && exitCount == 1 && leftPathCount == 0 && rightPathCount == 0);

    }

    public void solve() throws MazeException {
        // check if maze satisfies our criteria
        if (!checkMaze())
            throw new MazeException();

        // add to the solution the entry and the exit
        for (int i = 0; i < width; i++) {
            if (maze[0][i].getCellType() == Cell.CellType.PATH)
                solution.add(0, maze[0][i]);
            if (maze[height-1][i].getCellType() == Cell.CellType.PATH)
                solution.add(1, maze[height-1][i]);
        }

        // setting the nodes and linking them together
        Cell.CellType wall = Cell.CellType.WALL;
        Cell.CellType path = Cell.CellType.PATH;
        for (int y = 0; y < height; y++) {
            for (int x = 1; x < width-1; x++) {
                if ( maze[y][x].getCellType() == path && ( // first check if the cell is a path cell (because it's impossible to have a node on a wall of a maze...) and then check the other conditions to know if a node should be added or not
                        ((y == 0 || y == height-1) && maze[y][x - 1].getCellType() == wall && maze[y][x + 1].getCellType() == wall) // add the entrance/exit to the nodes
                        || (maze[y][x-1].getCellType() == wall && maze[y][x+1].getCellType() == path) // start of a corridor (horizontal)
                        || (maze[y-1][x].getCellType() == wall && maze[y+1][x].getCellType() == path) // start of a corridor (vertical)
                        || (maze[y][x+1].getCellType() == wall && maze[y][x-1].getCellType() == path) // end of a corridor (horizontal)
                        || (maze[y+1][x].getCellType() == wall && maze[y-1][x].getCellType() == path) // end of a corridor (vertical)
                        || (maze[y-1][x].getCellType() == path && (maze[y][x+1].getCellType() == path || maze[y][x-1].getCellType() == path)) // junction where you come from north and can go either east or west (doesn't matter)
                        || (maze[y+1][x].getCellType() == path && (maze[y][x+1].getCellType() == path || maze[y][x-1].getCellType() == path)) // junction where you come from south and can go either east or west (doesn't matter)
                )) {
                    Node node = new Node(new Coordinates(x, y)); // creating the node
                    linkNode(node, SearchOrientation.NORTH); // searches for possible connection from north
                    linkNode(node, SearchOrientation.WEST); // searches for possible connection from west
                    maze[y][x].setNode(node);
                    nodes.add(node);
                }
            }
        }
        System.out.println(this);
    }

    private enum SearchOrientation{
        NORTH, WEST
    }

    /**
     * Methode to check if a node should accept a link or not, is used in linkNodes(Node node)
     * @param y (int): the column we are looking at in the maze
     * @param x (int): the row we are looking at in the maze
     * @param possibleNode (Node): the possible node that could be linked to the node we are currently looking at
     * @param isBlocked (boolean): boolean to know if there is a wall between 2 nodes
     * @return (Object[2]): contains the future value that has to be assigned to possibleNode and isBlocked in linkNodes(Node node)
     */
    private Object[] conditions(int y, int x, Node possibleNode, boolean isBlocked) {
        Object[] ret = new Object[2];
        Node possibleNode1 = possibleNode;
        boolean isBlocked1 = isBlocked;
        if (maze[y][x].getNode() != null) {
            possibleNode1 = maze[y][x].getNode();
            isBlocked1 = false;
        } else if (possibleNode != null && maze[y][x].getCellType() == Cell.CellType.WALL){
            possibleNode1 = null;
            isBlocked1 = true;
        }
        ret[0] = possibleNode1;
        ret[1] = isBlocked1;
        return ret;
    }

    /**
     * Passes through all the previous cells (above the current cell or to the left of it), checks if there is a node in
     * one of these cells and checks if there is a wall between them, if there is a node and there is no wall, connect them
     * together (either West-East or North-South)
     * @param node (Node): the node we are currently looking at
     * @param orientation (SearchOrientation): variable to know where we are looking (north or west)
     */
    private void linkNode(Node node, SearchOrientation orientation){
        boolean isBlocked = false;
        Node possibleNode = null;
        int maxCount, other;
        if (orientation == SearchOrientation.NORTH) {
            maxCount = node.getPosition().getY();
            other = node.getPosition().getX();
        } else {
            maxCount = node.getPosition().getX();
            other = node.getPosition().getY();
        }
        Object[] result;
        for (int i = 0; i < maxCount; i++){
            if (orientation == SearchOrientation.NORTH)
                result = conditions(i, other, possibleNode, isBlocked);
            else
                result = conditions(other, i, possibleNode, isBlocked);
            possibleNode = (Node) result[0];
            isBlocked = (boolean) result[1];
        }
        if (!isBlocked && possibleNode != null) {
            if (orientation == SearchOrientation.NORTH) {
                node.setConnectionNorth(possibleNode);
                possibleNode.setConnectionSouth(node);
            }else{
                node.setConnectionWest(possibleNode);
                possibleNode.setConnectionEast(node);
            }
        }


    }

    /**
     * Getter for the width of the maze, in number of cells
     * @return (int)
     */
    public int getWidth() {
        return width;
    }

    /**
     * Getter for the height of the maze, in number of cells
     * @return (int)
     */
    public int getHeight() {
        return height;
    }

    /**
     * Getter for the representation of the maze scale by the scaleFactor
     * @return image (BufferedImage)
     */
    public BufferedImage getImage(){
        int newW = width*scaleFactor;
        int newH = height*scaleFactor;
        Image tmp = image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    public void setScaleFactor(int scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    @Override
    public String toString() {

        String ret = "";
        int lastRow=0;
        for (Node n : nodes) {
            if (lastRow != n.getPosition().getY()) {
                lastRow = n.getPosition().getY();
                ret += "\n";
            }
            ret += n;
        }

//        for (Cell[] row: maze){
//            for (Cell cell: row) {
//                i (cell.getCellType() == Cell.CellType.WALL)
//                    ret += "##";
//                else if (cell.getNode() != null)
//                    ret += "()";
//                else
//                    ret += "  ";
//            }
//            ret += "\n";
//        }
        return ret;
    }
}
