package agents;

import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Created by diogo on 26-10-2014.
 */
public class FIREAgent extends Agent {
    protected void setup() {
        try {
            // Register to messages about topic "JADE"
            TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
            final AID addition = topicHelper.createTopic("0");
            final AID subtraction = topicHelper.createTopic("1");
            final AID multiplication = topicHelper.createTopic("2");
            final AID division = topicHelper.createTopic("3");
            topicHelper.register(addition);

            // Add a behaviour collecting messages about topic "JADE"
            addBehaviour(new CyclicBehaviour(this) {
                public void action() {
                    ACLMessage msg = myAgent.receive(MessageTemplate.MatchTopic(addition));
                    if (msg != null) {
                        System.out.println("Agent "+myAgent.getLocalName()+": Message about topic "+addition.getLocalName()+" received. Content is "+msg.getContent());
                    }
                    else {
                        block();
                    }
                }
            } );
        }
        catch (Exception e) {
            System.err.println("Agent "+getLocalName()+": ERROR registering to topic \"JADE\"");
            e.printStackTrace();
        }
    }

}
