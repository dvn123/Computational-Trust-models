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

public class Sabio extends Agent {

	protected void setup() {
		
		registerWise();
		
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

					// We agree to perform the action. Note that in the FIPA-Request
					// protocol the AGREE message is optional. Return null if you
					// don't want to send it.
					System.out.println("Agent "+getLocalName()+": Agree");
					ACLMessage agree = request.createReply();
					if(x != null) {
						System.out.println("Sabio result: " + Double.toString(x.getResult()));
						agree.setContent(Double.toString(x.getResult()));
					}
					agree.setPerformative(ACLMessage.INFORM);

					return agree;
		
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

	private boolean performAction() {
		// Simulate action execution by generating a random number
		return (Math.random() > 0.2);
	}
	
	private void registerWise() {
			String serviceName = Constants.SERVICE_NAME_WISE;

		// Register the service
		System.out.println("Agent "+getLocalName()+" registering service \""+serviceName+"\" of type \"" + Constants.SERVICE_DESCRIPTION_TYPE_WISE + "\"");
		
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setName(serviceName);
			sd.setType(Constants.SERVICE_DESCRIPTION_TYPE_WISE);
			// Agents that want to use this service need to "know" the weather-forecast-ontology
			sd.addOntologies(Constants.SERVICE_DESCRIPTION_ONTOLOGY_WISE);
			dfd.addServices(sd);

			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
}

