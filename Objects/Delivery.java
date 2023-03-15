package Objects;

import java.util.ArrayList;
import Tools.StatsManager;

/**
 * A section is the class that represents a bookshelf.
 */
public class Delivery {
    private ArrayList<Book> booksList = new ArrayList<Book>();
    private StatsManager statsManager;
    private int sizeOfDelivery;
    private ArrayList<String> sectionNames;

    public Delivery(int sizeOfDelivery, ArrayList<String> sectionNames) {
        this.sizeOfDelivery = sizeOfDelivery;
        this.sectionNames = sectionNames;
        this.statsManager = StatsManager.getInstance();
    }

    public void doADelivery() {
        for (int i = 0; i < sizeOfDelivery; i++) {
            addABook();
        }
    }

    private void addABook() {
        for (int comp = 0; comp < this.sizeOfDelivery; comp++) {
            int random = (int)(Math.random() * this.sectionNames.size());
            if (random == this.sectionNames.size()) random--; // rare case if you happen to receive 1.0 with Math.random
            booksList.add(new Book(sectionNames.get(random)));
        }
    }

    /**
     * Get the current number of books in the section.
     * @return A Integer which cantains the current number of books in the section.
     */
    public int getNbCurrentBook() {
        return this.booksList.size();
    }
    
    /**
     *If there are a available book, it is removed from the section and returned. Unless, increment the number of customer on this section and returns null.
     */
    public synchronized Book takeBook() {
        if (this.booksList.size() > 0) {
            return this.booksList.remove(0);
        } else {
            return null;
        }
    }
}
