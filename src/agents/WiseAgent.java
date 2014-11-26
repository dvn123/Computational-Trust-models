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


public class WiseAgent extends Agent {

	protected void setup() {
		
		registerWise();
		
		writeMsg("waiting for requests...");
		MessageTemplate template = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );

		addBehaviour(new AchieveREResponder(this, template) {
			protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
				writeMsg("REQUEST received from "+request.getSender().getLocalName()); //+". Action is "+request.getContent());

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
					writeMsg("Agree");
					ACLMessage agree = request.createReply();
					if(x != null) {
						writeMsg("result: " + Double.toString(x.getResult()));
						agree.setContent(Double.toString(x.getResult()));
					}
					agree.setPerformative(ACLMessage.INFORM);

					return agree;
		
			}


			protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
				if (performAction()) {
					writeMsg("Action successfully performed");
					ACLMessage inform = request.createReply();
					inform.setPerformative(ACLMessage.INFORM);
					return inform;
				}
				else {
					writeMsg("Action failed");
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
		writeMsg("registering service \""+serviceName+"\" of type \"" + Constants.SERVICE_DESCRIPTION_TYPE_WISE + "\"");
		
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
	
	private void writeMsg(String msg) {
		System.out.println("Agent "+ getLocalName() + ": " + msg);
	}
}

