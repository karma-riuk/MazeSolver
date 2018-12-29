package maze.math;

import java.util.List;
import java.util.ArrayList;
//import java.lang.ArrayIndexOutOfBoundException;

public class PriorityList {
    private List<Node> list;

    public PriorityList(){
        list = new ArrayList<>();
    }
    
    /**
     * Add method, it looks at the weight of the node given in argument and
     * puts it at the first position where his weight belongs
     * @param node (Node): the node about to added in the priority list
     */
    public void add(Node node){
        int index = 0;
        for (Node n: list){
            if (n.getWeight() > node.getWeight())
               break; 
            index ++;
        }
        list.add(index, node);
    }

    /**
     * Remove method, that removes from the priority list a given node
     * @param node (Node): the node that is about to get removed
     */
    public void remove(Node node){
        this.list.remove(node);
    }



    /**
     * Getter for the node, given an index
     * @param index (int): the position of the node we want
     * @return (Node)
     */
    public Node get(int index){// throws ArrayIndexOutBoundException{
        //if (index > 0 && index < list.size())
            return list.get(index);
        //else
            //throw new ArrayIndexOutBoundException();
    }

    /**
     * IndexOf method to get the index at which a given node sites in the list
     * @param node (Node): the node that is analyzed
     * @return (int)
     */
    public int indexOf(Node node){
        return list.indexOf(node);
    }

    /**
     * Pop method is used to assign to a variable the value of the element at
     * the index "index" (the parametre) and remove it as soon as it was
     * assigned
     * @param index (int): the index of the element to be returned and removed
     * @return (Node): the Node at the index-th posistion in the priority list
     */
    public Node pop(int index){
        Node ret = list.get(index);
        list.remove(index);
        return ret;
    }
}
