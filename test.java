import java.util.ArrayList;
import java.util.Arrays;

public class test {
    private static String lastMessage = "";

    private static String repeaString(String s, int n) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n ; i++)
            builder.append(s);
        return builder.toString();
    }

    private static void print(String msg) {
        System.out.print(repeaString("\n", 10));
        System.out.print(msg);
    }

    private static void graph(ArrayList<Integer> data, int horizontalMin, int horizontalMax) {
        String chart = "";

        int height = 10;
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
        min = min - 1;
        max = max + 1;

        int indicatorMin = min;
        int indicatorOneThird = (int) ((double)(max - min)) / 3 + min;
        int indicatorTwoThird = (int) ((double)(max - min)) / 3 * 2 + min;
        int indicatorMax = max;

        String strIndicatorMin = Integer.toString(indicatorMin) + " -";
        String strIndicatorOneThird = Integer.toString(indicatorOneThird) + " -";
        String strIndicatorTwoThird = Integer.toString(indicatorTwoThird) + " -";
        String strIndicatorMax = Integer.toString(indicatorMax) + " -";

        strIndicatorMin = repeaString(" ", Math.max(strIndicatorMax.length(), strIndicatorMin.length()) - strIndicatorMin.length()) + strIndicatorMin;
        strIndicatorOneThird = repeaString(" ", Math.max(strIndicatorMax.length(), strIndicatorMin.length()) - strIndicatorOneThird.length()) + strIndicatorOneThird;
        strIndicatorTwoThird = repeaString(" ", Math.max(strIndicatorMax.length(), strIndicatorMin.length()) - strIndicatorTwoThird.length()) + strIndicatorTwoThird;

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
            floors.add((int) ((double)(max - min)) / height * i + min);
        }
        floors.add(max);

        for (int i = height - 1; i > -1; i--) {
            //add vertical axis
            if (i == height - 1) chart = chart + strIndicatorMax;
            else if (i == 0)  chart = chart + strIndicatorMin;
            else if (indicatorOneThird > floors.get(i) && indicatorOneThird < floors.get(i + 1)) chart = chart + strIndicatorOneThird;
            else if (indicatorTwoThird > floors.get(i) && indicatorTwoThird < floors.get(i + 1)) chart = chart + strIndicatorTwoThird;
            else chart = chart + repeaString(" ", strIndicatorMax.length());
            chart = chart + "|";
            for (int j = 0; j < toShow.size(); j++) {
                int number = toShow.get(j);
                if (number == min - 1) {
                    chart = chart + " ";
                } else if (number > floors.get(i) && number <= floors.get(i + 1)) {
                    chart = chart + "+";
                } else {
                    if (j < toShow.size() - 1 && i != 0) {
                        int next = toShow.get(j + 1);
                        if (next > number && next > floors.get(i + 1) && floors.get(i) > number) {
                            chart = chart + "|";
                        } else if (next < number && next < floors.get(i) && floors.get(i + 1) < number) {
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
        int a = Math.max(strIndicatorMax.length(), strIndicatorMin.length());
        chart = chart + repeaString(" ", a) + "+" + repeaString("-", width) + "\n";
        chart = chart + repeaString(" ", a) + "|" + repeaString(" ", width - 1) + "|\n";
        //((int)((Integer.toString(horizontalMin).length() + 1) / (double) 2) - 1)
        chart = chart + repeaString(" ", a) + Integer.toString(horizontalMin) + repeaString(" ", 1 + width - Integer.toString(horizontalMin).length() - Integer.toString(horizontalMax).length()) + Integer.toString(horizontalMax) + "\n";
        print(chart);
    }

    public static void main(String[] args) {
        ArrayList<Integer> data = new ArrayList<Integer>();
        while (true) {
            data.add((int) (Math.random() * 255));
            graph(data, 50, 1555);
            try {Thread.sleep(200);} catch (InterruptedException e) {}
        }
    }
}