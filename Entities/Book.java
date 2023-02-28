package Entities;

/**
 * The class which represents the book
 */
public class Book {
    private String section;
    
    public Book(String sectionChoice) {
        this.section = sectionChoice;
    }

    /**
     * Return the target section.
     * @Return the name of the section name
     */
    public String getSection() {
        return this.section;
    }
}
