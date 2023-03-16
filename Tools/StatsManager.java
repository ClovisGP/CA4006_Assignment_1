package Tools;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import Entities.SynchronizedThread;

public class StatsManager extends SynchronizedThread {

    private static StatsManager single_instance = null;
    private InputStreamReader fileInputStream = new InputStreamReader(System.in);
    private BufferedReader bufferedReader = new BufferedReader(fileInputStream);
    private ArrayList<Integer> booksSold = new ArrayList<Integer>(Arrays.asList(0));
    private ArrayList<Integer> booksWaitingDelivery = new ArrayList<Integer>(Arrays.asList(0));
    private ArrayList<Integer> booksWaitingInSection = new ArrayList<Integer>(Arrays.asList(0));
    private ArrayList<Integer> customersWaitingInSection = new ArrayList<Integer>(Arrays.asList(0));
    private ArrayList<Integer> numberOfAssistantWorking = new ArrayList<Integer>(Arrays.asList(0));
    private String command = "";
    private int tickReset = 0;
    private int numberOfTicksToAgregate = 1;
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
        System.out.print("\033[H\033[2J\n" + msg + "\n" + command + "\nYou can write the new lenght in millieconds of a tick and press enter for the change to take effet.\nEnter 'quit' or use CTRL + C to exit. If you have issues seeing all the charts please increase the size of the terminal.\n");
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
        chart = chart + repeatString(" ", leftPadding + title.length() + rightpadding) + "\n";

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

    public void statsAddBookBought() {
        int index = booksSold.size() - 1;
        booksSold.set(index, booksSold.get(index) + 1);
    }

    public void booksInSection(int bookNumber) {
        int index = booksWaitingInSection.size() - 1;
        booksWaitingInSection.set(index, booksWaitingInSection.get(index) + bookNumber);
    }

    public void booksInDelivery(int bookNumber) {
        int index = booksWaitingDelivery.size() - 1;
        booksWaitingDelivery.set(index, booksWaitingDelivery.get(index) + bookNumber);
    }

    public void numberClientWaitingInSection(int clients) {
        int index = customersWaitingInSection.size() - 1;
        customersWaitingInSection.set(index, customersWaitingInSection.get(index) + clients);
    }

    public void assistantIsWorking() {
        int index = numberOfAssistantWorking.size() - 1;
        numberOfAssistantWorking.set(index, numberOfAssistantWorking.get(index) + 1);
    }

    private String myConcat(String chart1, String chart2) {
        String res = "";
        int width = 0;
        while (width < chart1.length() && chart1.charAt(width) != '\n') width++;
        String[] split = chart1.split("\n");
        int height = split[split.length - 1].isEmpty() ? split.length - 1 : split.length;
        
        String[] split1 = chart1.split("\n");
        String[] split2 = chart2.split("\n");
        for (int i = 0; i < height; i++) {
            res = res.concat( split1[i] + "    " + split2[i] + "\n");
        }
        return res;
    }

    private void printStats() {
        int min = 0;
        int max = tickReset;
        if (tickReset - numberOfTicksToAgregate * 50 > 0) {
            min = tickReset - numberOfTicksToAgregate * 50;
        }

        ArrayList<Integer> averageBookinWaitingSection = new ArrayList<Integer>();
        for (Integer d : booksWaitingInSection) {
            averageBookinWaitingSection.add((int)(d / (double)numberOfTicksToAgregate));
        }
        ArrayList<Integer> averageBookInDelivery = new ArrayList<Integer>();
        for (Integer d : booksWaitingDelivery) {
            averageBookInDelivery.add((int)(d / (double)numberOfTicksToAgregate));
        }
        ArrayList<Integer> averageCustomersWaitingInSection = new ArrayList<Integer>();
        for (Integer d : customersWaitingInSection) {
            averageCustomersWaitingInSection.add((int)(d / (double)numberOfTicksToAgregate));
        }
        ArrayList<Integer> averageNumberOfAssistantWorking = new ArrayList<Integer>();
        for (Integer d : numberOfAssistantWorking) {
            averageNumberOfAssistantWorking.add((int)(d / (double)numberOfTicksToAgregate));
        }

        String chart1 = graph(booksSold, min, max, "Number of books sold.");
        String chart2 = graph(averageBookinWaitingSection, min, max, "Number of books waiting in the sections.");
        String chart3 = graph(averageBookInDelivery, min, max, "Number of books waiting in the delivery area.");
        String chart4 = graph(averageCustomersWaitingInSection, min, max, "Number of clients waiting.");
        String chart5 = graph(averageNumberOfAssistantWorking, min, max, "Number of assistants working.");

        String res;
        res = myConcat(chart1, chart2);
        res = myConcat(res, chart3);
        res = res + "\n\n";
        res = res + myConcat(chart4, chart5);
        print(res);
    }

    private void resetStats() {
        while (booksSold.size() > numberOfDataPointsToKeep) booksSold.remove(0);
        booksSold.add(0);
        while (booksWaitingInSection.size() > numberOfDataPointsToKeep) booksWaitingInSection.remove(0);
        booksWaitingInSection.add(0);
        while(booksWaitingDelivery.size() > numberOfDataPointsToKeep) booksWaitingDelivery.remove(0);
        booksWaitingDelivery.add(0);
        while(customersWaitingInSection.size() > numberOfDataPointsToKeep) customersWaitingInSection.remove(0);
        customersWaitingInSection.add(0);
        while(numberOfAssistantWorking.size() > numberOfDataPointsToKeep) numberOfAssistantWorking.remove(0);
        numberOfAssistantWorking.add(0);
    }

    public void showStats() {
        if (getTick() >= tickReset + numberOfTicksToAgregate) {
            printStats();
            resetStats();
            tickReset = tickReset + numberOfTicksToAgregate;
        }
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
                if (newCommand.equals("quit")) System.exit(0);
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