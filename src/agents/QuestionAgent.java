package agents;

import util.Question;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

public class QuestionAgent extends Agent {

    protected void setup() {
        try {
            // Periodically send messages about topic "JADE"

            TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
            final AID addition = topicHelper.createTopic("0");
            final AID subtraction = topicHelper.createTopic("1");
            final AID multiplication = topicHelper.createTopic("2");
            final AID division = topicHelper.createTopic("3");
            addBehaviour(new TickerBehaviour(this, 10000) {
                public void onTick() {
                    Question q = Question.generateQuestion();
                    //System.out.println("Agent "+myAgent.getLocalName()+": Sending message about topic "+topic.getLocalName());
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    switch(q.operator) {
                        case 0: msg.addReceiver(addition);
                                break;
                        case 1: msg.addReceiver(subtraction);
                                break;
                        case 2: msg.addReceiver(multiplication);
                                break;
                        case 3: msg.addReceiver(division);
                                break;
                    }
                    try {
                        msg.setContentObject(q);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myAgent.send(msg);
                }
            } );
        }
        catch (Exception e) {
            System.err.println("Agent "+getLocalName()+": ERROR creating topic \"JADE\"");
            e.printStackTrace();
        }
    }
}