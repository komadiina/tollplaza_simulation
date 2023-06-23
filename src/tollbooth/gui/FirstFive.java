package tollbooth.gui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import tollbooth.plaza.TollPlaza;
import tollbooth.vehicles.Vehicle;

public class FirstFive extends Thread {
	private JLabel[] labels;
	private Vehicle[] vehicleQueue;
	
	public FirstFive(JLabel[] labels) {
		this.vehicleQueue = TollPlaza.vehicleQueue;
		this.labels = labels;
	}
	
	@Override
	public void run() {
		while (true) {
			for (int i = 0; i < labels.length; i++) {
				Vehicle veh = vehicleQueue[i];
				String type = BorderMainThread.getVehType(veh);
				Color color = BorderMainThread.getColor(veh);
				
				final int pos = i;
				SwingUtilities.invokeLater(() -> {
					labels[pos].setText(type);
					labels[pos].setForeground(color);
				});
			}
			
			try {
				Thread.sleep(250);
			} catch (InterruptedException ex) {}
		}
	}
}
