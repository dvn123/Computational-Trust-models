package agents;

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


