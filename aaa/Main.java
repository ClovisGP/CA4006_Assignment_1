package aaa;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Arrays;

import stuff.TimeScheduler;
import Entities.Book;
import Entities.Section;

public class Main {

    public static void main(String[] args) {
        
        // sections names                               -sn noms_separated_by_space_no_quotes
        // section capacity                             -sc number
        // starting number of books in section          -ssbn number
        // ou générer x sections                        -sg number

        // tick time value                              -tv number

        // client spawn rate                            -csr number

        // box spawn rate                               -bsr number
        // box size                                     -bs number
        
        // assistant number                             -an number
        // carry capa assistant                         -acn number
        // move time between section assistant          -amst number
        // move penalty for each book being carried     -am number
        // time to put a book in the section            -atbs number
        // break time (in ticks) assistant              -abt number
        // break interval start                         -abis number
        // break interval end                           -abie number

        // help page                                    -h
        // run with gui                                 -gui
        // run with default                             -d

        // defaults
        ArrayList<String> sectionNames = new ArrayList<String>(Arrays.asList("fiction", "horror", "romance", "fantasy", "poetry", "history"));
        int sectionCapacity = 10; // if it is set to 0 it will mean infinite capacity
        int startingNumberBooksSection = 1;

        int tickTimeValue = 100;

        int clientSpawnRate = 10;

        int bowSpawnRate = 100;
        int boxSpawnSize = 10;
        
        int assistantNumber = 1;
        int assistantCarryCapacity = 10;
        int assistantMoveTime = 10;
        int assistantMovePenaltyPerBook = 1;
        int assistantTimeInsertBookIntoSection = 1;
        int assistantBreakTime = 150; // if set to 0, the assistant do not take breaks
        int assistantMinTimeBeforeBreak = 200;
        int assistantMaxTimeBeforeBreak = 300; // the chance of a break will be: 1 / (assistantMaxTimeBeforeBreak - assistantMinTimeBeforeBreak)

        boolean startWithGUI = true;
        
        // Temporaire for testing bookstore
        Section deliveryArea = new Section("Delivery", 0);
        deliveryArea.takeBook();
        deliveryArea.addBook(new Book("fiction"));
        deliveryArea.addBook(new Book("horror"));
        deliveryArea.addBook(new Book("fantasy"));
        ArrayList<Section> sectionList = new ArrayList<Section>();
        for (String current : sectionNames) {
            sectionList.add(new Section(current, sectionCapacity));
        }

        TimeScheduler scheduler = new TimeScheduler();
        scheduler.addBookstore(sectionList, deliveryArea, clientSpawnRate, /*bowSpawnRate*/10, boxSpawnSize);
        scheduler.addAssistant(sectionList, deliveryArea, assistantCarryCapacity, assistantMoveTime, assistantMovePenaltyPerBook, assistantTimeInsertBookIntoSection, assistantBreakTime, assistantMinTimeBeforeBreak, assistantMaxTimeBeforeBreak);
        scheduler.start();

        // for (int i = 0; i < 5; i++) {
        //     try {
        //         TimeUnit.MILLISECONDS.sleep(5000);
        //         scheduler.addWorker();
        //     } catch (InterruptedException e) {
        //         System.out.println(e);
        //     }
        // }

        // for (int i = 0; i < 7; i++) {
        //     try {
        //         TimeUnit.MILLISECONDS.sleep(5000);
        //         scheduler.removeWorker();
        //     } catch (InterruptedException e) {
        //         System.out.println(e);
        //     }
        // }
    }
}