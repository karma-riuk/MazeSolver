package maze.math;

public enum Orientation{
    NORTH(0), WEST(1), SOUTH(2), EAST(3);

    private int i;
    Orientation(int i){
        this.i = i;
    }

    public int toNumber(){
        return i;
    }
}
