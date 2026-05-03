package stmarys.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import stmarys.library.DatabaseManager;
import stmarys.library.model.Book;

public class BookDAOImpl {
    private final DatabaseManager databaseManager;

    public BookDAOImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void save(Book book) throws DaoException {
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT OR REPLACE INTO books (book_id, title, author, category, availability_status) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, book.getBookId());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getCategory());
            ps.setString(5, book.getAvailabilityStatus());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Error saving book: " + ex.getMessage(), ex);
        }
    }

    public Book findById(int id) throws DaoException {
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM books WHERE book_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBook(rs);
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error finding book: " + ex.getMessage(), ex);
        }
        return null;
    }

    public List<Book> findAll() throws DaoException {
        List<Book> books = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM books")) {
            while (rs.next()) {
                books.add(mapBook(rs));
            }
        } catch (SQLException ex) {
            throw new DaoException("Error fetching all books: " + ex.getMessage(), ex);
        }
        return books;
    }

    public void delete(int id) throws DaoException {
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement("DELETE FROM books WHERE book_id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Error deleting book: " + ex.getMessage(), ex);
        }
    }

    public List<Book> searchByTitle(String title) throws DaoException {
        List<Book> books = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM books WHERE title LIKE ?")) {
            ps.setString(1, "%" + title + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapBook(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error searching books: " + ex.getMessage(), ex);
        }
        return books;
    }

    private Book mapBook(ResultSet rs) throws SQLException {
        return new Book(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"), rs.getString("category"),
                rs.getString("availability_status"));
    }
}
