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
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.Date;

import util.Question;

/**
   This example shows how to implement the initiator role in 
   a FIPA-request interaction protocol. In this case in particular 
   we use an <code>AchieveREInitiator</code> ("Achieve Rational effect") 
   to request a dummy action to a number of agents (whose local
   names must be specified as arguments).
   @author Giovanni Caire - TILAB
 */
public class Manela extends Agent {
	
	private class QuestioningBehaviour extends CyclicBehaviour {
		Object[] args;
		private int nResponders;
		
		MessageTemplate template = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		
		public QuestioningBehaviour (Agent a, Object[] args) {
			super(a);
			this.args = args;
		}
		
		@Override
		public void action() {
			
			if (args != null && args.length > 0) {
				nResponders = args.length;
				System.out.println("Requesting dummy-action to "+nResponders+" responders.");

				// Fill the REQUEST message
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				for (int i = 0; i < args.length; ++i) {
					msg.addReceiver(new AID((String) args[i], AID.ISLOCALNAME));
				}
				msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
				// We want to receive a reply in 10 secs
				msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
				msg.setContent("dummy-action");

				question = Question.generateQuestion();

				try {
					msg.setContentObject(question);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				send(msg);

				ACLMessage hey = blockingReceive(template, 10000);

				System.out.println("Agent "+hey.getSender().getName()+" successfully performed the requested action");
				try {
					double result = (Double) hey.getContentObject();
					System.out.println("MANELA: Result returned: " + result);

					/*ACLMessage reply = inform.createReply();
					reply.setOntology("solution");

					reply.setPerformative(result == question.getResult()? ACLMessage.CONFIRM : ACLMessage.DISCONFIRM);
					System.out.println("MANELA: Sending solution");
					send(reply);*/


				} catch (UnreadableException e) {
					System.err.println("MANELAERROR");
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	private Question question;

	protected void setup() {
		// Read names of responders as arguments
		Object[] args = getArguments();
		if (args != null && args.length > 0) {

		addBehaviour(new QuestioningBehaviour(this, args));
		
		}
		else {
			System.out.println("No responder specified.");
		}
	} 
}


