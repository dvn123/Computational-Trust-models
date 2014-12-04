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
import jade.util.leap.Iterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import util.Constants;
import util.Question;


public class QuestionAgent extends Agent {

	private static int[] agentsResults = new int[10];
	private static Stack<Question> questions = new Stack<Question>();
	private static ArrayList<AID> players;

	protected void setup() {
		players = getPlayers();

		/*for(int index = 0; index < agentsResults.length; index++) {
			agentsResults[index] = 0;
		}*/

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

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
		if (Constants.logManela)
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
			writeMsg("***********************************************************");

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

				questions.push(Question.generateQuestion());

				Question question = questions.peek();


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

					int result = Integer.parseInt(answer.getContent());
					writeMsg("Result returned by " + answer.getSender().getLocalName() + ": " + result);

					ACLMessage reply = answer.createReply();
					reply.setOntology(Constants.SOLUTION_ONTOLOGY);

					if(result == question.getResult()) {
						reply.setPerformative(ACLMessage.CONFIRM);
						int agentIndex = getAgentIndex(answer.getSender().getLocalName());
						agentsResults[agentIndex] += 1;
					}
					else 
						reply.setPerformative(ACLMessage.DISCONFIRM);


					reply.setContent(Integer.toString(question.getId()));

					try {
						reply.setContentObject(question);
					} catch (IOException e) {
						e.printStackTrace();
					}

					writeMsg("Operation result: " + question.getResult());
					writeMsg("Sending solution to " + answer.getSender().getLocalName());
					send(reply);
				}
			}
			writeMsg("***********************************************************");
			try {
				Thread.sleep(900);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	public static double getAgentRatio(String agent) {
		int i = getAgentIndex(agent);
		
		if (i < 0)
			return 0;

		double ratio = (double) agentsResults[i] / (questions.size() == 0 ? 1 : questions.size()) * 100;	

		if (Constants.printRatio)
			System.err.println("-------> " + agentsResults[i] + "/" + questions.size() + " = " + ratio + "   -  - " + agent);

		return ratio;
	}

	public static int getNumberQuestions() {
		return questions.size();
	}
	
	public static int getAgentIndex(String localName) {
		int i;
		for (i = 0; i < players.size(); i++) {

			if(players.get(i).getLocalName().equals(localName))
				break;
		}


		if (i == players.size())
			return -1;
		
		return i;
	}
}


