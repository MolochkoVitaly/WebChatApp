package bsu.famcs.chat.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MessageStorage {
    private static final List<Message> HISTORY = Collections.synchronizedList(new ArrayList<Message>());

    private MessageStorage() {
    }

    public static void addMessagePost(Message message) {
        HISTORY.add(message);
    }

    public static void addMessageDelete(Message message) {
        for (Message aHISTORY : HISTORY) {
            if (Integer.parseInt(aHISTORY.getId()) == Integer.parseInt(message.getId())) {
                aHISTORY.setMsgText(message.getMsgText());
                aHISTORY.isDelete();
                aHISTORY.setChangeDate();
            }
        }
    }

    public static void addMessagePut(Message message) {
        for (Message aHISTORY : HISTORY) {
            if (Integer.parseInt(aHISTORY.getId()) == Integer.parseInt(message.getId())) {
                aHISTORY.setMsgText(message.getMsgText());
                aHISTORY.setChangeDate();
            }
        }
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

    public static int countOfMessages(int index){
        return HISTORY.subList(index, HISTORY.size()).size();
    }

    public static String getStringView() {
        StringBuilder sb = new StringBuilder();
        for (Message message : HISTORY) {
            sb.append(message.getUserMessage());
            sb.append('\n');
        }
        return sb.toString().trim();
    }
}
