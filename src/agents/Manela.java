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


public class Manela extends Agent {
	private Question question;

	protected void setup() {
		ArrayList<AID> players = getPlayers();

		// Read names of responders as arguments
		
		if (players != null && players.size() > 0)
			addBehaviour(new QuestioningBehaviour(this, players));
		else
			System.out.println("No responder specified.");
		
		/*Object[] args = getArguments();
		if (args != null && args.length > 0) {
			addBehaviour(new QuestioningBehaviour(this, args));
		}
		else {
			System.out.println("No responder specified.");
		}*/
	}



	private ArrayList<AID> getPlayers() {
		ArrayList<AID> agentsFound = new ArrayList<AID>();
		System.out.println("Agent "+getLocalName()+" searching for services of type \"\"");
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
				System.out.println("Agent "+getLocalName()+" found the following "+Constants.SERVICE_DESCRIPTION_TYPE_PLAYER+" services:");
				for (int i = 0; i < results.length; ++i) {
					DFAgentDescription dfd = results[i];
					AID provider = dfd.getName();
					// The same agent may provide several services; we are only interested
					// in the weather-forcast one
					Iterator it = dfd.getAllServices();
					while (it.hasNext()) {
						ServiceDescription sd = (ServiceDescription) it.next();
						if (sd.getType().equals(Constants.SERVICE_DESCRIPTION_TYPE_PLAYER)) {
							System.out.println("- Service \""+sd.getName()+"\" provided by agent "+provider.getName());
							agentsFound.add(provider);
						}
					}
				}
			}	
			else {
				System.out.println("Agent "+getLocalName()+" did not find any weather-forecast service");
			}
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		return agentsFound;
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

			if (players != null && players.size() > 0) {
				nResponders = players.size();
				System.out.println("Requesting dummy-action to "+nResponders+" responders.");

				// Fill the REQUEST message
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				for (int i = 0; i < players.size(); ++i) {
					msg.addReceiver(players.get(i)/*new AID((String) players[i], AID.ISLOCALNAME)*/);
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
				double result = Double.parseDouble(hey.getContent());
				System.out.println("MANELA: Result returned: " + result);

				ACLMessage reply = hey.createReply();
				reply.setOntology("solution");

				reply.setPerformative(result == question.getResult()? ACLMessage.CONFIRM : ACLMessage.DISCONFIRM);
				System.out.println("MANELA: Sending solution");
				send(reply);
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}


