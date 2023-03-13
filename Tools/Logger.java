package Tools;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class aims to manage the log action.
 */
public class Logger {
    private static String fileName = "output.dat";

    /**
     * This function aims to prepare the log file by removing all it content and create it if it doesn't exist already.
     */
    public static synchronized void initLoggerFile() {
        try {
            // Check if the file exists and if it not the case, create it.
            File checkFile = new File(fileName);
            if (checkFile.exists() && !checkFile.isDirectory()) {
                FileWriter logFile = new FileWriter(fileName);
                logFile.write("");
                logFile.close();
            } else {
                checkFile.createNewFile();
            }



        } catch (IOException e) {
            System.out.println("An error occurred during in the logger => " + e);
        }
    }

    /**
     * This function aims to write a message in the log file and in the standart output
     * @param msgThe message you want to write
     */
    public static synchronized void writeLog(String msg) {
        try {
            //System.out.println(msg);

            // Write the message in the file.
            BufferedWriter logFile = new BufferedWriter(new FileWriter(fileName, true));
            logFile.append(msg + "\n");
            logFile.close();
        } catch (IOException e) {
            System.out.println("An error occurred during in the logger => " + e);
        }
    }
}
