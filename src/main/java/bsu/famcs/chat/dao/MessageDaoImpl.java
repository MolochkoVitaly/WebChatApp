package bsu.famcs.chat.dao;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import bsu.famcs.chat.db.ConnectionManager;
import bsu.famcs.chat.model.Message;

public  class MessageDaoImpl implements MessageDao {
    private static Logger logger = Logger.getLogger(MessageDaoImpl.class.getName());

    @Override
    public void add(Message message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO messages (id, userName, msgText, sendDate, changeDate, isDeleted) VALUES (?,?, ?, ?, ?, ?)");
            preparedStatement.setString(1, message.getId());
            preparedStatement.setString(2, message.getUserName());
            preparedStatement.setString(3, message.getMsgText());
            preparedStatement.setString(4, message.getSendDate());
            preparedStatement.setString(5, message.getChangeDate());
            preparedStatement.setBoolean(6, message.isDeleted());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }

    @Override
    public void update(Message message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement("Update messages SET msgText = ?, isDeleted = ?, changeDate = ? WHERE id = ?");
            preparedStatement.setString(1, message.getMsgText());
            preparedStatement.setBoolean(2, message.isDeleted());
            preparedStatement.setString(3, message.getChangeDate());
            preparedStatement.setString(4, message.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }

    @Override
    public Message selectById(Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Message> selectAll() {
        List<Message> messages = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSetMessages = null;

        try {
            connection = ConnectionManager.getConnection();
            statement = connection.createStatement();
            resultSetMessages = statement.executeQuery("SELECT * FROM messages");

            while (resultSetMessages.next()) {
                String id = resultSetMessages.getString("id");
                String msgText= resultSetMessages.getString("msgText");
                String userName = resultSetMessages.getString("userName");
                String sendDate = resultSetMessages.getNString("sendDate");
                String changeDate = resultSetMessages.getString("changeDate");
                Boolean isDeleted = resultSetMessages.getBoolean("isDeleted");
                messages.add(new Message(id, userName, msgText, sendDate, changeDate, isDeleted));
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (resultSetMessages != null) {
                try {
                    resultSetMessages.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
        return messages;
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException();
    }

}
