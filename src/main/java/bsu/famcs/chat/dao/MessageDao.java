package bsu.famcs.chat.dao;

import java.sql.SQLException;
import java.util.List;
import bsu.famcs.chat.model.Message;

public interface MessageDao {
    void add(Message message) throws SQLException;

    void update(Message message) throws SQLException;

    void delete(int id);

    Message selectById(Message message);

    List<Message> selectAll() throws SQLException;
}
