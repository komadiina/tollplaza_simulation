package tollbooth.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import tollbooth.plaza.TollPlaza;

public final class Timer extends Thread {
    public static Handler handler;

    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir") + File.separator +
                    "logs" + File.separator + "Timer.log");
            Logger.getLogger(Timer.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
    }
	
	public Integer timeElapsed = 0;
	
	public Timer() {}
	
    private final PropertyChangeSupport propChangeSupport = new PropertyChangeSupport(this);
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propChangeSupport.removePropertyChangeListener(listener);
    }
	
	@Override
	public void run() {
		while (TollPlaza.isRunning) {
			this.timeElapsed++;
			this.propChangeSupport.firePropertyChange("tick", (Object)(timeElapsed-1), (Object)timeElapsed);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				Logger.getLogger(Timer.class.getName()).log(
						Level.WARNING,
						"Timer service failed."
				);	
			}
		}
	}
}
