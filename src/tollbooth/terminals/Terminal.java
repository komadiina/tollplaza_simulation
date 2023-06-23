package tollbooth.terminals;

import tollbooth.people.Person;
import tollbooth.plaza.TollPlaza;
import tollbooth.vehicles.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Terminal extends Thread {
    public static Handler handler;

    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir") + File.separator +
                    "logs" + File.separator + "Terminal.log");
            Logger.getLogger(Terminal.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
    }
	
    public TerminalType type;
    public String id;
    public Vehicle currentVehicle = null;

    public final Object vehicleArrivedNotifier = new Object();
    public final Object vehicleLeftNotifier = new Object();
    public final Object terminalPauseLock = new Object();
    public boolean paused = false;
    public boolean busy = false;
    
    private final PropertyChangeSupport propChangeSupport = new PropertyChangeSupport(this);
 
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propChangeSupport.removePropertyChangeListener(listener);
    }

    public void setCurrentVehicle(Vehicle veh) {
        Vehicle oldValue = this.currentVehicle;
        this.currentVehicle = veh;
        
        propChangeSupport.firePropertyChange("currentVehicle", oldValue, veh);
    }
   

    public Terminal(String id, TerminalType type) {
        this.id = id;
        this.type = type;
    }
    
    protected Integer getProcessingTime(Vehicle veh) {
        if (veh instanceof Bus) return 500;
        else return 100;
    }

    public Boolean acceptsVehicle(Vehicle veh) {
        return this.type == TerminalType.ANY || this.type == veh.getCorrespondentType();
    }

    public abstract Boolean checkPerson(Person p, Integer processingTime);

    public abstract Boolean checkVehicle(Vehicle veh);

    protected void stopVehicle(Vehicle veh) {
        veh.hadPassengerProblems = true;
        veh.queuePosition = -1;
        veh.stopped = true;
        
        TollPlaza.addCriminal(veh.driver);
        TollPlaza.popVehicle(veh);
        TollPlaza.addStopped(veh);
        TollPlaza.logEvent(String.format("[%1$s] Stopped vehicle: %2$s.", this.id, veh.registrationPlates));
        
        synchronized (veh.processingLock) {
            veh.interrupt();
        }

        System.out.printf("[%1$s] Stopped vehicle: %2$s.%n", this.id, veh.registrationPlates);
    }

    protected void stopPerson(Person p) {
//        TollPlaza.criminals.add(p);
        TollPlaza.addCriminal(p);
        TollPlaza.logEvent(String.format("[%1$s] Fined person: %2$s.", this.id, p.fullName));
    }

    private void checkWorkStatus() {
        // Check for work status of the terminal (paused, unpaused)
        if (this.paused) {
            synchronized (this.terminalPauseLock) {
                try {
                    this.terminalPauseLock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Terminal.class.getName()).log(
                            Level.WARNING,
                            "Thread interrupted, t_id: " + this.threadId()
                    );

                    // For exiting the simulation (TollPlaza)
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void waitForVehicleArrival() {
        synchronized (this.vehicleArrivedNotifier) {
            try {
                this.vehicleArrivedNotifier.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Terminal.class.getName()).log(Level.WARNING,
                        "Thread interrupted during lazy-wait. t_id: " + this.threadId());

                // For exiting the simulation (TollPlaza)
                Thread.currentThread().interrupt();
            }
        }

        this.busy = true;
    }

    private void waitForVehicleDeparture() {
        synchronized (this.vehicleLeftNotifier) {
            this.notifyVehicle();

            try {
                this.vehicleLeftNotifier.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Terminal.class.getName()).log(Level.WARNING,
                        "Thread interrupted during lazy-wait. t_id: " + this.threadId());
            }
        }
    }

    private void notifyVehicle() {
        synchronized (this.currentVehicle.processingLock) {
            this.currentVehicle.processingLock.notify();
        }
    }

    private void freeTerminal() {
    	this.setCurrentVehicle(null);
        this.busy = false;
    }

    protected synchronized boolean vehiclesRemaining() {
        return TollPlaza.numVehicles > 0;
    }

    @Override
    public void run() {
        while (this.vehiclesRemaining()) {
            this.waitForVehicleArrival();
            if (this.isInterrupted())
                break;

            System.out.printf("[%1$s] Arrived: [%2$s].%n", this.id, this.currentVehicle.registrationPlates);

            boolean isOk = this.checkVehicle(this.currentVehicle);

            if (isOk) {
                this.waitForVehicleDeparture();
            } else {
                synchronized (this.currentVehicle.processingLock) {
                    this.stopVehicle(this.currentVehicle);
                }
            }

            this.freeTerminal();
            System.out.printf("[%1$s] Terminal freed.%n", this.id);

            this.checkWorkStatus();
            if (this.isInterrupted())
                break;
        }
    }

    @Override
    public String toString() {
        return this.id;
    }
}
