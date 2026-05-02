package stmarys.library.dao;

import java.util.List;
import stmarys.library.model.BorrowRecord;

public interface BorrowRecordDAO {
    void save(BorrowRecord record) throws DaoException;
    BorrowRecord findById(int id) throws DaoException;
    List<BorrowRecord> findAll() throws DaoException;
}
