public class InitBookstore {
    private static Integer nbAssistants = 2;
    private static Integer nbSections = 5;
    public static void main(String[] args) {
        for (String src: args) {
            String[] tmpArray = src.split("=");

            if (tmpArray[0].equals("a")) {
                nbAssistants = (Integer.parseInt(tmpArray[1]) != 0) ? Integer.parseInt(tmpArray[1]) : nbAssistants;
            } else if (tmpArray[0].equals("s")) {
                nbSections = (Integer.parseInt(tmpArray[1]) != 0) ? Integer.parseInt(tmpArray[1]) : nbSections;
            }
        }
        System.out.println(nbAssistants + " " + nbSections);
    }
}
