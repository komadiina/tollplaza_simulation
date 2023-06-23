package tollbooth.people;

import tollbooth.documents.Identification;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.*;
import java.util.logging.Logger;

public final class Driver extends Person implements Serializable {
    public static Handler handler;
    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir")
                    + File.separator + "logs"
                    + File.separator + "Driver.log");
            Logger.getLogger(Driver.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public Driver(String fullName, Identification id) {
        super(fullName, id);
    }
}
