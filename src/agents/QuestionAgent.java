/*****************************************************************
 JADE - Java Agent DEvelopment Framework is a framework to develop
 multi-agent systems in compliance with the FIPA specifications.
 Copyright (C) 2000 CSELT S.p.A.

 GNU Lesser General Public License

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation,
 version 2.1 of the License.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 Boston, MA  02111-1307, USA.
 *****************************************************************/

package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import util.Question;

import java.io.IOException;
import java.util.HashMap;

public class QuestionAgent extends Agent {
    // The title of the book to buy
    private Question q;
    // The list of known seller agents
    private AID[] answerAgents;
    private HashMap<AID, Boolean> answersReceived;

    // Put agent initializations here
    protected void setup() {

        // Printout a welcome message
        System.out.println("Hallo! Buyer-agent "+getAID().getName()+" is ready.");

        // Get the title of the book to buy as a start-up argument
        q = Question.generateQuestion();
        System.out.println("ola");
        if (q != null) {
            System.out.println("Question is "+ q.op1 + " " + q.operator + " " + q.op2);

            // Add a TickerBehaviour that schedules a request to seller agents every minute
            addBehaviour(new TickerBehaviour(this, 60000) {
                protected void onTick() {
                    // Update the list of seller agents
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("answer-agent");
                    template.addServices(sd);
                    try {
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        System.out.println("Found the following seller agents:");
                        answerAgents = new AID[result.length];
                        for (int i = 0; i < result.length; ++i) {
                            answerAgents[i] = result[i].getName();
                            System.out.println(answerAgents[i].getName());
                        }
                    }
                    catch (FIPAException fe) {
                        fe.printStackTrace();
                    }

                    // Perform the request
                    myAgent.addBehaviour(new AskQuestion());
                }
            } );
        }
        else {
            // Make the agent terminate
            System.out.println("No target book title specified");
            doDelete();
        }
    }

    // Put agent clean-up operations here
    protected void takeDown() {
        // Printout a dismissal message
        System.out.println("Buyer-agent "+getAID().getName()+" terminating.");
    }

    /**
     Inner class AskQuestion.
     This is the behaviour used by Book-buyer agents to request seller
     agents the target book.
     */
    private class AskQuestion extends Behaviour {
        private AID bestSeller; // The agent who provides the best offer
        private int bestPrice;  // The best offered price
        private int repliesCnt = 0; // The counter of replies from seller agents
        private MessageTemplate mt; // The template to receive replies
        private int step = 0;

        public void action() {
            switch (step) {
                case 0:
                    // Send the cfp to all sellers
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < answerAgents.length; ++i) {
                        cfp.addReceiver(answerAgents[i]);
                    }
                    try {
                        cfp.setContentObject(q);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cfp.setConversationId("question");
                    cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp);
                    // Prepare the template to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("question"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    step = 1;
                    break;
                case 1:
                    // Receive all proposals/refusals from seller agents
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        // Reply received
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            // This is an offer
                            double result = Double.parseDouble(reply.getContent());
                            answersReceived = new HashMap<AID, Boolean>();
                            answersReceived.put(reply.getSender(), q.getResult() == result);
                        }
                        repliesCnt++;
                        if (repliesCnt >= answerAgents.length) {
                            // We received all replies
                            step = 2;
                        }
                    }
                    else {
                        block();
                    }
                    break;
                case 2:
                    // Send the purchase order to the seller that provided the best offer
                    ACLMessage correct = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    ACLMessage reject = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                    for (int i = 0; i < answerAgents.length; i++) {
                        if(answersReceived.get(answerAgents[i]))
                            correct.addReceiver(answerAgents[i]);
                        else reject.addReceiver(answerAgents[i]);
                    }
                    try {
                        correct.setContentObject(true);
                        reject.setContentObject(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    correct.setConversationId("question");
                    reject.setConversationId("question");
                    correct.setReplyWith("order"+System.currentTimeMillis());
                    reject.setReplyWith("order"+System.currentTimeMillis());
                    myAgent.send(correct);
                    myAgent.send(reject);
                    // Prepare the template to get the purchase order reply
                    /*mt = MessageTemplate.and(MessageTemplate.MatchConversationId("question"),
                            MessageTemplate.MatchInReplyTo(order.getReplyWith()));*/
                    break;
                /*case 3:
                    // Receive the purchase order reply
                    reply = myAgent.receive(mt);
                    if (reply != null) {
                        // Purchase order reply received
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            // Purchase successful. We can terminate
                            System.out.println(targetBookTitle+" successfully purchased from agent "+reply.getSender().getName());
                            System.out.println("Price = "+bestPrice);
                            myAgent.doDelete();
                        }
                        else {
                            System.out.println("Attempt failed: requested book already sold.");
                        }

                        step = 4;
                    }
                    else {
                        block();
                    }
                    break;*/
            }
        }

        public boolean done() {
            return (step == 2);
        }
    }  // End of inner class AskQuestion
}
