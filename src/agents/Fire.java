package agents;

/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import util.Question;

/**
 This example shows how to implement the responder role in
 a FIPA-request interaction protocol. In this case in particular
 we use an <code>AchieveREResponder</code> ("Achieve Rational effect")
 to serve requests to perform actions from other agents. We use a
 random generator to simulate request refusals and action execution
 failures.
 @author Giovanni Caire - TILAB
 */
public class Fire extends Agent {

	protected void setup() {
		System.out.println("Agent "+getLocalName()+" waiting for requests...");
		MessageTemplate template = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );

		SequentialBehaviour sb = new SequentialBehaviour();

		addBehaviour(sb);

		sb.addSubBehaviour(new AchieveREResponder(this, template) {
			protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
				System.out.println("Agent "+getLocalName()+": REQUEST received from "+request.getSender().getName() + " type: " + request.getPerformative()); //+". Action is "+request.getContent());

				/*if (request.getOntology().equals("result")) {
					ACLMessage agree = request.createReply();
					agree.setPerformative(ACLMessage.AGREE);
					System.out.println("FIRE: I agree!");
					return agree;
				}*/
					
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
				msg.setContent("dummy-action");
				msg.setOntology("oioi");
				
				try {
					msg.setContentObject(x);
				} catch (IOException e) {
					e.printStackTrace();
				}
				send(msg);

				ACLMessage hey = blockingReceive(MessageTemplate.MatchOntology("oioi"),50000);

				if (checkAction()) {
					// We agree to perform the action. Note that in the FIPA-Request
					// protocol the AGREE message is optional. Return null if you
					// don't want to send it.
					System.out.println("Agent "+getLocalName()+": Agree");
					ACLMessage agree = request.createReply();

					double y;
					try {
						y = (Double) hey.getContentObject();
						agree.setContentObject(new Double(y));
						System.out.println("FIRE: Received from sabio: " + y);
					} catch (UnreadableException e) {
						
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
			protected void handleInform(ACLMessage inform) {

				System.out.println("Agent "+inform.getSender().getName()+" successfully performed the requested action");
				try {
					System.out.println("Fire: Result returned: " + ((Double) inform.getContentObject()));
				} catch (UnreadableException e) {
					System.err.println("MANELAERROR");
					e.printStackTrace();
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

