package tollbooth.vehicles;

import tollbooth.documents.Cargo;
import tollbooth.people.Driver;
import tollbooth.people.Passenger;
import tollbooth.terminals.TerminalType;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public final class Truck extends Vehicle implements Serializable {
    public static Handler handler;
    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir")
                    + File.separator + "logs"
                    + File.separator + "Truck.log");
            Logger.getLogger(Truck.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public Cargo cargo;

    public Truck(String registrationPlates, Driver driver, List<Passenger> passengers, Cargo cargo) {
        super(registrationPlates, driver, passengers);

        this.capacity = Capacities.TRUCK_CAPACITY;
        this.cargo = cargo;
    }

    public Boolean isOverweight() {
        return this.cargo.needsDocumentation ? this.cargo.isOverloaded() : false;
    }

    @Override
    public TerminalType getCorrespondentType() {
        return TerminalType.TRUCK;
    }
    
    @Override
    public String toString() {
    	return super.toString() + " | " + this.cargo.toString();
    }
}
