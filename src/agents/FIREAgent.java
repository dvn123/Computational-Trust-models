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
import util.Question;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.Property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class FIREAgent extends Agent {
    MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );

    ArrayList<ReplyAgent> agentsKnown;

    private class AnsweringBehaviour extends CyclicBehaviour{

        public AnsweringBehaviour(Agent x) {
            super(x);
            agentsKnown = new ArrayList<ReplyAgent>();
        }

        @Override
        public void action() {
            ACLMessage request = myAgent.receive(template);

            if (request != null) {
                System.out.println("Agent "+getLocalName()+": REQUEST received from "+request.getSender().getName() + " type: " + request.getPerformative()); //+". Action is "+request.getContent());

                Question x = null;
                try {
                    x = (Question) request.getContentObject();
                    x.printQuestion();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }

                // Fill the REQUEST message
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

                msg.addReceiver(new AID((String) "sabio" , AID.ISLOCALNAME));

                msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
                // We want to receive a reply in 10 secs
                msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
                msg.setOntology("oioi");

                try {
                    msg.setContentObject(x);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                send(msg);

                ACLMessage hey = blockingReceive(MessageTemplate.MatchOntology("oioi"),50000);

                // We agree to perform the action. Note that in the FIPA-Request
                // protocol the AGREE message is optional. Return null if you
                // don't want to send it.
                System.out.println("Agent "+getLocalName()+": Agree");
                ACLMessage agree = request.createReply();

                double y;
                y = Double.parseDouble(hey.getContent());
                agree.setContent(Double.toString(y));
                System.out.println("FIRE: Received from sabio: " + y);

                agree.setPerformative(ACLMessage.INFORM);

                send(agree);

                ACLMessage ok = blockingReceive(MessageTemplate.MatchOntology("solution"),50000);

                if (ok.getPerformative() == ACLMessage.CONFIRM)
                    System.out.println("FIRE SAYS: Answer is correct");
                else if (ok.getPerformative() == ACLMessage.DISCONFIRM)
                    System.out.println("FIRE SAYS: Answer is incorrect");
                else
                    System.out.println("FIRE SAYS: estás tolo");
            }
            else {
                block();
            }
        }
    }

    protected void setup() {
        System.out.println("Agent "+getLocalName()+" waiting for requests...");




        addBehaviour(new AnsweringBehaviour(this));
    }

    private boolean checkAction() {
        // Simulate a check by generating a random number
        return true;//(Math.random() > 0.2);
    }

    private boolean performAction() {
        // Simulate action execution by generating a random number
        return (Math.random() > 0.2);
    }

}
