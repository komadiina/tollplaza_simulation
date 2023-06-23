package tollbooth.people;

import tollbooth.documents.Identification;
import tollbooth.documents.Luggage;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public final class Passenger extends Person implements Serializable {
    public static Handler handler;
    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir")
                    + File.separator + "logs"
                    + File.separator + "Passenger.log");
            Logger.getLogger(Passenger.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public Boolean hasLuggage = false;
    private Luggage luggage = null;

    public Passenger(String fullName, Identification id) {
        super(fullName, id);

        Random rng = new Random();
        if (rng.nextInt() < 70) {
            hasLuggage = true;
            this.luggage = new Luggage(this, new String[]{"Item1", "Item2", "Item3"});
        }
    }

    public Luggage getLuggage() { return this.luggage; }
}
