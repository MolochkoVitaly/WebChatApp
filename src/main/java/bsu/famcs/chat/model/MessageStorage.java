package bsu.famcs.chat.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MessageStorage {
    private static final List<Message> HISTORY = Collections.synchronizedList(new ArrayList<Message>());

    private MessageStorage() {
    }

    public static List<Message> getStorage() {
        return HISTORY;
    }

    public static void addMessage(Message message) {
        HISTORY.add(message);
    }

    public static void addAll(List<Message> messages) {
        HISTORY.addAll(messages);
    }

    public static int getSize() {
        return HISTORY.size();
    }

    public static List<Message> getSubHistory(int index) {
        return HISTORY.subList(index, HISTORY.size());
    }

    public static Message getMessageById(String id) {
        for (Message message : HISTORY) {
            if (id.equals(message.getId())) {
                return message;
            }
        }
        return null;
    }

    public static String getStringView() {
        StringBuffer sb = new StringBuffer();
        for (Message message : HISTORY) {
            sb.append(message.getUserMessage());
            sb.append('\n');
        }
        return sb.toString().trim();
    }
}
