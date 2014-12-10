package util;

import java.util.ArrayList;

public class FIREDb {
    private static ArrayList<FIREInteraction> interactions = new ArrayList<FIREInteraction>();

    public static ArrayList<FIREInteraction> find(String ra, String topic) {
        ArrayList<FIREInteraction> tmp = new ArrayList<FIREInteraction>();

        for(FIREInteraction f: interactions) {
            if(f.replyAgent.equals(ra)) {
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

    public static ArrayList<FIREInteraction> find(String ra, String topic, String aa) {
        ArrayList<FIREInteraction> tmp = new ArrayList<FIREInteraction>();
        //System.out.println("Searching length - " + interactions.size());

        for(FIREInteraction f: interactions) {
            //System.out.println("Testing 1 - " + f.replyAgent + " == " + ra + "\n 2 - " + f.fireAgent + " == " + aa + "\n 3 - " + f.topic + " == " + topic);
            if(f.replyAgent.equals(ra) && f.fireAgent.equals(aa) && f.topic.equals(topic)) {
                //System.out.println("FOUND");
                tmp.add(f);
            }
        }

        return tmp;
    }

    public static void addRating(int id, float rating) {
        for(FIREInteraction f: interactions) {
            if(f.id == id)
                f.rating = rating;
        }
    }

    public static void addInteraction(FIREInteraction f) {
        interactions.add(f);
        //System.out.println("Creating - " + f.replyAgent + "\n 2 - " + f.fireAgent + "\n 3 - " + f.topic);
    }

}
