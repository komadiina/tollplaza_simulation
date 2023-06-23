package tollbooth.documents;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import java.util.logging.*;
import java.util.logging.Logger;

public final class Cargo implements Serializable {
    public static Handler handler;

    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir")
                    + File.separator + "logs"
                    + File.separator + "Cargo.log");
            Logger.getLogger(Cargo.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Boolean needsDocumentation;
    private final CustomsDocumentation documentation;
    private double actualWeight;

    public Cargo() {
        Random rng = new Random();

        this.actualWeight = rng.nextDouble(5000);
        this.documentation = new CustomsDocumentation(this.actualWeight);

        this.needsDocumentation = rng.nextBoolean();
        if (needsDocumentation) {

            this.actualWeight = this.documentation.declaredWeight;
            if (rng.nextInt() < 20)
                this.actualWeight = documentation.declaredWeight
                        + documentation.declaredWeight * rng.nextDouble(0.20);
        }
    }

    public Boolean isOverloaded() {
        if (needsDocumentation)
            return this.documentation.declaredWeight < this.actualWeight;
        else return false;
    }

    @Override
    public String toString() {
        return "Declared cargo weight: " + String.format("%.2f", this.documentation.declaredWeight);
    }
}
