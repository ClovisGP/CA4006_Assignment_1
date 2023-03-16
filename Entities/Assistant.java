package Entities;

import java.util.ArrayList;
import Objects.Book;
import Objects.Section;
import Objects.Section.returnType;
import Objects.Delivery;
import Tools.Logger;
import Tools.StatsManager;

public class Assistant extends SynchronizedThread {

    private ArrayList<Section> sectionList;
    private ArrayList<Book> currentBookList = new ArrayList<Book>();
    private Delivery deliveryArea;
    private int assistantCarryCapacity;
    private int assistantMoveTime;
    private int assistantMovePenaltyPerBook;
    private int assistantTimeInsertBookIntoSection;
    private int lastBreakTaken = 0;
    private int ticksBeforeBreakEnd = 0;
    private int breakTime;
    private int minTimeBeforeBreak;
    private int maxTimeBeforeBreak;
    private String currentPosition = "delivery";
    private int waitingTicks = 0;
    private ArrayList<Section> destinationList;
    private StatsManager statsManager;

    private boolean printEndAction = false;
    private String msgEndAction;

    public Assistant(ArrayList<Section> sectionList, Delivery deliveryArea, int assistantCarryCapacity, int assistantMoveTime, int assistantMovePenaltyPerBook, int assistantTimeInsertBookIntoSection, int assistantBreakTime, int assistantMinTimeBeforeBreak, int assistantMaxTimeBeforeBreak, int currentTick) {
        this.sectionList = sectionList;
        this.deliveryArea = deliveryArea;
        this.assistantCarryCapacity = assistantCarryCapacity;
        this.assistantMoveTime = assistantMoveTime;
        this.assistantMovePenaltyPerBook = assistantMovePenaltyPerBook;
        this.assistantTimeInsertBookIntoSection = assistantTimeInsertBookIntoSection;
        this.breakTime = assistantBreakTime;
        this.minTimeBeforeBreak = assistantMinTimeBeforeBreak;
        this.maxTimeBeforeBreak = assistantMaxTimeBeforeBreak;
        Logger.writeLog("T = " + currentTick + " | A new assistant has been created");
        this.statsManager = StatsManager.getInstance();
    }

    /**
     * Tell if the assistant takes a break
     * @param averageSpawnRateInterval - the spawn rate
     * @return boolean - True if the ressource must spawn
     */
    private boolean doesItTakesBreak(Integer averageSpawnRateInterval) {
        if (averageSpawnRateInterval == 0) return true;
        if (Math.random() < (1 / (double)averageSpawnRateInterval)) return true;
        return false;
    }

    /**
     * This function creates a itinerary for the assistant
     */
    private void GPSSetUp() {
        this.destinationList = new ArrayList<Section>();

        for (Section currentSection : this.sectionList) {
            if (searchBookForSection(currentSection.getName())) {
                currentSection.getNbWaitingCustomer();
                if (this.destinationList.size() == 0) {
                    this.destinationList.add(currentSection);
                } else {
                    int index = 0;
                    Boolean isAdd = false;
                    for (; index < this.destinationList.size() && isAdd == false; index++) {
                        if (this.destinationList.get(index).getNbWaitingCustomer() < currentSection.getNbWaitingCustomer()) {
                            this.destinationList.add(index, currentSection);
                            isAdd = true;
                        }
                    }
                    if (isAdd == false) {
                        this.destinationList.add(currentSection);
                    }
                }
            }
        }
    }

    /**
     * The function selects the next destination of the assistant
     * @param availableSection True if there are some space in the section. Unless false
     */
    private void chooseNextDestination(Boolean availableSection) {
        if (availableSection == false) {
            if (this.currentBookList.size() > 1 && this.destinationList.size() > 1) {
                this.destinationList.remove(0);
                moveAction(this.destinationList.get(0).getName());
                this.destinationList.remove(0);
            } else {
                moveAction("delivery");
            }
        } else {
            if (this.currentBookList.size() > 0 && this.destinationList.size() > 0) {
                moveAction(this.destinationList.get(0).getName());
                this.destinationList.remove(0);
            } else if (this.currentBookList.size() > 0) {
                moveAction(this.currentBookList.get(0).getSection());
            } else {
                moveAction("delivery");
            }
        }
    }

    /**
     * Searchs and returns the targetting section
     * @param targetSectionName A String that contains the section name.
     * @return The target section
     */
    private Section searchCurrentSection(String targetSectionName) {
        for (Section section : this.sectionList) {
            if (section.getName() == targetSectionName) return section;
        }
        return null;
    }

    /**
     * This function checks if there are a book of this section.
     * @param targetSectionName A String that contains the section name.
     * @return true if there is a book, unless false.
     */
    private Boolean searchBookForSection(String targetSectionName) {
        for (int index = 0; index < this.currentBookList.size(); index++) {
            if (this.currentBookList.get(index).getSection() == targetSectionName) {
                return true;
            }
        }
        return false;
    }

    /**
     * Takes a book of targetSectionName, returns it and removes it from the list. 
     * @param targetSectionName A string that contains the name of the target section
     * @return A book or null if there are no book fro this section.
     */
    private Book getBookForSection(String targetSectionName) {
        if (this.currentBookList.size() == 0)
            return null;
        for (int index = 0; index < this.currentBookList.size(); index++) {
            if (this.currentBookList.get(index).getSection() == targetSectionName) {
                Book targetBook = this.currentBookList.remove(index);
                return targetBook;
            }
        }
        return null;
    }

    /**
     * This function manages the beginning of a move
     * @param dest The destination section name in String.
     */
    private void moveAction(String dest) {
        this.waitingTicks = this.assistantMoveTime + (this.assistantMovePenaltyPerBook * this.currentBookList.size()) - 1;
        this.currentPosition = dest;

        Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | Assistant ID = " + Thread.currentThread().threadId() + " | An assistant began to travel to " + dest);
        this.printEndAction = true;
        this.msgEndAction = "An assistant has arrived to " + dest;
    }


    /**
     * This function contains the behaviour of a assistant when he is in the delivery area.
     */
    private void deliveryBehaviour() {
        int comp = 0;

        for (;this.currentBookList.size() < this.assistantCarryCapacity && this.deliveryArea.getNbCurrentBook() > 0; comp++) {
            Book delivery = this.deliveryArea.takeBook();
            if (delivery != null) this.currentBookList.add(delivery);
        }
        if (this.currentBookList.size() > 0) {
            Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | Assistant ID = " + Thread.currentThread().threadId() + " | An assistant took " + comp + " book(s) from the delivery box.");
            GPSSetUp();
            chooseNextDestination(true);
        }
    }

    /**
     * This function cantains the behaviour of a assistant when he is in a section.
     */
    private void sectionBehaviour() {
        Section currentSection = searchCurrentSection(this.currentPosition);
        if (currentSection == null) {
            return;
        }
        if (currentSection.isFull()) {
            chooseNextDestination(false);
            return;
        }

        Book currentBook = getBookForSection(this.currentPosition);
        if (currentBook == null) {
            chooseNextDestination(true);
        }
        
        returnType a = currentSection.addBook(currentBook);
        Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | A book has been bought from the " + currentSection.getName() + " section.");
        int count = 1;
        if (assistantTimeInsertBookIntoSection == 0) {
            while (currentBook != null) {
                currentBook = getBookForSection(this.currentPosition);
                currentSection.addBook(currentBook);
                Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | A book has been bought from the " + currentSection.getName() + " section.");
                count++;
            }
            this.waitingTicks = 0;
            this.printEndAction = false;
        } else {
            this.waitingTicks = this.assistantTimeInsertBookIntoSection - 1;
            this.printEndAction = true;
            this.msgEndAction = "An assistant has finished to put a book in the " + this.currentPosition + " section";
        }
        if (count > 1) {
            Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | Assistant ID = " + Thread.currentThread().threadId() + " | An assistant has finished to put " + Integer.toString(count) + " books in the " + this.currentPosition + " section");
        } else {
            Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | Assistant ID = " + Thread.currentThread().threadId() + " | An assistant put a book in the " + this.currentPosition + " section");
        }
    }

    protected void doWork() {
        
        if (this.ticksBeforeBreakEnd > 0) {
            this.ticksBeforeBreakEnd = this.ticksBeforeBreakEnd - 1;
            if (this.ticksBeforeBreakEnd == 0) {
                Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | Assistant ID = " + Thread.currentThread().threadId() + " | An assistant finished his break and returns to work.");
                lastBreakTaken = this.scheduler.getTickNumber();
            }
        } else {
            statsManager.assistantIsWorking();
            if ((this.lastBreakTaken + minTimeBeforeBreak) <= this.scheduler.getTickNumber()) {
                if ((this.lastBreakTaken + maxTimeBeforeBreak) <= this.scheduler.getTickNumber()) {
                    this.ticksBeforeBreakEnd = this.breakTime;
                    Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | Assistant ID = " + Thread.currentThread().threadId() + " | An assistant takes a break.");
                } else if (doesItTakesBreak(maxTimeBeforeBreak - minTimeBeforeBreak)) {
                    Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | Assistant ID = " + Thread.currentThread().threadId() + " | An assistant takes a break.");
                    this.ticksBeforeBreakEnd = this.breakTime;
                }
            }

            if (this.ticksBeforeBreakEnd <= 0) {
                if (this.waitingTicks > 0) {
                    this.waitingTicks = this.waitingTicks - 1;
                    if (this.waitingTicks == 0 && this.printEndAction) {
                        this.printEndAction = false;
                        Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | Assistant ID = " + Thread.currentThread().threadId() + " | " + msgEndAction);
                    }
                } else {
                    if (this.currentPosition == "delivery") {
                        deliveryBehaviour();
                    } else {
                        sectionBehaviour();
                    }
                }
            }
        }
    }
}

/*
Do you know the story of an optimistic guy?
As he fell from an huge building, he said on each floor "Jusqu'ici, tout va bien" (translation: "so far all is well") while laughing.
*/