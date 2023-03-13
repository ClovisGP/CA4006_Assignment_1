package Objects;

import java.util.ArrayList;

import Objects.Book;
import Tools.StatsManager;

/**
 * A section is the class that represents a bookshelf.
 */
public class Section {
    private String name;
    private int nbMaxBook;
    private ArrayList<Book> booksList = new ArrayList<Book>();
    private int nbWaitingCustomer = 0;
    private StatsManager statsManager;
    public enum returnType {
        BOOKADDED,
        BOOKTOCUSTOMER,
        FULLSECTION
    }

    public Section(String name, int nbMaxBook, int startingNumberOfBooks, StatsManager statsManager) {
        this.name = name;
        this.nbMaxBook = nbMaxBook;
        for (int i = 0; i < startingNumberOfBooks; i++) {
            this.booksList.add(new Book(name + "-starting_book-" + Integer.toString(i)));
        }
        this.statsManager = statsManager;
    }
    
    /**
     * Get the name of the section.
     * @return A String which cantains the name of the section
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the maximum number of books that can contains the section.
     * @return A Integer which cantains the maximum number of books that can contains the section.
     */
    public int getNbMaxBook() {
        return this.nbMaxBook;
    }

    /**
     * Get the name of the section.
     * @return A String which cantains the name of the section
     */
    public Integer getNbWaitingCustomer() {
        return this.nbWaitingCustomer;
    }

    /**
     * Get the current number of books in the section.
     * @return A Integer which cantains the current number of books in the section.
     */
    public synchronized int getNbCurrentBook() {
        return this.booksList.size();
    }

    /**
     * Add a book to the sections.
     * @param newBook - The book which is had to be added.
     * @return BOOKADDED => the addition is a success. BOOKTOCUSTOMER => a failure. FULLSECTION => if a customer is waiting and takes directly the new book.
     */
    public synchronized returnType addBook(Book newBook) {
        if (nbWaitingCustomer > 0) {
            this.nbWaitingCustomer--;
            if (this.name != "delivery") statsManager.statsAddBookBought();
            return returnType.BOOKTOCUSTOMER;
        }
        if (booksList.size() + 1 <= nbMaxBook || nbMaxBook == 0) {
            this.booksList.add(newBook);
            return returnType.BOOKADDED;
        }
        return returnType.FULLSECTION;
    }
    
    /**
     *If there are a available book, it is removed from the section and returned. Unless, increment the number of customer on this section and returns null.
     */
    public synchronized Book takeBook() {
        if (this.booksList.size() > 0) {
            if (this.name != "delivery") statsManager.statsAddBookBought();
            return this.booksList.remove(0);
        } else {
            this.nbWaitingCustomer++;
            return null;
        }
    }
}
