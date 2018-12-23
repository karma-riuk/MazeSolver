package maze.maze;

import maze.exceptions.ImageException;
import maze.exceptions.MazeException;
import maze.math.Coordinates;
import maze.math.Node;
import maze.solver.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    /// images
    // original maze
    private BufferedImage image;
    // solved maze
    private BufferedImage imageSolved;
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
//            imageSolved = ImageIO.read(new File("res/"+name+".png"));
            imageSolved = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
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
        this.transform();
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

    /**
     * Creates the nodes and links them together. To create them, the program cycles through the maze and checks some conditions
     * (to know what these conditions are, check the if statement below). Then, after the program decided that a node should
     * be created, it search for other nodes (that are above him of to his left) and check if they are linkable, and if so
     * they get linked.
     */
    private void transform() throws MazeException {
        // check if maze satisfies our criteria
        if (!checkMaze())
            throw new MazeException();

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
    }

    private enum SearchOrientation{
        NORTH, WEST
    }

    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * Method to check if a node should accept a link or not, is used in linkNodes(Node node)
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
     * Used by maze.solver.*
     * @param coordinates (List<Coordinates>): the list of cell coordinates that are part of the solution of the maze
     */
    public void addToSolution(List<Coordinates> coordinates){
        for (Coordinates coordinate : coordinates) {
            solution.add(maze[coordinate.getY()][coordinate.getX()]);
        }
    }

    public void makeSolvedImage(){
//        int color = 255<<16;
//        for (Cell cell : solution) {
//            imageSolved.setRGB(cell.getCoordinates().getX(), cell.getCoordinates().getY(), color);
//        }


//        int red = 0;
//        int blue = 255;
//        int color;
//        int solutionSize = solution.size();
//        for (Cell cell : solution) {
//            color = (red << 16) | blue;
//            imageSolved.setRGB(cell.getCoordinates().getX(), cell.getCoordinates().getY(), color);
//            red = (int) (255*((double) solution.indexOf(cell)/solutionSize));
//            blue = (int) (255*((double) (solutionSize-solution.indexOf(cell))/solutionSize));
//        }

        int wall = Color.BLACK.getRGB();
        int path = Color.WHITE.getRGB();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (maze[y][x].getCellType() == Cell.CellType.WALL)
                    imageSolved.setRGB(x, y, wall);
                else
                    imageSolved.setRGB(x, y, path);
            }
        }
        int red = 0;
        int blue = 255;
        int color;
        int solutionSize = solution.size();
        for (Cell cell : solution) {
            color = (red << 16) | blue;
            imageSolved.setRGB(cell.getCoordinates().getX(), cell.getCoordinates().getY(), color);
            red = (int) (255*((double) solution.indexOf(cell)/solutionSize));
            blue = (int) (255*((double) (solutionSize-solution.indexOf(cell))/solutionSize));
        }
    }

    public void makeFullSolution(){
        int i = 0;
        List<Cell> toBeAdded;
        while (i < solution.size()-1){
            toBeAdded = getCorridorBetween(solution.get(i), solution.get(i+1));
            solution.addAll(i+1, toBeAdded);
            i += 1+toBeAdded.size();
        }
        System.out.println(solution);
    }

    private List<Cell> getCorridorBetween(Cell cell1, Cell cell2){
        List<Cell> ret = new ArrayList<>();
        Coordinates c1 = cell1.getCoordinates();
        Coordinates c2 = cell2.getCoordinates();
        Coordinates tmp;
        if (c1.getX() - c2.getX() == 0 && c1.getY() - c2.getY() == 0) // if the cells are adjascent to each other there is no corridor
            return ret;
        else{
            if (c1.getX() - c2.getX() == 0) { // if the difference between there x is 0, that means they are on the same line
                if (c1.getY() > c2.getY()){
                    for (int i = c1.getY()-1; i > c2.getY(); i--)
                        ret.add(maze[i][c1.getX()]);
                }
                else{
                    for (int i = c1.getY()+1; i < c2.getY(); i++)
                        ret.add(maze[i][c1.getX()]);
                }
            }
            else if (c1.getY() - c2.getY() == 0){ // if they are not on the same line, then they are on the same column
                if (c1.getX() > c2.getX()){
                    for (int i = c1.getX()-1; i > c2.getX(); i--)
                        ret.add(maze[c1.getY()][i]);
                }
                else {
                    for (int i = c1.getX() + 1; i < c2.getX(); i++)
                        ret.add(maze[c1.getY()][i]);
                }
            }
            return ret;
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

    public BufferedImage getImage(){
        return getImage(image);
    }

    /**
     * Getter for the representation of the maze scale by the scaleFactor
     * @return image (BufferedImage)
     */
    private BufferedImage getImage(BufferedImage image){
        int newW = width*scaleFactor;
        int newH = height*scaleFactor;
        Image tmp = image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;

    }

    public BufferedImage getSolvedImage(){
        return getImage(imageSolved);
    }

    public void setScaleFactor(int scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    @Override
    public String toString() {
        String ret = "";


        for (Cell[] row: maze){
            for (Cell cell: row) {
                if (cell.getCellType() == Cell.CellType.WALL)
                    ret += "##";
                else if (cell.getNode() != null)
                    ret += "()";
                else
                    ret += "  ";
            }
            ret += "\n";
        }

//        int lastRow=0;
//        for (Node n : nodes) {
//            if (lastRow != n.getPosition().getY()) {
//                lastRow = n.getPosition().getY();
//                ret += "\n";
//            }
//            ret += n;
//        }
        return ret;
    }
}
