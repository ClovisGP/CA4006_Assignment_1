package Entities;

public class Book {
    private String section;
    private String name;
    
    public Book(String sectionChoice) {
        this.section = sectionChoice;
        this.name = "H2G2";
    }
    public String getName() {
        return this.name;
    }
    public String getSection() {
        return this.section;
    }
}
