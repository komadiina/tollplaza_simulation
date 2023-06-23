package tollbooth.vehicles;

import java.io.Serializable;

/**
 * A record-like class which represents the maximum capacities (passengers and driver) for each vehicle type.
 */
public class Capacities implements Serializable {
    public static int CAR_CAPACITY = 5;
    public static int BUS_CAPACITY = 52;
    public static int TRUCK_CAPACITY = 3;
}
