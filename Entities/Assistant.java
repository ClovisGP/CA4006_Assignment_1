package Entities;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import Objects.Book;
import Objects.Section;

import Tools.Logger;


public class Assistant extends SynchronizedThread {

    private ArrayList<Section> sectionList;
    private ArrayList<Book> currentBookList = new ArrayList<Book>();
    private Section deliveryArea;
    private int assistantCarryCapacity;
    private int assistantMoveTime;
    private int assistantMovePenaltyPerBook;
    private int assistantTimeInsertBookIntoSection;
    private int assistantBreakTime;
    private int assistantMinTimeBeforeBreak;
    private int assistantMaxTimeBeforeBreak;
    private String currentPosition = "delivery";
    private int waitingTicks = 0;
    private ArrayList<Section> destinationList;

    private boolean printEndAction = false;
    private String msgEndAction;

    public Assistant(ArrayList<Section> sectionList, Section deliveryArea, int assistantCarryCapacity, int assistantMoveTime, int assistantMovePenaltyPerBook, int assistantTimeInsertBookIntoSection, int assistantBreakTime, int assistantMinTimeBeforeBreak, int assistantMaxTimeBeforeBreak, int currentTick) {
        this.sectionList = sectionList;
        this.deliveryArea = deliveryArea;
        this.assistantCarryCapacity = assistantCarryCapacity;
        this.assistantMoveTime = assistantMoveTime;
        this.assistantMovePenaltyPerBook = assistantMovePenaltyPerBook;
        this.assistantTimeInsertBookIntoSection = assistantTimeInsertBookIntoSection;
        this.assistantBreakTime = assistantBreakTime;
        this.assistantMinTimeBeforeBreak = assistantMinTimeBeforeBreak;
        this.assistantMaxTimeBeforeBreak = assistantMaxTimeBeforeBreak;
        Logger.writeLog("T = " + currentTick + " | A new assistant is created");
    }

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
        for (Section test: this.destinationList) {
            System.out.println(test.getName());
        }
    }

    private void chooseNextDestination(Boolean availableSection) {
        if (availableSection == false) {//Don't work, if the delivery is full, he leave and go back
            if (this.currentBookList.size() > 1 && this.destinationList.size() > 1) {
                this.destinationList.remove(0);
                moveAction(this.destinationList.get(0).getName());
                this.destinationList.remove(0);
            } else if (this.currentBookList.size() > 1) {
                moveAction(this.currentBookList.get(1).getSection());
            } else {
                moveAction("delivery");
            }
        }
        if (this.currentBookList.size() > 0 && this.destinationList.size() > 0) {
            moveAction(this.destinationList.get(0).getName());
            this.destinationList.remove(0);
        } else if (this.currentBookList.size() > 0) {
            moveAction(this.currentBookList.get(0).getSection());
        } else {
            moveAction("delivery");
        }
    }

    private Section searchCurrentSection(String targetSectionName) {
        int index = 0;
        for (; this.sectionList.get(index).getName() == targetSectionName; index++);
        return sectionList.get(index);
    }

    private Boolean searchBookForSection(String targetSectionName) {
        for (int index = 0; index < this.currentBookList.size(); index++) {
            if (this.currentBookList.get(index).getSection() == targetSectionName) {
                return true;
            }
        }
        return false;
    }

    private Book getBookForSection(String targetSectionName) {
        if (this.currentBookList.size() == 0)
            return null;
        for (int index = 0; index < this.currentBookList.size(); index++) {
            if (this.currentBookList.get(index).getSection() == targetSectionName) {
                Book targetBook = this.currentBookList.get(index);
                this.currentBookList.remove(index);
                return targetBook;
            }
        }
        return null;
    }

    private void moveAction(String dest) {
        this.waitingTicks = this.assistantMoveTime + (this.assistantMovePenaltyPerBook * this.currentBookList.size()) - 1;
        this.currentPosition = dest;

        Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | A assistant begins to travel to " + dest);
        this.printEndAction = true;
        this.msgEndAction = "A assistant has arrived to " + dest;
    }

    protected void doWork() {
        if (this.waitingTicks > 0) {
            this.waitingTicks = this.waitingTicks - 1;
            if (this.waitingTicks == 0 && this.printEndAction) {
                this.printEndAction = false;
                Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | " + msgEndAction);
            }
        } else {
            if (this.currentPosition == "delivery") {
                // Delivery behaviour
                int comp = 0;
                for (;this.currentBookList.size() < this.assistantCarryCapacity && this.deliveryArea.getNbCurrentBook() > 0; comp++) {
                    Book test = this.deliveryArea.takeBook();
                    this.currentBookList.add(test);
                }
                if (this.currentBookList.size() > 0) {
                    Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | A assistant has took " + comp + " book(s) from the delivery box.");
                    GPSSetUp();
                    chooseNextDestination(true);
                }
            } else {
                // Section behaviour
                Section currentSection = searchCurrentSection(this.currentPosition);

                if ((currentSection.getNbCurrentBook() + 1) <= currentSection.getNbMaxBook()) {
                    Book currentBook = getBookForSection(this.currentPosition);
                    if (currentBook != null) {
                        currentSection.addBook(currentBook);
                        this.waitingTicks = this.assistantTimeInsertBookIntoSection - 1;

                        String logComplement = "is putting";
                        if (this.waitingTicks > 0) {
                            this.printEndAction = true;
                            this.msgEndAction = "A assistant has finished to put a book in the " + this.currentPosition + " section";
                            logComplement = "begins to put";
                        }
                        Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | A assistant " + logComplement + " a book in the " + this.currentPosition + " section");

                    } else {
                        chooseNextDestination(true);
                    }
                } else {
                    chooseNextDestination(false);
                }
            }
        }

        // To remove
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}

/*
Did you know the story of an optimistic guy?
As he fell from an huge building, he said on each floor "Jusqu'ici, tout va bien" (translation: "so far all is well") while laughing.
*/