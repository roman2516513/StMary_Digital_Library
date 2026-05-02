package stmarys.library.model;

import java.time.LocalDateTime;

public class BorrowRecord implements LibraryEntity {
    private int id;
    private int memberId;
    private int bookId;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public LocalDateTime getBorrowedAt() { return borrowedAt; }
    public void setBorrowedAt(LocalDateTime borrowedAt) { this.borrowedAt = borrowedAt; }
    public LocalDateTime getReturnedAt() { return returnedAt; }
    public void setReturnedAt(LocalDateTime returnedAt) { this.returnedAt = returnedAt; }
}
