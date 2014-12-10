package util;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class FIREInteraction implements Serializable {
    public String fireAgent;
    public String replyAgent;
    public int id;
    public String topic;
    public float rating;
    public Date date;

    public FIREInteraction(String fireAgent, String replyAgent, int id, String topic, float rating) {
        this.fireAgent = fireAgent;
        this.replyAgent = replyAgent;
        this.id = id;
        this.topic = topic;
        this.rating = rating;
        this.date = new Date();
    }
}
