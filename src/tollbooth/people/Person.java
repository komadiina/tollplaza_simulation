package tollbooth.people;

import tollbooth.documents.Identification;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public abstract class Person implements Serializable {
    public static Handler handler;
    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir")
                    + File.separator + "logs"
                    + File.separator + "Person.log");
            Logger.getLogger(Person.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public String fullName;
    protected Identification identification;

    public Person(String fullName, Identification id) {
        this.fullName = fullName;
        this.identification = id;
    }

    @Override
    public String toString() {
        return this.fullName + ", ID: " + this.identification.toString();
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Passenger p) {
            return p.identification.equals(this.identification);
        }

        return false;
    }
}
