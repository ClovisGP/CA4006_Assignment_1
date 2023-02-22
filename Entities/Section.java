package Entities;

import java.util.ArrayList;
import Entities.Book;

/**
 * A section is the class that represents a bookshelf..
 */
public class Section {
    private String name;
    private int nbMaxBook;
    private ArrayList<Book> booksList = new ArrayList<Book>();

    public Section(String name, int nbMaxBook) {
        this.name = name;
        this.nbMaxBook = nbMaxBook;
        this.booksList.add(new Book(name));
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
     * Get the current number of books in the section.
     * @return A Integer which cantains the current number of books in the section.
     */
    public int getNbCurrentBook() {
        return this.booksList.size();
    }
    /**
     * Add a book to the sections.
     * @param newBook - The book which is had to be added.
     * @return 0 => the addition is a success. 1 => 1 a failure.
     */
    public int addBook(Book newBook) {
        if (booksList.size() + 1 <= nbMaxBook || nbMaxBook == 0) {
            this.booksList.add(newBook);
            return 0;
        }
        return 1;
    }
    /**
     * Return a book and remove it from the section.
     * @return The first book of the section. Or null if the section is empty.
     */
    public Book takeBook() {
        if (booksList.size() <= 0) {
            Book bookTarget = this.booksList.get(0);
            this.booksList.remove(0);
            return bookTarget;
        }
        return null;
    }
}
