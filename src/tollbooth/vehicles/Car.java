package tollbooth.vehicles;

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

public final class Car extends Vehicle implements Serializable {
    public static Handler handler;

    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir") + File.separator + "logs" + File.separator + "Car.log");
            Logger.getLogger(Car.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Car(String registrationPlates, Driver driver, List<Passenger> passengers) {
        super(registrationPlates, driver, passengers);

        this.capacity = Capacities.CAR_CAPACITY;
    }

    @Override
    public TerminalType getCorrespondentType() {
        return TerminalType.BUS_CAR;
    }
}
