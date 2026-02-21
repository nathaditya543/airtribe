package exceptions;

// Generic runtime exception thrown when an entity lookup by ID fails.
public class IdNotFound extends RuntimeException{
    public IdNotFound(String message){
        super(message);
    }

}
