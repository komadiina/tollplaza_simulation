package tollbooth.vehicles;

import tollbooth.people.*;
import tollbooth.plaza.TollPlaza;
import tollbooth.terminals.CustomsTerminal;
import tollbooth.terminals.PoliceTerminal;
import tollbooth.terminals.Terminal;
import tollbooth.terminals.TerminalType;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Vehicle extends Thread implements Serializable {
    public static Handler handler;

    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir") + File.separator + "logs" + File.separator + "Vehicle.log");
            Logger.getLogger(Vehicle.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String registrationPlates;
    public List<Passenger> passengers; 
    public Driver driver;
    public Integer capacity;
    public Boolean hadPassengerProblems = false;
    public Integer queuePosition = -1;
    public final Object processingLock = new Object();
    private Terminal currentTerminal = null;
    public boolean stopped = false;

    public Vehicle(String registrationPlates, Driver driver, List<Passenger> passengers) {
        this.registrationPlates = registrationPlates;
        this.driver = driver;
        this.passengers = passengers;
    } 
    
    @Override
    public String toString() { 
    	return this.registrationPlates + " | " + this.driver + " | " + this.passengers.toString();
    }

    public abstract TerminalType getCorrespondentType();

    protected synchronized void moveOnce() {
        TollPlaza.vehicleQueue[this.queuePosition] = null;
        TollPlaza.vehicleQueue[this.queuePosition - 1] = this;
        this.queuePosition--;
    }

    protected synchronized boolean isNextFree() {
        return TollPlaza.vehicleQueue[this.queuePosition - 1] == null;
    }

    protected synchronized Terminal findNextPolice() {
        while (true) {
            for (PoliceTerminal pt : TollPlaza.policeTerminals) {
                if (!pt.paused && !pt.busy && pt.acceptsVehicle(this))
                    return pt;
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                Logger.getLogger(Vehicle.class.getName()).log(
                        Level.WARNING,
                        "Thread interrupted during sleep, t_id: " + this.threadId()
                );
            }
        }
    }

    protected synchronized Terminal findNextCustoms() {
        while (true) {
            for (CustomsTerminal ct : TollPlaza.customsTerminals) {
                if (!ct.paused && ct.acceptsVehicle(this) && !ct.busy)
                    return ct;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Vehicle.class.getName()).log(
                        Level.WARNING,
                        "Thread interrupted during sleep, t_id: " + this.threadId()
                );
            }
        }
    }

    protected synchronized void freeTOQ() {
        TollPlaza.vehicleQueue[0] = null;
    }

    protected void notifyTerminalDeparture(Terminal term) {

//        System.out.println("Notifying " + term.id + " on departure...");
        synchronized (term.vehicleLeftNotifier) {
            term.vehicleLeftNotifier.notify();
        }
    }

    protected void notifyTerminalArrival(Terminal term) {
        synchronized (term.vehicleArrivedNotifier) {
            term.vehicleArrivedNotifier.notify();
        }
    }

    public boolean waitUntilDone() {
        synchronized (this.processingLock) {
            this.notifyTerminalArrival(this.currentTerminal);

            try {
                this.processingLock.wait();
            } catch (InterruptedException ex) {
                return true;
            }
        }

        return false;
    }

    private boolean process() {
        if (this.waitUntilDone()) {
            this.notifyTerminalDeparture(this.currentTerminal);
            TollPlaza.decrementVehicleCounter();
            return true;
        }

        return stopped;
    }

    @Override
    public void run() {
        while (this.queuePosition > 0) {
            if (this.isNextFree())
                this.moveOnce();

            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                TollPlaza.decrementVehicleCounter();
                return;
            }
        }

        PoliceTerminal nextPolice = (PoliceTerminal) this.findNextPolice();
        this.currentTerminal = nextPolice;
        nextPolice.setCurrentVehicle(this);

        // needed
        this.freeTOQ();

        if (this.process())
            return;

        CustomsTerminal nextCustoms = (CustomsTerminal) this.findNextCustoms();
        nextCustoms.setCurrentVehicle(this);
        this.currentTerminal = nextCustoms;
        this.notifyTerminalDeparture(nextPolice);

        if (this.process())
            return;

        this.notifyTerminalDeparture(nextCustoms);

        System.out.println("Vehicle " + this.registrationPlates + " crossed the border!");
        TollPlaza.logEvent(String.format("Vehicle [%1$s] crossed the border!", this.registrationPlates));
        TollPlaza.decrementVehicleCounter();
        TollPlaza.addCrossed(this);
        TollPlaza.popVehicle(this);
    }
}
