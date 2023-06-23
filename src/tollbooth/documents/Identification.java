package tollbooth.documents;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.*;
import java.util.logging.Logger;

public class Identification implements Serializable {
    public static Handler handler;
    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir")
                    + File.separator + "logs"
                    + File.separator + "Identification.log");
            Logger.getLogger(Identification.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static Integer STATIC_ID = 1337;
    private final String id = (STATIC_ID++).toString();

    public Identification() {}

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Identification ident) {
            return ident.id.equals(this.id);
        }

        return false;
    }
}
