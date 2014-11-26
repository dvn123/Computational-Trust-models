package agents;

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
import java.util.ArrayList;
import jade.util.leap.Iterator;

import java.io.IOException;
import java.util.Date;

import util.Constants;
import util.Question;


public class QuestionAgent extends Agent {
	private Question question;

	protected void setup() {
		ArrayList<AID> players = getPlayers(); //Search for players


		if (players != null && players.size() > 0)
			addBehaviour(new QuestioningBehaviour(this, players));
		else
			writeMsg("No players found.");
	}



	private ArrayList<AID> getPlayers() {
		ArrayList<AID> agentsFound = new ArrayList<AID>();

		writeMsg("searching for players...");

		try {
			// Build the description used as template for the search
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription templateSd = new ServiceDescription();
			templateSd.setType(Constants.SERVICE_DESCRIPTION_TYPE_PLAYER);
			template.addServices(templateSd);

			SearchConstraints sc = new SearchConstraints();
			// We want to receive 10 results at most
			sc.setMaxResults(new Long(10));

			DFAgentDescription[] results = DFService.search(this, template, sc);
			if (results.length > 0) {
				writeMsg("found the following "+Constants.SERVICE_DESCRIPTION_TYPE_PLAYER+" services:");
				for (int i = 0; i < results.length; ++i) {
					DFAgentDescription dfd = results[i];
					AID provider = dfd.getName();
					// The same agent may provide several services; we are only interested
					// in the SERVICE_DESCRIPTION_TYPE_PLAYER one
					Iterator it = dfd.getAllServices();
					while (it.hasNext()) {
						ServiceDescription sd = (ServiceDescription) it.next();
						if (sd.getType().equals(Constants.SERVICE_DESCRIPTION_TYPE_PLAYER)) {
							writeMsg("- Service \""+sd.getName()+"\" provided by player "+provider.getName());
							agentsFound.add(provider);
						}
					}
				}
			}	
			else {
				writeMsg("Did not find any players");
			}
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		return agentsFound;
	} 

	private void writeMsg(String msg) {
		System.out.println("Agent "+ getLocalName() + ": " + msg);
	}

	private class QuestioningBehaviour extends CyclicBehaviour {
		ArrayList<AID> players;
		private int nResponders;

		MessageTemplate template = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));

		public QuestioningBehaviour (Agent a, ArrayList<AID> args) {
			super(a);
			this.players = args;
		}

		@Override
		public void action() {
			System.out.println("***********************************************************");

			if (players != null && players.size() > 0) {
				nResponders = players.size();
				writeMsg("Questioning "+nResponders+" responders.");

				// Fill the REQUEST message
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				for (int i = 0; i < players.size(); ++i) {
					msg.addReceiver(players.get(i)/*new AID((String) players[i], AID.ISLOCALNAME)*/);
				}

				msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
				// We want to receive a reply in 10 secs
				msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
				//msg.setContent("dummy-action");

				question = Question.generateQuestion();

				try {
					msg.setContentObject(question);
				} catch (IOException e) {
					e.printStackTrace();
				}

				send(msg);

				//wait for multiple players
				for (int i = 0; i < players.size(); ++i) {
					ACLMessage answer = blockingReceive(template, 10000);

					writeMsg("Agent "+answer.getSender().getLocalName()+" successfully performed the requested action");

					double result = Double.parseDouble(answer.getContent());
					writeMsg("Result returned by " + answer.getSender().getLocalName() + ": " + result);

					ACLMessage reply = answer.createReply();
					reply.setOntology(Constants.SOLUTION_ONTOLOGY);

					reply.setPerformative(result == question.getResult() ? ACLMessage.CONFIRM : ACLMessage.DISCONFIRM);
					reply.setContent(Integer.toString(question.getId()));
					
					try {
						reply.setContentObject(question);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					writeMsg("Sending solution to " + answer.getSender().getLocalName());
					send(reply);
				}
			}
			System.out.println("***********************************************************");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
}


