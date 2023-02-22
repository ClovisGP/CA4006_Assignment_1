import Entities.Book;

import java.util.ArrayList;
import java.util.Arrays;

public class InitBookstore {
    private static Integer nbAssistants = 2;
    private static Integer nbSections = 6;
    private static ArrayList<String> sectionList = new ArrayList<String>(Arrays.asList("fiction", "horror", "romance", "fantasy", "poetry", "history"));
    
    private static void bookSectionManagement() {
        if (nbSections < 6) {
            while (sectionList.size() > nbSections) {
                sectionList.remove(sectionList.size() - 1);
            }
        } else if (nbSections > 6) {
            for (int index = 1; sectionList.size() < nbSections; index++) {
                sectionList.add("Other Category " + index);
            }
        }
    }
    public static void main(String[] args) {
        for (String src: args) {
            String[] tmpArray = src.split("=");

            if (tmpArray[0].equals("a")) {
                nbAssistants = (Integer.parseInt(tmpArray[1]) != 0) ? Integer.parseInt(tmpArray[1]) : nbAssistants;
            } else if (tmpArray[0].equals("s")) {
                nbSections = (Integer.parseInt(tmpArray[1]) != 0) ? Integer.parseInt(tmpArray[1]) : nbSections;
            }
        }
        bookSectionManagement();
        for (String test : sectionList) {
            System.out.println(test);
        }
    }
}
