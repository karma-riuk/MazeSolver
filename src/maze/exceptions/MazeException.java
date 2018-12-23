package maze.exceptions;

public class MazeException extends RuntimeException{
    public MazeException(){
        super("\nThe maze is not solvable. The condition to solve a maze are: \n" +
                "- Have only 1 entrance (at the top)\n" +
                "- Have only 1 exit (at the bottom)\n" +
                "- Have the edge of covered in walls (besides the entrance and exit)\n");
    }

}
