package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import util.FIREDb;
import util.FIREInteraction;
import util.Question;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.Property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class FIREAgent extends BaseAnswerAgent {
    MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );

    protected void setup() {
        super.setup();
        System.out.println("Agent "+getLocalName()+" waiting for requests...");
    }

    private float getDateWeight(Date t1) {
        Date now = new Date();
        float diff = now.getTime() - t1.getTime();
    }

    private float getScore(ArrayList<FIREInteraction> db) {

    }

    private AID getBestWiseAgent(Question question) {
        for (AID w: wiseAgents) {
            getScore(FIREDb.find(w, String.valueOf(question.getOperator()), this));
        }
       // return wiseAgents.get(index);
    }
}
