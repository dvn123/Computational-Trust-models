package agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;

import java.util.Random;

import util.Constants;
import util.Question;

public class ReplyAgent extends Agent {
    public static final int MAX = 100;
    public static final int MIN = 0;

    Float[] knowledge;


    float reply(Question q) {
        Random rand = new Random();
        float factor = -1;
        factor = knowledge[q.getOperator()];
        return q.getResult()*factor + (1-factor)*(rand.nextInt((MAX - MIN) + 1) + MIN);
    }

    protected void setup() {
        Random rand = new Random();

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            knowledge = new Float[] {Float.valueOf((String) args[0]), Float.valueOf((String) args[1]), Float.valueOf((String) args[2]), Float.valueOf((String) args[3])};
        } else {
            knowledge = new Float[] {rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), rand.nextFloat()};
        }


        try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(Constants.SERVICE_DESCRIPTION_ONTOLOGY_PLAYER);
            sd.setType(Constants.SERVICE_DESCRIPTION_ONTOLOGY_INFORMATION_SOURCE_FIRE);
            sd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
            dfd.addServices(sd);
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        System.out.println("Agent "+getLocalName()+" waiting for requests...");

        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );

        addBehaviour(new AchieveREResponder(this, template) {
            protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
                System.out.println("Agent "+getLocalName()+": REQUEST received from "+request.getSender().getName()); //+". Action is "+request.getContent());

                Question x = null;
                try {
                    x = (Question) request.getContentObject();
                    x.printQuestion();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }

                if (checkAction()) {
                    // We agree to perform the action. Note that in the FIPA-Request
                    // protocol the AGREE message is optional. Return null if you
                    // don't want to send it.
                    System.out.println("Agent "+getLocalName()+": Agree");
                    ACLMessage agree = request.createReply();
                    if(x != null) {
                        System.out.println("Sabio result: " + Double.toString(x.getResult()));
                        agree.setContent(Double.toString(reply(x)));
                    }
                    agree.setPerformative(ACLMessage.INFORM);

                    return agree;
                }
                else {
                    // We refuse to perform the action
                    System.out.println("Agent "+getLocalName()+": Refuse");
                    throw new RefuseException("check-failed");
                }
            }


            protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
                if (performAction()) {
                    System.out.println("Agent "+getLocalName()+": Action successfully performed");
                    ACLMessage inform = request.createReply();
                    inform.setPerformative(ACLMessage.INFORM);
                    return inform;
                }
                else {
                    System.out.println("Agent "+getLocalName()+": Action failed");
                    throw new FailureException("unexpected-error");
                }
            }
        } );
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
