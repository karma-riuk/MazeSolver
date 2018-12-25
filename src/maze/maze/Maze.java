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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class Maze {
    /// Maze information
    // his name
    private String name;
    // width
    private int width;
    // height
    private int height;
    // width of output
    private int newW;
    // height of output
    private int newH;
    // scale factor
    private float scaleFactor = 1;
    // reduce boolean, to get rid of useless nodes (memory optimization)
    private boolean reduce = false;
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

    public Maze(String name, boolean reduce, float scaleFactor){
        this(name);
        this.reduce = reduce;
        this.scaleFactor = scaleFactor;
    }

    public Maze(String name, float scaleFactor){
        this(name);
        this.scaleFactor = scaleFactor;
    }

    public Maze(String name, boolean reduce){
        this(name);
        this.reduce = reduce;
    }

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

        scaleFactor = (float)(1000/width);

        newH = 1000;
        newW = (newH*width)/height;

        // create a cell version of the image, to be easier to manipulate
        maze = new Cell[height][width];
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
        for (int i = 0; i < width; i++) {
            if (maze[0][i].getCellType() == Cell.CellType.PATH)
                entryCount++;
            if (maze[height - 1][i].getCellType() == Cell.CellType.PATH)
                exitCount++;
        }
        for (int i = 0; i < height; i++) {
            if (maze[i][0].getCellType() == Cell.CellType.PATH)
                leftPathCount++;
            if (maze[i][width-1].getCellType() == Cell.CellType.PATH)
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
    public void transform() throws MazeException {
        long startTime = System.nanoTime();
        long endTime;
        // check if maze satisfies our criteria
        if (!checkMaze())
            throw new MazeException();

        File file = new File("res/"+name+"/nodes");
        if (!file.exists()) { // if the binary file containg the nodes doesn't exist, then create the nodes
            Cell.CellType wall = Cell.CellType.WALL;
            Cell.CellType path = Cell.CellType.PATH;
            for (int y = 0; y < height; y++) {
                for (int x = 1; x < width - 1; x++) {
//                boolean shouldAdd = true;
                    if (maze[y][x].getCellType() == path && ( // first check if the cell is a path cell (because it's impossible to have a node on a wall of a maze...) and then check the other conditions to know if a node should be added or not
                            ((y == 0 || y == height - 1) && maze[y][x - 1].getCellType() == wall && maze[y][x + 1].getCellType() == wall) // add the entrance/exit to the nodes
                                    || (maze[y][x - 1].getCellType() == wall && maze[y][x + 1].getCellType() == path) // start of a corridor (horizontal)
                                    || (maze[y - 1][x].getCellType() == wall && maze[y + 1][x].getCellType() == path) // start of a corridor (vertical)
                                    || (maze[y][x + 1].getCellType() == wall && maze[y][x - 1].getCellType() == path) // end of a corridor (horizontal)
                                    || (maze[y + 1][x].getCellType() == wall && maze[y - 1][x].getCellType() == path) // end of a corridor (vertical)
                                    || (maze[y - 1][x].getCellType() == path && (maze[y][x + 1].getCellType() == path || maze[y][x - 1].getCellType() == path)) // junction where you come from north and can go either east or west (doesn't matter)
                                    || (maze[y + 1][x].getCellType() == path && (maze[y][x + 1].getCellType() == path || maze[y][x - 1].getCellType() == path)) // junction where you come from south and can go either east or west (doesn't matter)
                    )) {
                        Node node = new Node(new Coordinates(x, y)); // creating the node
                        nodes.add(node);
                        maze[y][x].setNode(node);
                    }
                }
            }
            endTime = System.nanoTime();
            System.out.println("Time taken to create the nodes: "+ ((endTime - startTime)/1000000000));

            System.out.println("Writing nodes to binary file...");
            startTime = System.nanoTime();
//            List<Node> tryNodes = new LinkedList<>();
//            tryNodes.add(new Node(new Coordinates(2051, 2049)));
//            tryNodes.add(new Node(new Coordinates(3, 4)));
//            tryNodes.add(new Node(new Coordinates(5, 6)));
//            tryNodes.add(new Node(new Coordinates(7, 8)));
            writeBinaryFile(nodes);
//            List<Node> testNodes = parseNodes(readBinaryFile());
//            System.out.println("\n\nTEST NODES: "+testNodes+"\n\n");
            endTime = System.nanoTime();
            System.out.println("Time taken to writes the nodes in file: "+(endTime - startTime)/1000000000);
        }
        else{
            nodes.addAll(parseNodes(readBinaryFile()));
            for (Node node : nodes) {
                maze[node.getPosition().getY()][node.getPosition().getX()].setNode(node);
            }
            endTime = System.nanoTime();
            System.out.println("Time taken to read the nodes from binary file and create them: "+ ((endTime - startTime)/1000000000));
        }

        System.out.println("Linking nodes together...");
        startTime = System.nanoTime();
        for (Node node : nodes) {
            linkNode(node, SearchOrientation.NORTH); // searches for possible connection from north
            linkNode(node, SearchOrientation.WEST); // searches for possible connection from west
        }
//        System.out.println(nodes);
        endTime = System.nanoTime();
        System.out.println("Time taken to link nodes together: "+ ((endTime - startTime)/1000000000));


        if (reduce) {
            System.out.println("Reducing...");
            startTime = System.nanoTime();
            reduce();
            endTime = System.nanoTime();
            System.out.println("Time taken to reduce them: "+ ((endTime - startTime)/1000000000));
        }
        System.out.println("Nodes count: "+nodes.size());
//        System.out.println(this);

    }

    private int extractInt(byte b1, byte b2){
        return (((b1 & 0xFF) << 8) | (b2 & 0xFF));
    }

    private List<Node> parseNodes(byte[] data){
        int len = (( ((data[0] & 0xFF) << 24 ) | ((data[1] & 0xFF) << 16 ) | (data[2] & 0xFF) << 8) | (data[3] & 0xFF));
        Node[] tmpNodes = new Node[len];

        int z = 4;
        for (int i = 0; i < tmpNodes.length; i++) {
            tmpNodes[i] = new Node(new Coordinates(extractInt(data[z], data[z+1]), extractInt(data[z+2], data[z+3])));
            z+=4;
        }
        return Arrays.asList(tmpNodes);
    }

    private void writeBinaryFile(List<Node> nodes){
        byte[] byteNodes = new byte[4+nodes.size()*4]; // the size of the byte array is 3 (the size of the array) + 2 (the number of coordinates in a node) * 2 (the bytes that will occupy the coordinate)
        for (int i = 0; i < 4; i++)
            byteNodes[i] = (byte) ((nodes.size()>> (3-i)*8) & 0xFF);

        int i = 4;

        for (Node node : nodes) {
            for (int j = 0; j < 2; j++) {
                byteNodes[i] = (byte) ((node.getPosition().getX() >> (1-j)*8) & 0xFF);
                i++;
            }
            for (int j = 0; j < 2; j++) {
                byteNodes[i] = (byte) ((node.getPosition().getY() >> (1-j)*8) & 0xFF);
                i++;
            }
        }

        Path path = Paths.get("res/"+name+"/nodes");
        try{
            Files.write(path, byteNodes);
        }catch (IOException io) {
            System.out.println("Could not write to file");
            System.out.println(io.getMessage());
        }
    }

    private byte[] readBinaryFile(){
        Path path = Paths.get("res/"+name+"/nodes");
        byte[] data = null;
        try{
            data = Files.readAllBytes(path);
        }catch (IOException io) {
            System.out.println("Could not open file");
            System.out.println(io.getMessage());
        }
        return data;
    }

    private void reduce(Node node){
        nodes.remove(node);
        maze[node.getPosition().getY()][node.getPosition().getX()].setNode(null);
        Node onlyChild = null;

        for (Node child : node.getChildren()) {
            if (child != null)
                onlyChild = child;
        }
        onlyChild.removeConnectionWithIndex(Arrays.asList(onlyChild.getChildren()).indexOf(node));
        int nChilds = 0;
        for (Node child : onlyChild.getChildren()) {
            if (child != null)
                nChilds++;
        }
        if (nChilds <= 1){
            reduce(onlyChild);
        }
    }

    private void reduce(){
        int nChilds;
        List<Node> nodesToRemove = new ArrayList<>();
        for (Node node : nodes) {
            if (node.getPosition().getY() != 0 && node.getPosition().getY() != height-1 ) { //as before, don't remove start or end
                nChilds = 0;
                for (Node child : node.getChildren()) {
                    if (child != null){
                        nChilds ++;
                    }
                }
                if (nChilds <= 1){
                    nodesToRemove.add(node);
                }
            }
        }
        for (Node node : nodesToRemove) {
            reduce(node);
        }
    }

    private enum SearchOrientation{
        NORTH, WEST
    }

    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * !!! OLD !!!
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

    private boolean conditions(int y, int x){
        return (maze[y][x].getCellType() != Cell.CellType.WALL);
    }

    /**
     * Passes through all the previous cells (above the current cell or to the left of it), checks if there is a node in
     * one of these cells and checks if there is a wall between them, if there is a node and there is no wall, connect them
     * together (either West-East or North-South)
     * @param node (Node): the node we are currently looking at
     * @param orientation (SearchOrientation): variable to know where we are looking (north or west)
     */
    private void linkNode(Node node, SearchOrientation orientation){
        Node possibleNode = null;
        int maxCount, other;

        if (orientation == SearchOrientation.NORTH) {
            maxCount = node.getPosition().getY();
            other = node.getPosition().getX();
        } else {
            maxCount = node.getPosition().getX();
            other = node.getPosition().getY();
        }

        for (int i = maxCount-1; i >= 0; i--){ // look behind the node (left side or up respectively)
            if (orientation == SearchOrientation.NORTH) {
                if (maze[i][other].getCellType() == Cell.CellType.WALL) // if there is a wall and we still haven't found anything, just give up
                    break;
                else if (maze[i][other].getNode() != null) {
                    possibleNode = maze[i][other].getNode();
                    break;
                }

            }
            else {
                if (maze[other][i].getCellType() == Cell.CellType.WALL) // if there is a wall and we still haven't found anything, just give up
                    break;
                else if (maze[other][i].getNode() != null){
                    possibleNode = maze[other][i].getNode();
                    break;
                }
            }
        }
        if (possibleNode != null)
            if (orientation == SearchOrientation.NORTH) {
                node.setConnectionNorth(possibleNode);
                possibleNode.setConnectionSouth(node);
            } else {
                node.setConnectionWest(possibleNode);
                possibleNode.setConnectionEast(node);
            }

//        for (int i = 0; i < maxCount; i++){
//            if (orientation == SearchOrientation.NORTH)
//                result = conditions(i, other, possibleNode, isBlocked);
//            else
//                result = conditions(other, i, possibleNode, isBlocked);
//            possibleNode = (Node) result[0];
//            isBlocked = (boolean) result[1];
//        }
//        if (!isBlocked && possibleNode != null) {
//            if (orientation == SearchOrientation.NORTH) {
//                node.setConnectionNorth(possibleNode);
//                possibleNode.setConnectionSouth(node);
//            } else {
//                node.setConnectionWest(possibleNode);
//                possibleNode.setConnectionEast(node);
//            }
//        }


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

        try {
            File outPutFile = new File("res/"+name + "-solved.png");
            ImageIO.write(imageSolved, "png", outPutFile);
        }catch (IOException e){
            System.out.println("Couldn't write solution file");
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
    }

    private List<Cell> getCorridorBetween(Cell cell1, Cell cell2){
        List<Cell> ret = new ArrayList<>();
        Coordinates c1 = cell1.getCoordinates();
        Coordinates c2 = cell2.getCoordinates();
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

    public int getNewH() {
        return newH;
    }

    public int getNewW() {
        return newW;
    }

    public BufferedImage getImage(){
        return getImage(image);
    }

    /**
     * Getter for the representation of the maze scale by the scaleFactor
     * @return image (BufferedImage)
     */
    private BufferedImage getImage(BufferedImage image){
//        int newW = (int)(width*scaleFactor);
//        int newH = (int)(height*scaleFactor);
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

    public String getName() {
        return name;
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
