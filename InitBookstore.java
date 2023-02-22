import Entities.Book;
import Entities.Section;

import java.util.ArrayList;
import java.util.Arrays;

public class InitBookstore {
    private static Integer nbAssistants = 2;
    private static Integer nbSections = 6;
    private static ArrayList<Section> sectionList = new ArrayList<Section>();
    
    private static void bookSectionManagement() {
        ArrayList<String> sectionNameList = new ArrayList<String>(Arrays.asList("fiction", "horror", "romance", "fantasy", "poetry", "history"));
        if (nbSections < 6) {
            while (sectionNameList.size() > nbSections) {
                sectionNameList.remove(sectionNameList.size() - 1);
            }
        } else if (nbSections > 6) {
            for (int index = 1; sectionNameList.size() < nbSections; index++) {
                sectionNameList.add("Other Category " + index);
            }
        }
        for (String currentName : sectionNameList) {
            sectionList.add(new Section(currentName, 0));
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
        for (Section current : sectionList) {
            System.out.println(current.getName());
        }
    }
}
