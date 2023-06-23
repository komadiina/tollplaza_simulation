package tollbooth.data.examples;

import java.util.Random;

/**
 * Used for instatiating a random last name.
 */
public class ExampleLastNames {
    public static final String[] LAST_NAMES = new String[]{
            "Markovic", "Jankovic", "Komadina", "Petrovic", "Kovacevic",
            "Nikolic", "Simic", "Popovic", "Milosevic", "Milic",
            "Stojanovic", "Kovacevic", "Maric", "Tomic", "Ilic", "Mujadzic"
    };

    /**
     * Fetches a random last name from the list, using the Random() generator.
     * @return A randomly chosen first name.
     */
    public static String getRandom() {
        Random rng = new Random();

        return LAST_NAMES[rng.nextInt(LAST_NAMES.length)];
    }
}
