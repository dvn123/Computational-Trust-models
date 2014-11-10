package agents;

import sun.management.Agent;
import util.FIREInteraction;
import util.Question;

import java.util.ArrayList;

public abstract class ReplyAgent extends Agent {
    ArrayList<FIREInteraction> interactions;



    abstract float reply(Question q);
}
