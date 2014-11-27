package util;

import agents.BaseAnswerAgent;
import agents.FIREAgent;
import jade.core.AID;

import java.util.ArrayList;

public class FIREDb {
    private static ArrayList<FIREInteraction> interactions;

    public static ArrayList<FIREInteraction> find(AID ra, String topic) {
        ArrayList<FIREInteraction> tmp = new ArrayList<FIREInteraction>();

        for(FIREInteraction f: interactions) {
            if(f.replyAgent == ra) {
                if(topic != null) {
                    if(f.topic.equals(topic))
                        tmp.add(f);
                } else {
                    tmp.add(f);
                }
            }
        }
        return tmp;
    }

    public static ArrayList<FIREInteraction> find(AID ra, String topic, FIREAgent aa) {
        ArrayList<FIREInteraction> tmp = new ArrayList<FIREInteraction>();

        for(FIREInteraction f: interactions) {
            if(f.replyAgent == ra && f.fireAgent == aa && f.topic.equals(topic)) {
                tmp.add(f);
            }
        }

        return tmp;
    }

    public static void addInteraction(FIREInteraction f) {
        interactions.add(f);
    }

}
