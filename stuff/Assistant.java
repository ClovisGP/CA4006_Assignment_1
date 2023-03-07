package stuff;

import Entities.Book;
import Entities.Section;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

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

    public Assistant(ArrayList<Section> sectionList, Section deliveryArea, int assistantCarryCapacity, int assistantMoveTime, int assistantMovePenaltyPerBook, int assistantTimeInsertBookIntoSection, int assistantBreakTime, int assistantMinTimeBeforeBreak, int assistantMaxTimeBeforeBreak) {
        this.sectionList = sectionList;
        this.deliveryArea = deliveryArea;
        this.assistantCarryCapacity = assistantCarryCapacity;
        this.assistantMoveTime = assistantMoveTime;
        this.assistantMovePenaltyPerBook = assistantMovePenaltyPerBook;
        this.assistantTimeInsertBookIntoSection = assistantTimeInsertBookIntoSection;
        this.assistantBreakTime = assistantBreakTime;
        this.assistantMinTimeBeforeBreak = assistantMinTimeBeforeBreak;
        this.assistantMaxTimeBeforeBreak = assistantMaxTimeBeforeBreak;
        System.out.println("A new assistant is created");
    }

    private Section searchCurrentSection(String targetSectionName) {
        int index = 0;
        for (; this.sectionList.get(index).getName() == targetSectionName; index++);
        return sectionList.get(index);
    }

    private Book searchBookForSection(String targetSectionName) {
        if (this.currentBookList.size() == 0)
            return null;
        for (int index = 0; this.currentBookList.get(index) != null; index++) {
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
        System.out.println("A assistant go to " + dest);
    }

    protected void doWork() {
        if (this.waitingTicks > 0) {
            this.waitingTicks = this.waitingTicks - 1;
            if (this.waitingTicks == 0) {
                System.out.println("end wait");
            }
        } else {
            if (this.currentPosition == "delivery") {
                int comp = 0;
                for (;this.currentBookList.size() < this.assistantCarryCapacity && this.deliveryArea.getNbCurrentBook() > 0; comp++) {
                    Book test = this.deliveryArea.takeBook();
                    this.currentBookList.add(test);
                }
                if (this.currentBookList.size() > 0) {
                    System.out.println("A assistant had took " + comp + " book(s) from delivery.");
                    moveAction(this.currentBookList.get(0).getSection());
                }
            } else {
                Section currentSection = searchCurrentSection(this.currentPosition);// refactoring it
                if ((currentSection.getNbCurrentBook() + 1) <= currentSection.getNbMaxBook()) {//waste of time here
                    Book currentBook = searchBookForSection(this.currentPosition);
                    if (currentBook != null) {
                        currentSection.addBook(currentBook);
                        this.waitingTicks = this.assistantTimeInsertBookIntoSection - 1;
                        System.out.println("A assistant is putting a book in the " + this.currentPosition + " section");
                    } else {
                        System.out.println(this.currentBookList.size());
                        if (this.currentBookList.size() == 0) {
                            moveAction("delivery");
                        } else {
                            moveAction(this.currentBookList.get(0).getSection());
                        }
                    }
                } else {
                    System.out.println(this.currentBookList.size());
                    if (this.currentBookList.size() == 0) {
                        moveAction("delivery");
                    } else {
                        moveAction(this.currentBookList.get(0).getSection());
                    }
                }
            }
        }

        int timeToWaste = (int)(Math.random() * 900 + 100);
        try {
            TimeUnit.MILLISECONDS.sleep(timeToWaste);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}
