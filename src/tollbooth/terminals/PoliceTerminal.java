package tollbooth.terminals;

import tollbooth.people.*;
import tollbooth.plaza.TollPlaza;
import tollbooth.vehicles.Vehicle;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public final class PoliceTerminal extends Terminal {
    public static Handler handler;

    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir") + File.separator + "logs" + File.separator + "CustomsTerminal.log");
            Logger.getLogger(PoliceTerminal.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public PoliceTerminal(String id, TerminalType tp) {
        super(id, tp);
    }

    @Override
    public Boolean checkPerson(Person p, Integer processingTime) {
        try {
            Thread.sleep(processingTime);
        } catch (InterruptedException ex) {
            Logger.getLogger(PoliceTerminal.class.getName()).log(Level.WARNING, "Thread interrupted during processing, t_id: " + this.threadId());
        }

        Random rng = new Random();
        return rng.nextInt(100) >= 3;
    }

    @Override
    public Boolean checkVehicle(Vehicle veh) {
        Integer processingTime = this.getProcessingTime(veh);

        if (!this.checkPerson(veh.driver, processingTime)) {
            return false;
        }

        Iterator<Passenger> it = veh.passengers.iterator();
        while (it.hasNext()) {
            Passenger p = it.next();

            if (!this.checkPerson(p, processingTime)) {
                it.remove();
                veh.hadPassengerProblems = true;
                this.stopPerson(p);
            }
        }

        return true;
    }
}
