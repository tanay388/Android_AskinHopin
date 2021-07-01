package com.askinhopin.app;

public class ConversationClass {

    Boolean read;
    long time;
    String senderuid;
    String recieveruid;
    String lastmessage;

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }


    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSenderuid() {
        return senderuid;
    }

    public void setSenderuid(String senderuid) {
        this.senderuid = senderuid;
    }

    public String getRecieveruid() {
        return recieveruid;
    }

    public void setRecieveruid(String recieveruid) {
        this.recieveruid = recieveruid;
    }


}
