package util;

import agents.FIREAgent;
import jade.core.AID;

import java.io.Serializable;
import java.util.Date;

public class FIREInteraction implements Serializable {
    public FIREAgent fireAgent;
    public AID replyAgent;
    public int id;
    public String topic;
    public float rating;
    public Date date;

    public FIREInteraction(FIREAgent fireAgent, AID replyAgent, int id, String topic, float rating) {
        this.fireAgent = fireAgent;
        this.replyAgent = replyAgent;
        this.id = id;
        this.topic = topic;
        this.rating = rating;
        this.date = new Date();
    }
}
