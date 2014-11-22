package agents;

import jade.core.Agent;
import util.FIREInteraction;
import util.Question;

import java.util.ArrayList;

public abstract class ReplyAgent extends Agent {
    ArrayList<FIREInteraction> bestInteractions;

    abstract float reply(Question q);
}
