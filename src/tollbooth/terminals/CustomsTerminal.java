package tollbooth.terminals;

import tollbooth.people.Passenger;
import tollbooth.people.Person;
import tollbooth.plaza.TollPlaza;
import tollbooth.vehicles.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.*;

public final class CustomsTerminal extends Terminal {
    public static Handler handler;

    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir") + File.separator +
                    "logs" + File.separator + "CustomsTerminal.log");
            Logger.getLogger(CustomsTerminal.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
    }

    public CustomsTerminal(String id, TerminalType tp) {
        super(id, tp);
    }

    @Override
    public Boolean checkPerson(Person p, Integer processingTime) {
        try {
            Thread.sleep(processingTime);
        } catch (InterruptedException ex) {
            Logger.getLogger(CustomsTerminal.class.getName()).log(
                    Level.WARNING,
                    "Thread interrupted during processing, t_id: " + this.threadId()
            );
        }

        if (p instanceof Passenger passenger)
            if (passenger.hasLuggage)
                return !passenger.getLuggage().hasContraband;

        return true;
    }

    @Override
    public Boolean checkVehicle(Vehicle veh) {
        if (veh instanceof Car) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(CustomsTerminal.class.getName()).log(
                        Level.WARNING,
                        "Thread interrupted during sleep. t_id: " + this.threadId()
                );
            }

            return true;
        }
        else if (veh instanceof Truck tr) {
            return !this.checkTruck(tr);
        }

        Integer processingTime = this.getProcessingTime(veh);
        Iterator<Passenger> it = veh.passengers.iterator();
        while (it.hasNext()) {
            Passenger p = it.next();

            if (!this.checkPerson(p, processingTime)) {
                it.remove();
                TollPlaza.criminals.add(p);

                this.stopPerson(p);
            }
        }

        return true;
    }

    private Boolean checkTruck(Truck tr) {
        if (tr.cargo.needsDocumentation)
            return tr.cargo.isOverloaded();

        return true;
    }
}
