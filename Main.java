import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Arrays;

import Entities.TimeScheduler;
import Entities.Bookstore;

import Objects.Book;
import Objects.Delivery;
import Objects.Section;

import Tools.Logger;
import Tools.StatsManager;

public class Main {

    private static void printHelp() {
        System.out.println("This program has been made for the course CA4006 in 2023 by Martin Ferrand and Clovis Gilles.");
        System.out.println("Usage:");
        System.out.println("    Sections:");
        System.out.println("        -sn name1 [name2 name3 (...)]: The names of the sections, separated by spaces with no \".");
        System.out.println("            default: -sn fiction horror romance fantasy poetry history");
        System.out.println("        -sc number: Number of books a section can hold. If set to 0, the section can hold an infinite amount of books.");
        System.out.println("            default: -sc 20");
        System.out.println("        -ssbn number: Starting number of books in a section.");
        System.out.println("            default: -ssbn 1");
        System.out.println("        -sg number: The number of section to generate (the names won't be pretty however....). No default value. Must be more than 0.");
        System.out.println("    Ticks:");
        System.out.println("        -tv number: The minimum time a tick must take in milliseconds.");
        System.out.println("            default: -tv 10");
        System.out.println("    Clients:");
        System.out.println("        -csr number: The average number of tick a client will take to spawn.");
        System.out.println("            default: -csr 10");
        System.out.println("    Deliveries:");
        System.out.println("        -dsr number: The average number of tick a box will take to spawn.");
        System.out.println("            default: -dsr 100");
        System.out.println("        -ds number: the number of books a box hold when delivered.");
        System.out.println("            default: -ds 10");
        System.out.println("    Assistants:");
        System.out.println("        -an number: The number of assistants to use.");
        System.out.println("            default: -an 10");
        System.out.println("        -acn number: The number of books an assistant can carry at once.");
        System.out.println("            default: -acn 10");
        System.out.println("        -amst number: The number of ticks it takes an assistant to move between sections.");
        System.out.println("            default: -amst 10");
        System.out.println("        -am number: The number of additional ticks it takes an assistant to move between section for each books carried.");
        System.out.println("            default: -am 1");
        System.out.println("        -atbs number: The number of ticks it require an assistant to put a book in a section.");
        System.out.println("            default: -atbs 1");
        System.out.println("        -abt number: The lenght (in ticks) of an assistant's break. If set to 0 the assistants will not take breaks.");
        System.out.println("            default: -abt 150");
        System.out.println("        -abis number: The minimum amount of ticks an assistant will work before it can start taking a break.");
        System.out.println("            default: -abis 200");
        System.out.println("        -abie number: The maximum amount of ticks an assistant can work before it must take a break.");
        System.out.println("            default: -abie 300");
        System.out.println("    Miscellaneous:");
        System.out.println("        -h: Show the help page.");
        System.out.println("        -d: Use all the default parameters shown.");
        System.exit(0);
    }

    public static void main(String[] args) {

        // defaults
        ArrayList<String> sectionNames = new ArrayList<String>(); // the real default value is later in the code
        int sectionCapacity = 20; // if it is set to 0 it will mean infinite capacity
        int startingNumberBooksSection = 1;
        int generateSection = 0;

        int tickTimeValue = 10;

        int clientSpawnRate = 10;

        int bowSpawnRate = 100;
        int boxSpawnSize = 10;
        
        int assistantNumber = 10;
        int assistantCarryCapacity = 10;
        int assistantMoveTime = 10;
        int assistantMovePenaltyPerBook = 1;
        int assistantTimeInsertBookIntoSection = 1;
        int assistantBreakTime = 150; // if set to 0, the assistant does not take breaks
        int assistantMinTimeBeforeBreak = 200;
        int assistantMaxTimeBeforeBreak = 300; // the chance of a break will be: 1 / (assistantMaxTimeBeforeBreak - assistantMinTimeBeforeBreak)

        // start dealing with the arguments
        if (args.length == 0) {
            printHelp();
        }
        for (int i = 0; i < args.length; i++) {
            String token = args[i];

            if (token.startsWith("-")) {
                if (token.equals("-sn")) {
                    ArrayList<String> tmpSectionNames = new ArrayList<String>();
                    // the following tokens are section names
                    i++;
                    while (i < args.length && !args[i].startsWith("-")) {
                        tmpSectionNames.add(args[i]);
                        i++;
                    }
                    if (tmpSectionNames.size() == 0) {
                        printHelp();
                    } else {
                        sectionNames.clear();
                        sectionNames.addAll(tmpSectionNames);
                    }
                } else if (token.equals("-sc")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        i++;
                        try {
                            sectionCapacity = Integer.parseInt(args[i]);
                            if (sectionCapacity < 0) {
                                System.out.println("-sc cannot be negative.");
                                printHelp();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("The argument after '-sc' is not an integer.");
                            printHelp();
                        }
                    }
                } else if (token.equals("-ssbn")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        i++;
                        try {
                            startingNumberBooksSection = Integer.parseInt(args[i]);
                            if (startingNumberBooksSection < 0) {
                                System.out.println("-ssbn cannot be negative.");
                                printHelp();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("The argument after '-ssbn' is not an integer.");
                            printHelp();
                        }
                    }
                } else if (token.equals("-sg")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        i++;
                        try {
                            generateSection = Integer.parseInt(args[i]);
                            if (generateSection < 1) {
                                System.out.println("-sg must be at least 1.");
                                printHelp();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("The argument after '-sg' is not an integer.");
                            printHelp();
                        }
                    }
                } else if (token.equals("-tv")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        i++;
                        try {
                            tickTimeValue = Integer.parseInt(args[i]);
                            if (tickTimeValue < 0) {
                                System.out.println("-tv cannot be negative.");
                                printHelp();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("The argument after '-tv' is not an integer.");
                            printHelp();
                        }
                    }
                } else if (token.equals("-csr")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        i++;
                        try {
                            clientSpawnRate = Integer.parseInt(args[i]);
                            if (clientSpawnRate < 1) {
                                System.out.println("-csr must be at least 1.");
                                printHelp();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("The argument after '-csr' is not an integer.");
                            printHelp();
                        }
                    }
                } else if (token.equals("-dsr")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        i++;
                        try {
                            bowSpawnRate = Integer.parseInt(args[i]);
                            if (bowSpawnRate < 1) {
                                System.out.println("-dsr must be at least 1.");
                                printHelp();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("The argument after '-dsr' is not an integer.");
                            printHelp();
                        }
                    }
                } else if (token.equals("-ds")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        i++;
                        try {
                            boxSpawnSize = Integer.parseInt(args[i]);
                            if (boxSpawnSize < 1) {
                                System.out.println("-ds must be at least 1.");
                                printHelp();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("The argument after '-ds' is not an integer.");
                            printHelp();
                        }
                    }
                }
                else if (token.equals("-an")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        i++;
                        try {
                            assistantNumber = Integer.parseInt(args[i]);
                            if (assistantNumber < 1) {
                                System.out.println("-an must be at least 1.");
                                printHelp();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("The argument after '-an' is not an integer.");
                            printHelp();
                        }
                    }
                } else if (token.equals("-acn")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        i++;
                        try {
                            assistantCarryCapacity = Integer.parseInt(args[i]);
                            if (assistantCarryCapacity < 1) {
                                System.out.println("-acn must be at least 1.");
                                printHelp();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("The argument after '-acn' is not an integer.");
                            printHelp();
                        }
                    }
                } else if (token.equals("-amst")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        i++;
                        try {
                            assistantMoveTime = Integer.parseInt(args[i]);
                            if (assistantMoveTime < 1) {
                                System.out.println("-amst must be at least 1.");
                                printHelp();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("The argument after '-amst' is not an integer.");
                            printHelp();
                        }
                    }
                } else if (token.equals("-am")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        i++;
                        try {
                            assistantMovePenaltyPerBook = Integer.parseInt(args[i]);
                            if (assistantMovePenaltyPerBook < 0) {
                                System.out.println("-am can not be negative.");
                                printHelp();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("The argument after '-am' is not an integer.");
                            printHelp();
                        }
                    }
                } else if (token.equals("-atbs")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        i++;
                        try {
                            assistantTimeInsertBookIntoSection = Integer.parseInt(args[i]);
                            if (assistantTimeInsertBookIntoSection < 0) {
                                System.out.println("-atbs can not be negative.");
                                printHelp();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("The argument after '-atbs' is not an integer.");
                            printHelp();
                        }
                    }
                } else if (token.equals("-abt")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        i++;
                        try {
                            assistantBreakTime = Integer.parseInt(args[i]);
                            if (assistantBreakTime < 0) {
                                System.out.println("-abt can not be negative.");
                                printHelp();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("The argument after '-abt' is not an integer.");
                            printHelp();
                        }
                    }
                } else if (token.equals("-abis")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        i++;
                        try {
                            assistantMinTimeBeforeBreak = Integer.parseInt(args[i]);
                            if (assistantMinTimeBeforeBreak < 0) {
                                System.out.println("-abis can not be negative.");
                                printHelp();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("The argument after '-abis' is not an integer.");
                            printHelp();
                        }
                    }
                } else if (token.equals("-abie")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        i++;
                        try {
                            assistantMaxTimeBeforeBreak = Integer.parseInt(args[i]);
                            if (assistantMaxTimeBeforeBreak < 1) {
                                System.out.println("-abie must be at least 1.");
                                printHelp();
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("The argument after '-abie' is not an integer.");
                            printHelp();
                        }
                    }
                } else if (token.equals("-d")) {
                    if (args.length > 1) {
                        System.out.println("The argument '-d' can only be used alone.");
                        printHelp();
                    }
                    break;
                } else {
                    printHelp();
                }
            }
        }

        if (generateSection > 0 && sectionNames.size() == 0) {
            for (int i = 0; i < generateSection; i++) {
                sectionNames.add("Section-" + Integer.toString(i));
            }
        }
        if (sectionNames.size() == 0) {
            // default value for the sections' name
            sectionNames = new ArrayList<String>(Arrays.asList("fiction", "horror", "romance", "fantasy", "poetry", "history"));
        }

        Logger.initLoggerFile();
        StatsManager statsManager = StatsManager.getInstance();
        
        ArrayList<Section> sectionList = new ArrayList<Section>();
        for (String name : sectionNames) {
            sectionList.add(new Section(name, sectionCapacity, startingNumberBooksSection));
        }

        Delivery delivery = new Delivery(boxSpawnSize, sectionNames);
        delivery.doADelivery();

        TimeScheduler scheduler = new TimeScheduler(tickTimeValue);
        scheduler.addStatsManager(statsManager);
        scheduler.addBookstore(sectionList, delivery, clientSpawnRate, bowSpawnRate);
        for (int i = 0; i < assistantNumber; i++) {
            scheduler.addAssistant(sectionList, delivery, assistantCarryCapacity, assistantMoveTime, assistantMovePenaltyPerBook, assistantTimeInsertBookIntoSection, assistantBreakTime, assistantMinTimeBeforeBreak, assistantMaxTimeBeforeBreak);
        }
        scheduler.start();
    }
}