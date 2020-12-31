package com.tvcs.mainzermobilitaet;

/**
 * Created by Tomahawk on 02.01.2018.
 */

public class PublicTransportLiveMessages {
    private String messageuuid;
    private int messagepriority;
    private String messagetext;
    private long starttime;
    private long expiretime;

    public PublicTransportLiveMessages(String messageuuid, int messagepriority, String messagetext, long starttime, long expiretime) {
        this.messageuuid = messageuuid;
        this.messagepriority = messagepriority;
        this.messagetext = messagetext;
        this.starttime = starttime;
        this.expiretime = expiretime;
    }

    public String getMessageuuid() {
        return messageuuid;
    }

    public int getMessagepriority() {
        return messagepriority;
    }

    public String getMessagetext() {
        return messagetext;
    }

    public long getStarttime() {
        return starttime;
    }

    public long getExpiretime() {
        return expiretime;
    }

    @Override
    public String toString() {
        return "Message: " + messagetext + "\n";
    }
}
