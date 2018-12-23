package maze.exceptions;


public class ImageException extends RuntimeException{
    public ImageException(String message){
        super(message);
    }
    public ImageException(){
        super("ERROR: the image you are trying to show is not present in the res/ directory!");
    }
}
