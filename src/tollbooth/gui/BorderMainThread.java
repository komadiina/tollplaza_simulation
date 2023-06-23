package tollbooth.gui;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import tollbooth.plaza.TollPlaza;
import tollbooth.terminals.Terminal;
import tollbooth.vehicles.Car;
import tollbooth.vehicles.Truck;
import tollbooth.vehicles.Vehicle;

public final class BorderMainThread extends Thread {
	private MainWindow window;
	private static FirstFive ff = null;
	
	public BorderMainThread(MainWindow window) {
		this.window = window;
	} 
	
	@Override
	public void run() {
		TollPlaza tp = new TollPlaza(30, 10, 5);
		
		Timer timer = new Timer();
		timer.addPropertyChangeListener(
			new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					Integer newTime = (Integer)evt.getNewValue();
					
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							window.timeElapsedLbl.setText(newTime.toString());
							window.timeElapsedLbl.repaint();
					}
				});
			}
		});
		
		// Setup terminal labels
		setupListener(window.cst1Lbl, TollPlaza.customsTerminals.get(0));
		setupListener(window.cst2Lbl, TollPlaza.customsTerminals.get(1));
		
		setupListener(window.pol1Lbl, TollPlaza.policeTerminals.get(0));
		setupListener(window.pol2Lbl, TollPlaza.policeTerminals.get(1));
		setupListener(window.pol3Lbl, TollPlaza.policeTerminals.get(2));
		
		// Setup first five labels
		ff = new FirstFive(new JLabel[]{MainWindow.veh0lbl, MainWindow.veh1lbl, 
				MainWindow.veh2lbl, MainWindow.veh3lbl, MainWindow.veh4lbl});
		
		window.frmTollPlaza.setVisible(true);

		tp.start();
		ff.start();

		timer.setDaemon(true);
		timer.start();

        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        

        TollPlaza.serialize();
	}
	
	private static void setupListener(JLabel label, Terminal term) {
		term.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Vehicle veh = (Vehicle)evt.getNewValue();
				String type = getVehType(veh);
				
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						label.setText(type);
						label.setForeground(getColor(veh));
						label.repaint();
					}
				});
			}
		});
	}
	
	public static String getVehType(Vehicle v) {
		if (v == null)
			return "---";
		
		if (v instanceof Car) return "CAR";
		else if (v instanceof Truck) return "TRCK";
		return "BUS";
	}
	
	public static Color getColor(Vehicle v) {
		if (v == null)
			return new Color(0,0,0);
		
		if (v instanceof Car)
			return new Color(255, 128, 128);
		else if (v instanceof Truck)
			return new Color(64, 192, 64);
		
		return new Color(128, 128, 255);
	}
	
	public void stopSimulation() {
		TollPlaza.stopSimulation();
	}
}
