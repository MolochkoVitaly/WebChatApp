package bsu.famcs.chat.storage.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import bsu.famcs.chat.model.Message;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class XMLHistoryUtil {
    private static final String XML_LOCATION = "D:\\history.xml";
    private static final String MESSAGES = "messages";
    private static final String MESSAGE = "message";
    private static final String ID = "id";
    private static final String USER_NAME = "userName";
    private static final String MSG_TEXT = "msgText";
    private static final String SEND_DATE = "sendDate";
    /*private static final String CHANGE_DATE = "changeDate";
    private static final String DELETED = "isDeleted";*/

    private XMLHistoryUtil() {
    }

    public static synchronized void createStorage() throws ParserConfigurationException, TransformerException {
        System.out.println(XML_LOCATION);
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element rootElement = document.createElement(MESSAGES);
        document.appendChild(rootElement);

        Transformer transformer = getTransformer();

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(XML_LOCATION));
        transformer.transform(source, result);
    }

    public static synchronized void addMessage(Message message) throws ParserConfigurationException, IOException,
            SAXException, TransformerException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(XML_LOCATION);

        Element rootElement = document.getDocumentElement();

        Element messageElement = document.createElement(MESSAGE);
        rootElement.appendChild(messageElement);
        messageElement.setAttribute(ID, message.getId());

        Element userNameElement = document.createElement(USER_NAME);
        userNameElement.appendChild(document.createTextNode(message.getUserName()));
        messageElement.appendChild(userNameElement);

        Element msgTextElement = document.createElement(MSG_TEXT);
        msgTextElement.appendChild(document.createTextNode(message.getMsgText()));
        messageElement.appendChild(msgTextElement);

        Element sendDateElement = document.createElement(SEND_DATE);
        sendDateElement.appendChild(document.createTextNode(message.getSendDate()));
        messageElement.appendChild(sendDateElement);

        /*Element modifyDateElement = document.createElement(CHANGE_DATE);
        modifyDateElement.appendChild(document.createTextNode(message.getChangeDate()));
        messageElement.appendChild(modifyDateElement);

        Element deletedElement = document.createElement(DELETED);
        deletedElement.appendChild(document.createTextNode(Boolean.toString(message.isDeleted())));
        messageElement.appendChild(deletedElement);*/



        Transformer transformer = getTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(XML_LOCATION);
        transformer.transform(source, result);
    }

    public static synchronized List<Message> getMessages() throws SAXException, IOException, ParserConfigurationException {
        List<Message> messages = new ArrayList<Message>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(XML_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        NodeList taskList = root.getElementsByTagName(MESSAGE);
        for (int i = 0; i < taskList.getLength(); i++) {
            Element messageElement = (Element) taskList.item(i);
            String id = messageElement.getAttribute(ID);
            String userName = messageElement.getElementsByTagName(USER_NAME).item(0).getTextContent().trim();
            String msgText = messageElement.getElementsByTagName(MSG_TEXT).item(0).getTextContent().trim();
            String sendDate = messageElement.getElementsByTagName(SEND_DATE).item(0).getTextContent().trim();
            /*String changeDate = messageElement.getElementsByTagName(CHANGE_DATE).item(0).getTextContent().trim();
            boolean isDeleted = Boolean.valueOf(messageElement.getElementsByTagName(DELETED).item(0).getTextContent().trim());*/
            messages.add(new Message(id, userName, msgText, sendDate/*, changeDate, isDeleted*/));
        }
        return messages;
    }

    private static Transformer getTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }

    public static synchronized boolean isStorageExist() {
        File file = new File(XML_LOCATION);
        return file.exists();
    }
}
