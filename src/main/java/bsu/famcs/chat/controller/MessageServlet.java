package bsu.famcs.chat.controller;

import bsu.famcs.chat.model.Message;
import bsu.famcs.chat.model.MessageStorage;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import static bsu.famcs.chat.util.MessageUtil.*;
import static bsu.famcs.chat.util.ServletUtil.APPLICATION_JSON;
import static bsu.famcs.chat.util.ServletUtil.UTF_8;
import static bsu.famcs.chat.util.ServletUtil.getMessageBody;
import bsu.famcs.chat.dao.MessageDaoImpl;

@WebServlet("/chat")
public class MessageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(MessageServlet.class.getName());
    MessageDaoImpl messageDao = new MessageDaoImpl();

    @Override
    public void init() throws ServletException {
        try {
            loadHistory();
        } catch (TransformerException | ParserConfigurationException | IOException | SAXException | SQLException e) {
            logger.error(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter(TOKEN);
        logger.info("Get request");
        if (token != null && !"".equals(token)) {
            int index = getIndex(token);
            if(MessageStorage.countOfMessages(index)==0){
                logger.info("GET request: response status: 304 Not Modified");
                response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            }else {
                     String messages = serverResponse(index);
                     response.setContentType(UTF_8);
                     response.setContentType(APPLICATION_JSON);
                     PrintWriter out = response.getWriter();
                     out.print(messages);
                     out.flush();
                  }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "token parameter is absent");
            logger.error("Token parameter is absent");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Post request");
        String data = getMessageBody(request);
        logger.info("Request data : " + data);
        try {
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            logger.info(message.getUserMessage());
            MessageStorage.addMessagePost(message);
            messageDao.add(message);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException  e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message");
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Put request");
        String data = getMessageBody(request);
        logger.info("Request data : " + data);
        Message message;
        try {
            JSONObject jsonObject = stringToJson(data);
            message = jsonToCurrentMessage(jsonObject);
            message.setChangeDate();
            MessageStorage.addMessagePut(message);
            messageDao.update(message);
        } catch (ParseException | NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message");
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Delete request");
        String data = getMessageBody(request);
        logger.info("Request data : " + data);
        Message message;
        try {
            JSONObject json = stringToJson(data);
            message = jsonToCurrentMessage(json);
            message.isDelete();
            message.setChangeDate();
            MessageStorage.addMessageDelete(message);
            messageDao.update(message);
        } catch (ParseException | NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message");
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    @SuppressWarnings("unchecked")
    private String serverResponse(int index) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MESSAGES, MessageStorage.getSubHistory(index));
        jsonObject.put(TOKEN, getToken(MessageStorage.getSize()));
        return jsonObject.toJSONString();
    }

    private void loadHistory() throws TransformerException, ParserConfigurationException, IOException,
            SAXException, SQLException {
        MessageStorage.addAll(messageDao.selectAll());
        logger.info('\n' + MessageStorage.getStringView());
        logger.info(MessageStorage.getSubHistory(0));
    }
}