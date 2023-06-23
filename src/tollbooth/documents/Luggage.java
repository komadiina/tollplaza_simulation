package tollbooth.documents;

import tollbooth.people.Passenger;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Logger;

/**
 * Used for filling in the bus' luggage department, with each luggage assigned to it's owner.
 */
public final class Luggage implements Serializable {
    public static Handler handler;
    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir")
                    + File.separator + "logs"
                    + File.separator + "Luggage.log");
            Logger.getLogger(Luggage.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private List<String> contents = new ArrayList<>();
    public boolean hasContraband = false;
    public final Passenger belongsTo;

    public Luggage(Passenger p, String[] contents) {
        this.contents.addAll(Arrays.asList(contents));
        this.belongsTo = p;

        Random rng = new Random();
        if (rng.nextInt() < 10) {
            this.contents.add("contraband");
            this.hasContraband = true;
        }
    }

    @Override
    public String toString() {
        return this.contents.toString();
    }
}
