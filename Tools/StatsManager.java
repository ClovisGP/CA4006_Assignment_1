package Tools;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;

import Entities.SynchronizedThread;

public class StatsManager extends SynchronizedThread {

    private static StatsManager single_instance = null;
    private InputStreamReader fileInputStream = new InputStreamReader(System.in);
    private BufferedReader bufferedReader = new BufferedReader(fileInputStream);
    private ArrayList<Integer> booksBought = new ArrayList<Integer>(Arrays.asList(0));
    private String command = "";
    private int tickReset = 0;
    private int numberOfTicksToAgregate = 100;
    private int numberOfDataPointsToKeep = 50;

    private StatsManager() {}

    public static synchronized StatsManager getInstance() {
        if (single_instance == null)
            single_instance = new StatsManager();
  
        return single_instance;
    }

    private String repeatString(String s, int n) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n ; i++)
            builder.append(s);
        return builder.toString();
    }

    private void print(String msg) {
        System.out.print("\033[H\033[2J\n" + msg + "\n" + command + "\nYou can write the new lenght in millieconds of a tick and press enter for the change to take effet.\n");
        System.out.flush();
    }

    private String graph(ArrayList<Integer> data, int horizontalMin, int horizontalMax, String title) {
        String chart = "";

        int height = 13;
        int width = 50;

        int min = 0;
        int max = 0;
        for (int d : data) {
            if (d > max) {
                max = d;
            }
            if (d < min) {
                min = d;
            }
        }

        int indicatorMin = min;
        int indicatorOneThird = (int) (((double)(max - min)) / 3) + min;
        int indicatorTwoThird = (int) (((double)(max - min)) / 3 * 2) + min;
        int indicatorMax = max;

        String strIndicatorMin = Integer.toString(indicatorMin) + " -";
        String strIndicatorOneThird = Integer.toString(indicatorOneThird) + " -";
        String strIndicatorTwoThird = Integer.toString(indicatorTwoThird) + " -";
        String strIndicatorMax = Integer.toString(indicatorMax) + " -";

        strIndicatorMin = repeatString(" ", Math.max(strIndicatorMax.length(), strIndicatorMin.length()) - strIndicatorMin.length()) + strIndicatorMin;
        strIndicatorMax = repeatString(" ", Math.max(strIndicatorMax.length(), strIndicatorMin.length()) - strIndicatorMax.length()) + strIndicatorMax;
        strIndicatorOneThird = repeatString(" ", Math.max(strIndicatorMax.length(), strIndicatorMin.length()) - strIndicatorOneThird.length()) + strIndicatorOneThird;
        strIndicatorTwoThird = repeatString(" ", Math.max(strIndicatorMax.length(), strIndicatorMin.length()) - strIndicatorTwoThird.length()) + strIndicatorTwoThird;
        
        int ordonateNumberLenght = Math.max(strIndicatorMax.length(), strIndicatorMin.length());

        ArrayList<Integer> toShow = new ArrayList<Integer>();
        if (data.size() < width) {
            for (int i = 0; i < width - data.size(); i++) {
                toShow.add(min - 1);
            }
            for (int i = 0; i < data.size(); i++) {
                toShow.add(data.get(i));
            }
        } else {
            for (int i = 0; i < width; i++) {
                toShow.add(data.get(data.size() - width + i));
            }
        }
        
        ArrayList<Integer> floors = new ArrayList<Integer>();
        floors.add(min);
        for (int i = 1; i < height; i++) {
            floors.add((int) (((double)(max - min)) / height * i) + min);
        }
        floors.add(max);

        // add title
        int lenghtLine = ordonateNumberLenght + 1 + width;
        int leftPadding;
        int rightpadding;
        if ((lenghtLine - title.length()) % 2 == 0) {
            leftPadding = (int)((lenghtLine - title.length()) / (double) 2);
            rightpadding = leftPadding;
        } else {
            leftPadding = (int)((lenghtLine - title.length() - 1) / (double) 2) + 1;
            rightpadding = leftPadding - 1;
        }
        chart = repeatString(" ", leftPadding) + title + repeatString(" ", rightpadding) + "\n";
        chart = chart + "\n";

        for (int i = height - 1; i > -1; i--) {
            //add vertical axis
            if (i == height - 1) chart = chart + strIndicatorMax;
            else if (i == 0)  chart = chart + strIndicatorMin;
            else if (indicatorOneThird > floors.get(i) && indicatorOneThird <= floors.get(i + 1)) chart = chart + strIndicatorOneThird;
            else if (indicatorTwoThird > floors.get(i) && indicatorTwoThird <= floors.get(i + 1)) chart = chart + strIndicatorTwoThird;
            else chart = chart + repeatString(" ", strIndicatorMax.length());
            chart = chart + "|";
            for (int j = 0; j < toShow.size(); j++) {
                int number = toShow.get(j);
                if (number == min - 1) {
                    chart = chart + " ";
                } else if (number > floors.get(i) && number <= floors.get(i + 1)) {
                    chart = chart + "+";
                } else if (number == min && i == 0) {
                    chart = chart + "+";
                } else {
                    if (j < toShow.size() - 1 && i != 0) {
                        int next = toShow.get(j + 1);
                        if (next > number && next > floors.get(i + 1) && floors.get(i) >= number) {
                            chart = chart + "|";
                        } else if (next < number && next <= floors.get(i) && floors.get(i + 1) < number) {
                            chart = chart + "|";
                        } else {
                            chart = chart + " ";
                        }
                    } else {
                        chart = chart + " ";
                    }
                }
            }
            chart = chart + "\n";
        }
        // add horizontal axis
        chart = chart + repeatString(" ", ordonateNumberLenght) + "+" + repeatString("-", width) + "\n";
        chart = chart + repeatString(" ", ordonateNumberLenght) + "|" + repeatString(" ", width - 1) + "|\n";
        chart = chart + repeatString(" ", ordonateNumberLenght) + Integer.toString(horizontalMin) + repeatString(" ", 1 + width - Integer.toString(horizontalMin).length() - Integer.toString(horizontalMax).length()) + Integer.toString(horizontalMax) + "\n";
        return chart;
    }

    private String readOnlyWhenDone() {
        String res = "";
        try {
            if (bufferedReader.ready()) res = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public synchronized void statsAddBookBought() {
        int index = booksBought.size() - 1;
        booksBought.set(index, booksBought.get(index) + 1);
    }

    private void printStats() {
        int min = 0;
        int max = tickReset;
        if (tickReset - numberOfTicksToAgregate * 50 > 0) {
            min = tickReset - numberOfTicksToAgregate * 50;
        }
        ArrayList<String> graphs = new ArrayList<String>();
        graphs.add(graph(booksBought, min, max, "Number of book sold"));
        graphs.add(graph(booksBought, min, max, "booksBought"));
        graphs.add(graph(booksBought, min, max, "booksBought"));
        graphs.add(graph(booksBought, min, max, "booksBought"));
        graphs.add(graph(booksBought, min, max, "booksBought"));
        graphs.add(graph(booksBought, min, max, "booksBought"));

        String res;
        print(graphs.get(0));
    }

    private void resetStats() {
        while (booksBought.size() > numberOfDataPointsToKeep) booksBought.remove(0);
        booksBought.add(0);
        
    }

    public void doWork() {
        try {
            if (this.command.length() == 0) {
                if (scheduler.getLenghtMillisecOfTick() == 0) {
                    this.command = "The tick time lenght is 0, the program runs as fast as possible.";
                } else {
                    this.command = "A tick takes " + Integer.toString(scheduler.getLenghtMillisecOfTick()) + " milliseconds minimum.";
                }
            }
            String newCommand = readOnlyWhenDone();
            if (newCommand.length() > 0) {
                int newTickLenght = -1;
                try {
                    newTickLenght = Integer.parseInt(newCommand);
                    if (newTickLenght >= 0) {
                        if (newTickLenght == 0) {
                            this.command = "The tick time lenght is 0, the program runs as fast as possible.";
                        } else {
                            this.command = "A tick takes " + Integer.toString(newTickLenght) + " milliseconds minimum.";
                        }
                        scheduler.setLenghtOfTick(newTickLenght);
                    }
                } catch (Exception e) {}
            }
            if (getTick() >= tickReset + numberOfTicksToAgregate) {
                printStats();
                resetStats();
                tickReset = tickReset + numberOfTicksToAgregate;
            }
        } catch (Exception e) {
            exitThread();
        }
    }

    @Override
    public void exitThread() {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.isRunning = false;
    }
}