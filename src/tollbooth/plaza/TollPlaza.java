package tollbooth.plaza;

import tollbooth.data.generators.Generator;
import tollbooth.documents.Cargo;
import tollbooth.documents.Luggage;
import tollbooth.gui.MainWindow;
import tollbooth.people.Passenger;
import tollbooth.people.Person;
import tollbooth.terminals.*;
import tollbooth.vehicles.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.nio.file.FileSystems;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;

public final class TollPlaza extends Thread {
    public static Handler handler;

    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir") + File.separator + "logs" + File.separator + "Person.log");
            Logger.getLogger(Person.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static Vehicle[] vehicleQueue = new Vehicle[50];
    public static List<PoliceTerminal> policeTerminals = new ArrayList<>();
    public static List<CustomsTerminal> customsTerminals = new ArrayList<>();
    public static List<Vehicle> stoppedVehicles = new ArrayList<>();
    public static DefaultListModel<String> stoppedVehiclesModel = new DefaultListModel<>();
    public static List<Person> criminals = new ArrayList<>();
    public static DefaultListModel<String> criminalsModel = new DefaultListModel<>();
    public static List<String> events = new ArrayList<>();
    public static DefaultListModel<String> eventsModel = new DefaultListModel<>();
    public static List<String> crossed = new ArrayList<>();
    public static DefaultListModel<String> crossedModel = new DefaultListModel<>();
    public static List<Vehicle> remainingVehicles = new ArrayList<>();
    public static DefaultListModel<String> remainingVehModel = new DefaultListModel<>();
    public static Integer numVehicles = 0;
    public static List<Vehicle> vehicles = new ArrayList<>();
    public static boolean isRunning = true;
    
    private final PropertyChangeSupport queueChangeSupport = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    	queueChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
    	queueChangeSupport.removePropertyChangeListener(listener);
    }

    
	public static TerminalWatcher watcher; 

    public TollPlaza(int numCars, int numBuses, int numTrucks) {
    	TollPlaza.cleanup();
    	
        TollPlaza.numVehicles = numBuses + numCars + numTrucks;

        // Instantiate cars
        Random rng = new Random();

        for (int i = 0; i < numBuses; i++) {
            int numPass = rng.nextInt(Capacities.BUS_CAPACITY - 1);
            List<Passenger> passengerList = new ArrayList<>();
            for (int j = 0; j < numPass; j++)
                passengerList.add(Generator.generatePassenger());

            List<Luggage> luggageList = new ArrayList<>();
            for (Passenger p : passengerList)
                if (p.hasLuggage) luggageList.add(p.getLuggage());

            vehicles.add(new Bus(Generator.generateRegistration(), Generator.generateDriver(), passengerList, luggageList));
        }

        for (int i = 0; i < numCars; i++) {
            int numPass = rng.nextInt(Capacities.CAR_CAPACITY - 1);
            List<Passenger> passengerList = new ArrayList<>();
            for (int j = 0; j < numPass; j++)
                passengerList.add(Generator.generatePassenger());

            // car passengers have no luggage (i guess)
            for (Passenger p : passengerList)
                p.hasLuggage = false;

            vehicles.add(new Car(Generator.generateRegistration(), Generator.generateDriver(), passengerList));
        }

        for (int i = 0; i < numTrucks; i++) {
            int numPass = rng.nextInt(Capacities.TRUCK_CAPACITY - 1);
            List<Passenger> passengerList = new ArrayList<>();
            for (int j = 0; j < numPass; j++)
                passengerList.add(Generator.generatePassenger());

            // truck passengers have no luggage (i guess)
            for (Passenger p : passengerList)
                p.hasLuggage = false;

            vehicles.add(new Truck(Generator.generateRegistration(), Generator.generateDriver(), passengerList, new Cargo()));
        }

        Collections.shuffle(vehicles);
        for (int i = 0; i < TollPlaza.numVehicles; i++) {
            TollPlaza.vehicleQueue[i] = vehicles.get(i);
            TollPlaza.vehicleQueue[i].queuePosition = i;
        }
        
        TollPlaza.remainingVehicles.addAll(TollPlaza.vehicles);
        TollPlaza.remainingVehModel.addAll(TollPlaza.remainingVehicles.stream().map(p -> {
        	return p.registrationPlates; 
        	}).collect(Collectors.toList()));

        // Instantiate terminals
        policeTerminals.add(new PoliceTerminal("POL1", TerminalType.BUS_CAR));
        policeTerminals.add(new PoliceTerminal("POL2", TerminalType.BUS_CAR));
        policeTerminals.add(new PoliceTerminal("POL3", TerminalType.TRUCK));

        customsTerminals.add(new CustomsTerminal("CST1", TerminalType.ANY));
        customsTerminals.add(new CustomsTerminal("CST2", TerminalType.TRUCK));
        
        List<Terminal> toRegister = new ArrayList<>();
        toRegister.addAll(customsTerminals);
        toRegister.addAll(policeTerminals);
        try {
            TollPlaza.watcher = new TerminalWatcher(System.getProperty("user.dir"), "watcherfile.txt",
                    FileSystems.getDefault().newWatchService(),
                    toRegister);
        } catch (IOException ex) {
            Logger.getLogger(TollPlaza.class.getName()).log(
                    Level.WARNING,
                    "TerminalWatcher service could not be registered: " +
                            ex.fillInStackTrace().toString()
            );
        }
    }
    
    @Override
    public void run() {
        isRunning = true;
        watcher.start();

        // Simulation started
        System.out.print("Police terminals: ");
        System.out.println(policeTerminals);

        System.out.print("Customs terminals: ");
        System.out.println(customsTerminals);

        for (PoliceTerminal pt : policeTerminals) {
            pt.setDaemon(true);
            pt.start();
        }
        
        
        for (CustomsTerminal ct : customsTerminals) {  
        	ct.setDaemon(true);  
        	ct.start(); 
        }
        
        TollPlaza.vehicles.forEach(veh -> { veh.setDaemon(true); });
        TollPlaza.vehicles.forEach(Vehicle::start);
    }

    private void interruptTerminals() {
        for (PoliceTerminal pt : policeTerminals)
            pt.interrupt();

        for (CustomsTerminal ct : customsTerminals)
            ct.interrupt();
    }

    public void waitForEnd() {
        try {
            for (Vehicle veh : vehicles)
                veh.join();

            for (Vehicle veh : vehicles)
                if (veh.isAlive()) {
                    veh.interrupt();
                }

            interruptTerminals();

            for (PoliceTerminal pt : policeTerminals)
                pt.join();

            for (CustomsTerminal ct : customsTerminals)
                ct.join();

            watcher.isRunning = false;
            watcher.interrupt();
            watcher.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    public static synchronized void decrementVehicleCounter() {
        if (TollPlaza.numVehicles == 0)
            return;

        TollPlaza.numVehicles--;
    }

    public static synchronized void logEvent(String ev) {
        TollPlaza.events.add(ev);
        TollPlaza.eventsModel.addElement(ev);
    }
    
    public static synchronized void addCrossed(Vehicle veh) {
    	TollPlaza.crossed.add(veh.registrationPlates);
    	TollPlaza.crossedModel.addElement(veh.registrationPlates);
    }
    
    public static synchronized void addCriminal(Person p) {
    	TollPlaza.criminals.add(p);
    	TollPlaza.criminalsModel.addElement(p.toString());
    }
    
    public static synchronized void addStopped(Vehicle veh) {
    	TollPlaza.stoppedVehicles.add(veh);
    	TollPlaza.stoppedVehiclesModel.addElement(veh.registrationPlates);
    }
    
    public static synchronized void popVehicle(Vehicle veh) {
    	TollPlaza.remainingVehicles.remove(veh);
    	TollPlaza.remainingVehModel.removeElement(veh.registrationPlates);
    }
    
    public static void serialize() {
        File finedPath = new File(System.getProperty("user.dir") + File.separator + "fined.dat");
        if (finedPath.exists())
            try {
                if (!finedPath.delete()) throw new IOException();
                if (!finedPath.createNewFile()) throw new IOException();
            } catch (IOException ex) {
                Logger.getLogger(PoliceTerminal.class.getName()).log(
                        Level.SEVERE,
                        "I/O Error: could not instantiate serialization files/directories!'\n"
                                + ex.fillInStackTrace().toString()
                );

                return;
            }

        TollPlaza.criminals.forEach(p -> {
            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(finedPath.getPath()));
                ObjectOutputStream objOut = new ObjectOutputStream(out);
            ) {
                objOut.writeObject(p);
            } catch (IOException ex) {
                Logger.getLogger(TollPlaza.class.getName()).log(
                        Level.WARNING,
                        "I/O exception raised during serialization."
                );
            }
        });
    }
    
    public static void stopSimulation() {
    	for (Vehicle veh : vehicles) {
    		veh.interrupt(); 
    	}
    	
    	for (CustomsTerminal ct : TollPlaza.customsTerminals)
    	{
    		ct.interrupt();
    		ct.setCurrentVehicle(null);
    	}
    	
    	for (PoliceTerminal pt : TollPlaza.policeTerminals) {
    		pt.interrupt();
    		pt.setCurrentVehicle(null);
    	}

    	TollPlaza.watcher.isRunning = false;
    	TollPlaza.watcher.interrupt();
    	
    	TollPlaza.isRunning = false;
    	TollPlaza.serialize();
    	TollPlaza.cleanup();    	
    	MainWindow.resetVehicleLabels();
    }
    
    private static void cleanup() { 
    	TollPlaza.vehicles.clear();
    	TollPlaza.criminals.clear();
    	TollPlaza.remainingVehicles.clear();
    	TollPlaza.crossed.clear();
    	TollPlaza.events.clear();
    	TollPlaza.policeTerminals.clear();
    	TollPlaza.customsTerminals.clear();
    	TollPlaza.stoppedVehicles.clear();
    	TollPlaza.criminalsModel.clear();
    	TollPlaza.stoppedVehiclesModel.clear();
    	TollPlaza.eventsModel.clear();
    	TollPlaza.crossedModel.clear();
    	TollPlaza.remainingVehModel.clear();
    	
    	TollPlaza.numVehicles = 0;
    	for (Vehicle v : TollPlaza.vehicleQueue)
    		v = null;
    }
}
