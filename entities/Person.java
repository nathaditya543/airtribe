package entities;

// Base entity for shared person identity fields.
public class Person {
    int id;
    String name;

    // Protected construction through concrete subclasses.
    Person(int id, String name){
        this.id = id;
        this.name = name;
    }

    // Returns unique identifier of the person.
    public int getId(){
        return id;
    }

    // Returns display name of the person.
    public String getName(){
        return name;
    }

}
