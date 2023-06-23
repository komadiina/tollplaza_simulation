package tollbooth.documents;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import java.util.logging.*;
import java.util.logging.Logger;

public final class CustomsDocumentation extends Identification implements Serializable {
    public static Handler handler;

    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir")
                    + File.separator + "logs"
                    + File.separator + "CustomsDocumentation.log");
            Logger.getLogger(CustomsDocumentation.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Double declaredWeight;

    public CustomsDocumentation(Double declaredWeight) {
        this.declaredWeight = declaredWeight;
    }
}
