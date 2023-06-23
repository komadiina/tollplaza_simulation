package tollbooth.data.examples;

import java.util.Random;

/**
 * Used for instantiating a random first name.
 */
public final class ExampleFirstNames {

    public static final String[] FIRST_NAMES = new String[]{
            "Marko", "Mirko", "Petar", "Janko", "Ivan",
            "Ana", "Marija", "Elena", "Luka", "Nikola",
            "Sara", "Iva", "Maja", "Matej", "Ante"
    };

    /**
     * Fetches a random first name from the list, using the Random() generator.
     * @return A randomly chosen first name.
     */
    public static String getRandom() {
        Random rng = new Random();

        return FIRST_NAMES[rng.nextInt(FIRST_NAMES.length)];
    }
}
