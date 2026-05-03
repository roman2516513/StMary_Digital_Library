package stmarys.library.service;

import java.time.LocalDate;
import java.util.List;
import stmarys.library.dao.BookDAOImpl;
import stmarys.library.dao.BorrowRecordDAOImpl;
import stmarys.library.dao.DaoException;
import stmarys.library.dao.MemberDAOImpl;
import stmarys.library.model.Book;
import stmarys.library.model.BorrowRecord;
import stmarys.library.model.Member;
import stmarys.library.util.ValidationException;
import stmarys.library.util.ValidationUtil;

public class LibraryService {
    private final BookDAOImpl bookDAO;
    private final MemberDAOImpl memberDAO;
    private final BorrowRecordDAOImpl borrowRecordDAO;

    public LibraryService(BookDAOImpl bookDAO, MemberDAOImpl memberDAO, BorrowRecordDAOImpl borrowRecordDAO) {
        this.bookDAO = bookDAO;
        this.memberDAO = memberDAO;
        this.borrowRecordDAO = borrowRecordDAO;
    }

    // Book operations
    public void addBook(String idStr, String title, String author, String category, String status)
            throws ValidationException, DaoException {
        int id = ValidationUtil.positiveInt(idStr, "Book ID");
        if (title.trim().isEmpty())
            throw new ValidationException("Title cannot be empty");
        if (author.trim().isEmpty())
            throw new ValidationException("Author cannot be empty");
        if (category.trim().isEmpty())
            throw new ValidationException("Category cannot be empty");

        Book book = new Book(id, title, author, category, status);
        bookDAO.save(book);
    }

    public List<Book> getAllBooks() throws DaoException {
        return bookDAO.findAll();
    }

    public Book getBookById(int id) throws DaoException {
        return bookDAO.findById(id);
    }

    public void updateBook(String idStr, String title, String author, String category, String status)
            throws ValidationException, DaoException {
        int id = ValidationUtil.positiveInt(idStr, "Book ID");
        Book book = bookDAO.findById(id);
        if (book == null)
            throw new ValidationException("Book not found");

        if (!title.trim().isEmpty())
            book.setTitle(title);
        if (!author.trim().isEmpty())
            book.setAuthor(author);
        if (!category.trim().isEmpty())
            book.setCategory(category);
        if (!status.trim().isEmpty())
            book.setAvailabilityStatus(status);

        bookDAO.save(book);
    }

    public void deleteBook(int id) throws DaoException {
        bookDAO.delete(id);
    }

    public List<Book> searchBooks(String query) throws DaoException {
        return bookDAO.searchByTitle(query);
    }

    // Member operations
    public void addMember(String idStr, String name, String email, String type) throws ValidationException, DaoException {
        int id = ValidationUtil.positiveInt(idStr, "Member ID");
        if (name.trim().isEmpty())
            throw new ValidationException("Name cannot be empty");
        ValidationUtil.email(email);

        Member member = new Member(id, name, email, type);
        memberDAO.save(member);
    }

    public List<Member> getAllMembers() throws DaoException {
        return memberDAO.findAll();
    }

    public Member getMemberById(int id) throws DaoException {
        return memberDAO.findById(id);
    }

    public void updateMember(String idStr, String name, String email, String type)
            throws ValidationException, DaoException {
        int id = ValidationUtil.positiveInt(idStr, "Member ID");
        Member member = memberDAO.findById(id);
        if (member == null)
            throw new ValidationException("Member not found");

        if (!name.trim().isEmpty())
            member.setMemberName(name);
        if (!email.trim().isEmpty()) {
            ValidationUtil.email(email);
            member.setEmail(email);
        }
        if (!type.trim().isEmpty())
            member.setMembershipType(type);

        memberDAO.save(member);
    }

    public void deleteMember(int id) throws DaoException {
        memberDAO.delete(id);
    }

    public List<Member> searchMembers(String query) throws DaoException {
        return memberDAO.searchByName(query);
    }

    // Borrow record operations
    public void addBorrowRecord(String bookIdStr, String memberIdStr, String borrowDateStr, String dueDateStr)
            throws ValidationException, DaoException {
        int bookId = ValidationUtil.positiveInt(bookIdStr, "Book ID");
        int memberId = ValidationUtil.positiveInt(memberIdStr, "Member ID");

        Book book = bookDAO.findById(bookId);
        if (book == null)
            throw new ValidationException("Book not found");

        Member member = memberDAO.findById(memberId);
        if (member == null)
            throw new ValidationException("Member not found");

        LocalDate borrowDate = ValidationUtil.date(borrowDateStr, "Borrow Date");
        LocalDate dueDate = ValidationUtil.date(dueDateStr, "Due Date");

        if (dueDate.isBefore(borrowDate))
            throw new ValidationException("Due date cannot be before borrow date");

        BorrowRecord record = new BorrowRecord(0, bookId, memberId, borrowDate, dueDate, "Borrowed");
        borrowRecordDAO.save(record);

        book.setAvailabilityStatus("Borrowed");
        bookDAO.save(book);
    }

    public List<BorrowRecord> getAllBorrowRecords() throws DaoException {
        return borrowRecordDAO.findAll();
    }

    public void updateBorrowRecord(String recordIdStr, String status) throws ValidationException, DaoException {
        int recordId = ValidationUtil.positiveInt(recordIdStr, "Record ID");
        BorrowRecord record = borrowRecordDAO.findById(recordId);
        if (record == null)
            throw new ValidationException("Borrow record not found");

        record.setReturnStatus(status);
        borrowRecordDAO.save(record);

        if ("Returned".equalsIgnoreCase(status)) {
            Book book = bookDAO.findById(record.getBookId());
            if (book != null) {
                book.setAvailabilityStatus("Available");
                bookDAO.save(book);
            }
        }
    }

    public void deleteBorrowRecord(int recordId) throws DaoException {
        borrowRecordDAO.delete(recordId);
    }
}
