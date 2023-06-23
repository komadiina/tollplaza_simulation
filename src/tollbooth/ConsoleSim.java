package tollbooth;

import tollbooth.plaza.TollPlaza;
import tollbooth.gui.*;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class ConsoleSim {
    public static Handler handler;

    static {
        File logDirectory = new File(System.getProperty("user.dir") + File.separator +
                "logs" + File.separator);

        if (!logDirectory.exists()) if (!logDirectory.mkdir()) {
            Logger.getLogger(ConsoleSim.class.getName()).log(Level.WARNING, "'logs' directory could not be created.");
        }

        try {
            handler = new FileHandler(System.getProperty("user.dir") + File.separator +
                    "logs" + File.separator +
                    "ConsoleSim.log");
            Logger.getLogger(ConsoleSim.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
//
//    public static void main(String[] main) {
////    	MainWindow.start();
//    	
////    	TollPlaza.startSimulation();
//// 		TollPlaza.stopSimulation();
//    	
//        TollPlaza tollPlaza = new TollPlaza(35, 5, 10);
//        System.out.println("Starting simulation...");
//        tollPlaza.start();
//        tollPlaza.waitForEnd();
//
//        System.out.println(TollPlaza.events);
//    }
}
