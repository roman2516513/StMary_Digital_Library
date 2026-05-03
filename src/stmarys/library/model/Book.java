package stmarys.library.model;

public class Book extends LibraryEntity {
    private String title;
    private String author;
    private String category;
    private String availabilityStatus;

    public Book(int bookId, String title, String author, String category, String availabilityStatus) {
        super(bookId);
        this.title = title;
        this.author = author;
        this.category = category;
        this.availabilityStatus = availabilityStatus;
    }

    public int getBookId() {
        return getId();
    }

    public void setBookId(int bookId) {
        setId(bookId);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
}
