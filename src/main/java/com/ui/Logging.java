package com.ui;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class Logging {
    private FileManager fileManager;
    FileWriter logOut;
    Calendar c;

    public Logging(FileManager fileManager) {
        this.fileManager = fileManager;
        c = Calendar.getInstance();
        String dateString = "LOG-" + c.get(Calendar.DAY_OF_MONTH) + "-"+ c.get(Calendar.MONTH) + "-" 
            + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR_OF_DAY) + ":"  + c.get(Calendar.SECOND);
        
        try {
            logOut = new FileWriter(fileManager.getLogsDir() + "/" + dateString + ".txt");
        } catch (IOException e) {
            System.out.println("Failed to create log outfile");
            e.printStackTrace();
        }
    }

    public void logError(String error) {
        try {
            logOut.write("[ERROR] - " + error + " - " + c.get(Calendar.HOUR_OF_DAY) + ":"  + c.get(Calendar.SECOND) + "\n");
            logOut.flush();
        } catch (IOException e) {
            System.out.println("Failed to write to log outfile");
            e.printStackTrace();
        }
    }

    public void logInfo(String info) {
        try {
            logOut.write("[INFO] - " + info + " - " + c.get(Calendar.HOUR_OF_DAY) + ":"  + c.get(Calendar.SECOND) + "\n");
            logOut.flush();
        } catch (IOException e) {
            System.out.println("Failed to write to log outfile");
            e.printStackTrace();
        }
    }

    public void logCommand(String command) {
        try {
            logOut.write("[COMMAND] - " + command + " - " + c.get(Calendar.HOUR_OF_DAY) + ":"  + c.get(Calendar.SECOND) + "\n");
            logOut.flush();
        } catch (IOException e) {
            System.out.println("Failed to write to log outfile");
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            logOut.flush();
            logOut.close();
        } catch (IOException e) {
            System.out.println("Failed to save log outfile");
            e.printStackTrace();
        }
    }
}
