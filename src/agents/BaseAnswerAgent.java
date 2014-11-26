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
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.leap.Iterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import util.Constants;
import util.Question;


public class BaseAnswerAgent extends Agent {
	private ArrayList<AID> wiseAgents = null;
	

	protected void setup() {
		String serviceName = Constants.SERVICE_NAME_PLAYER;
		// Read the name of the service to register as an argument
		Object[] args = getArguments();
		
		//Find wise agents
		registerPlayer(serviceName, args);
		wiseAgents = getWiseAgents();
		

		writeMsg("waiting for requests...");

		addBehaviour(new AnswearingBehaviour(this));
	}

	private void registerPlayer(String serviceName, Object[] args) {
		if (args != null && args.length > 0) {
			serviceName = (String) args[0];
		}

		// Register the service
		writeMsg("registering service \""+serviceName+"\" of type \"" + Constants.SERVICE_DESCRIPTION_TYPE_PLAYER + "\"");
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
	
	private ArrayList<AID> getWiseAgents() {
		ArrayList<AID> agentsFound = new ArrayList<AID>();
		
		writeMsg("searching for wise agents...");
		
		try {
			// Build the description used as template for the search
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription templateSd = new ServiceDescription();
			templateSd.setType(Constants.SERVICE_DESCRIPTION_TYPE_WISE);
			template.addServices(templateSd);

			SearchConstraints sc = new SearchConstraints();
			// We want to receive 10 results at most
			sc.setMaxResults(new Long(10));

			DFAgentDescription[] results = DFService.search(this, template, sc);
			if (results.length > 0) {
				writeMsg("found the following "+Constants.SERVICE_DESCRIPTION_TYPE_WISE + " services:");
				for (int i = 0; i < results.length; ++i) {
					DFAgentDescription dfd = results[i];
					AID provider = dfd.getName();
					// The same agent may provide several services; we are only interested
					// in the SERVICE_DESCRIPTION_TYPE_PLAYER one
					Iterator it = dfd.getAllServices();
					while (it.hasNext()) {
						ServiceDescription sd = (ServiceDescription) it.next();
						if (sd.getType().equals(Constants.SERVICE_DESCRIPTION_TYPE_WISE)) {
							writeMsg("- Service \""+sd.getName()+"\" provided by wise agent "+provider.getName());
							agentsFound.add(provider);
						}
					}
				}
			}	
			else {
				writeMsg("did not find any players");
			}
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		return agentsFound;
	} 

	private AID getBestWiseAgent(Question question) {
		//taking care question type do something
		
		Random x = new Random();
		int index = x.nextInt(wiseAgents.size());
		return wiseAgents.get(index);
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
				writeMsg("REQUEST received from "+request.getSender().getLocalName() + " type: " + request.getPerformative()); //+". Action is "+request.getContent());

				Question x = null;
				try {
					x = (Question) request.getContentObject();
					x.printQuestion();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}


				// Fill the REQUEST message
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

				//Algorithm to 
				msg.addReceiver(getBestWiseAgent(x));

				msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
				// We want to receive a reply in 10 secs
				msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
				msg.setOntology(Constants.WISE_CONVERSATION_ONTOLOGY);

				try {
					msg.setContentObject(x);
				} catch (IOException e) {
					e.printStackTrace();
				}
				send(msg);

				ACLMessage wiseAnswer = blockingReceive(MessageTemplate.MatchOntology(Constants.WISE_CONVERSATION_ONTOLOGY),50000);

				// We agree to perform the action. Note that in the FIPA-Request
				// protocol the AGREE message is optional. Return null if you
				// don't want to send it.
				writeMsg("Agree");
				ACLMessage agree = request.createReply();

				double y;
				y = Double.parseDouble(wiseAnswer.getContent());
				agree.setContent(Double.toString(y));
				writeMsg("Received from agent " + wiseAnswer.getSender().getLocalName()+ "sabio: " + y);

				agree.setPerformative(ACLMessage.INFORM);

				send(agree);

				ACLMessage ok = blockingReceive(MessageTemplate.MatchOntology(Constants.SOLUTION_ONTOLOGY),50000);
				
				//taking care of the answer
				handleSolution(ok);
			}
			else {
				block();
			}


		}

		private void handleSolution(ACLMessage ok) {
			if (ok.getPerformative() == ACLMessage.CONFIRM)
				writeMsg(ok.getSender().getLocalName() + " - Answer is correct");
			else if (ok.getPerformative() == ACLMessage.DISCONFIRM)
				writeMsg(ok.getSender().getLocalName() + " - Answer is incorrect");
			else 
				writeMsg(ok.getSender().getLocalName() + " - estás tolo");
		}
	}
	
	private void writeMsg(String msg) {
		System.out.println("Agent "+ getLocalName() + ": " + msg);
	}
}

