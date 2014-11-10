package util;

import agents.FIREAgent;
import agents.ReplyAgent;

import java.io.Serializable;

public class FIREInteraction implements Serializable {
    public FIREAgent fireAgent;
    public ReplyAgent replyAgent;
    public int id;
    public String topic;
    public float rating;

    FIREInteraction(FIREAgent fireAgent, ReplyAgent replyAgent, int id, String topic, float rating) {
        this.fireAgent = fireAgent;
        this.replyAgent = replyAgent;
        this.id = id;
        this.topic = topic;
        this.rating = rating;
    }
}
