package tollbooth.terminals;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

import tollbooth.plaza.TollPlaza;

import static java.nio.file.StandardWatchEventKinds.*;

public final class TerminalWatcher extends Thread {
    public static Handler handler;

    static {
        try {
            handler = new FileHandler(System.getProperty("user.dir") + File.separator + "logs" + File.separator + "TerminalWatcher.log");
            Logger.getLogger(TerminalWatcher.class.getName()).addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public final List<Terminal> terminals;
    public final String fileDirectory;
    public final String fileName;
    public WatchService watcher;
    public boolean isRunning = true;

    public TerminalWatcher(String fileDirectory, String fileName, WatchService watcher, List<Terminal> terminals) throws IllegalArgumentException {
        if (!fileName.endsWith(".txt"))
            throw new IllegalArgumentException("File extension does not match predicted! (Expected: .txt)");

        this.fileDirectory = fileDirectory;
        this.fileName = fileName;
        this.watcher = watcher;
        this.terminals = terminals;
    }

    @Override
    public void run() {
        File targetFile;
        Path filePath;
 
        // Assure existence of specified paths
        try {
            targetFile = new File(this.fileDirectory);
            if (!targetFile.exists())
                if (!targetFile.mkdirs()) throw new IOException();

            targetFile = new File(targetFile.getPath() + File.separator + this.fileName);
            targetFile.createNewFile();

            filePath = Paths.get(targetFile.getPath());
            Paths.get(this.fileDirectory).register(watcher, ENTRY_MODIFY, ENTRY_DELETE, ENTRY_CREATE);
        } catch (IOException ex) {
            Logger.getLogger(TerminalWatcher.class.getName()).log(Level.SEVERE,
                    "(TerminalWatcher::run): Failed instantiating logfiles!");
            System.err.println("[WATCHSERVICE] Watcher service could not be registered for " + this.fileDirectory);
            return;
        }

        while (isRunning) {
            try {
                WatchKey key = this.watcher.take();
                
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind().equals(ENTRY_MODIFY)) {
                        List<String> content = Files.readAllLines(filePath);
                        this.pauseTerminals(content);
                    }
                }
                
                key.reset();
            } catch (Exception ex) {
                Logger.getLogger(TerminalWatcher.class.getName()).log(Level.WARNING,
                        "TerminalWatcher service faulted: " + ex.fillInStackTrace().toString());
            }
        }
    }

    private void pauseTerminals(List<String> ids) {
    	List<Terminal> markForPausing = new ArrayList<>();
    	
        this.terminals.forEach(t -> {
            for (String termId : ids) {
                if (t.id.equals(termId)) {
                    markForPausing.add(t);
                    break;
                }
            }
        });
        
        this.terminals.forEach(t -> {
        	if (markForPausing.contains(t))
        	{
        		t.paused = true;
        		TollPlaza.logEvent(String.format("! Terminal [%1$s] paused.", t.id));
        	} 
        	else {
        		if (t.paused) { 
        		t.paused = false;
        		TollPlaza.logEvent(String.format("! Terminal [%1$s] unpaused.", t.id));
        		
        		synchronized (t.terminalPauseLock) {
        			t.terminalPauseLock.notify();
        		}  
        		}
        	}
        });
    }
}
