package maze.math;

public class Coordinates {
    private int x;
    private int y;

    public Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Coordinates(Coordinates otherCoordinates){
        this.x = otherCoordinates.x;
        this.y = otherCoordinates.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(x:"+x+", y:"+y+")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != getClass())
            return false;
        Coordinates other = (Coordinates) obj;
        if (other.x == x && other.y == y)
            return true;
        return false;
    }
}
