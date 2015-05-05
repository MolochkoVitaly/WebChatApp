package bsu.famcs.chat.util;

import bsu.famcs.chat.model.Message;
import bsu.famcs.chat.model.MessageStorage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class MessageUtil {

    public static final String TOKEN = "token";
    public static final String MESSAGES = "messages";
    private static final String TN = "TN";
    private static final String EN = "EN";
    private static final String USER_NAME = "userName";
    private static final String MSG_TEXT = "msgText";
    //private static final String NOT_CHANGE = "not changed";
    private static final String ID = "id";
    private static int CURRENT_ID = 0;

    private MessageUtil() {
    }

    public static String getToken(int index) {
        Integer number = index * 8 + 11;
        return TN + number + EN;
    }

    public static int getIndex(String token) {
        return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
    }

    private static String generateId() {
        return String.valueOf(MessageStorage.getSize());
    }

    private static String generateCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss");
        //dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Minsk"));
        return dateFormat.format(new Date());
    }

    public static JSONObject stringToJson(String data) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(data.trim());
    }

    public static Message jsonToMessage(JSONObject json) {
        String userName = (String)json.get(USER_NAME);
        String msgText = (String)json.get(MSG_TEXT);
        //String id = (String)json.get(ID);
        if (userName != null && msgText != null) {
            Message msg =  new Message(generateId(), userName, msgText, generateCurrentDate());
            return msg;
        }
        return null;
    }
}
