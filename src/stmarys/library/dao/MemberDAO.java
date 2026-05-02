package stmarys.library.dao;

import java.util.List;
import stmarys.library.model.Member;

public interface MemberDAO {
    void save(Member member) throws DaoException;
    Member findById(int id) throws DaoException;
    List<Member> findAll() throws DaoException;
}
