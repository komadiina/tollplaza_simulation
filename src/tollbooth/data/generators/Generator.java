package tollbooth.data.generators;
import tollbooth.data.examples.*;
import tollbooth.documents.Identification;
import tollbooth.people.*;

/**
 * A static method collection-like class, providing a way to randomly initialize a Driver,
 * Passenger, Luggage and Registration objects.
 */
public class Generator {
    public static Driver generateDriver() {
        return new Driver(
                ExampleFirstNames.getRandom() + " " + ExampleLastNames.getRandom(),
                new Identification()
                );
    }

    /**
     * Fetches a random Passenger object, from the Example... dataset.
     * @return A Passenger object, with a unique Passport ID, and an unitialized seatPosition.
     */
    public static Passenger generatePassenger() {
        return new Passenger(
                ExampleFirstNames.getRandom() + " " + ExampleLastNames.getRandom(),
                 new Identification()
        );
    }

    /**
     * Fetches a random Registration object, using the ExampleNumberPlates dataset.
     * @return A Registration object.
     */
    public static String generateRegistration() {
        return ExampleNumberPlates.getRandom();
    }
}
