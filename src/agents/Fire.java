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
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.Date;

import util.Constants;
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
		String serviceName = Constants.SERVICE_DESCRIPTION_TYPE_PLAYER + " player";
		// Read the name of the service to register as an argument
		Object[] args = getArguments();
		
		registerPlayer(serviceName, args);

		System.out.println("Agent "+getLocalName()+" waiting for requests...");

		addBehaviour(new AnswearingBehaviour(this));
	}

	private void registerPlayer(String serviceName, Object[] args) {
		if (args != null && args.length > 0) {
			serviceName = (String) args[0];
		}

		// Register the service
		System.out.println("Agent "+getLocalName()+" registering service \""+serviceName+"\" of type \"" + Constants.SERVICE_DESCRIPTION_TYPE_PLAYER + "\"");
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setName(serviceName);
			sd.setType(Constants.SERVICE_DESCRIPTION_TYPE_PLAYER);
			// Agents that want to use this service need to "know" the weather-forecast-ontology
			sd.addOntologies(Constants.SERVICE_DESCRIPTION_ONTOLOGY_PLAYER);
			// Agents that want to use this service need to "speak" the FIPA-SL language
			/*sd.addLanguages(FIPANames.ContentLanguage);
	  		sd.addProperties(new Property("country", "Italy"));*/
			dfd.addServices(sd);

			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	private class AnswearingBehaviour extends CyclicBehaviour {
		MessageTemplate template = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );

		public AnswearingBehaviour(Agent x) {
			super(x);
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
}

