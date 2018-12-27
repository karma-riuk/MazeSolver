package maze.math;

public class SampleNode extends Node{
//    public static Coordinates position = new Coordinates(0, 0);
    public SampleNode(Coordinates coordinates){
       super(coordinates);
       this.position = coordinates;
    }
//    public SampleNode(){
//        super(new Coordinates(0, 0));
//    }

//    @Override
//    public Coordinates getPosition() {
//        return position;
//    }

    //
    public void setNewCoordinates(Coordinates c){
        this.position = c;
    }
//
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
//        System.out.println(obj.getClass()+" "+getClass()+" "+Node.class);
        if (obj.getClass() != getClass() && obj.getClass() != Node.class)
            return false;
        Node other = (Node) obj;
        return (other.getPosition().getX() == getPosition().getX() && other.getPosition().getY() == getPosition().getY());
    }
}
