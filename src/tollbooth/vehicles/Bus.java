package tollbooth.vehicles;

import tollbooth.documents.Luggage;
import tollbooth.people.Driver;
import tollbooth.people.Passenger;
import tollbooth.terminals.TerminalType;

import java.io.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.*;

public final class Bus extends Vehicle implements Serializable {
    public static Handler handler;
    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir")
                    + File.separator + "logs"
                    + File.separator + "Bus.log");
            Logger.getLogger(Bus.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public List<Luggage> luggages;

    public Bus(String registrationPlates, Driver driver, List<Passenger> passengers, List<Luggage> luggages) {
        super(registrationPlates, driver, passengers);

        this.luggages = luggages;
        this.capacity = Capacities.BUS_CAPACITY;
    }

    @Override
    public TerminalType getCorrespondentType() {
        return TerminalType.BUS_CAR;
    }
}
