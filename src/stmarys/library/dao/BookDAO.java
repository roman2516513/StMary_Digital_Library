package stmarys.library.dao;

import java.util.List;
import stmarys.library.model.Book;

public interface BookDAO {
    void save(Book book) throws DaoException;
    Book findById(int id) throws DaoException;
    List<Book> findAll() throws DaoException;
}
