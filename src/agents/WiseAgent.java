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

import java.util.Random;


public class WiseAgent extends Agent {
    public static final int MAX = 100;
    public static final int MIN = 0;

    float[] knowledge;

	String role;

    float[] tiredness;
    int[] question_latest;

    float tiredness_factor;
    int tiredness_rest_limit;
    float tiredness_restored_per_rest;

    float reply(Question q) {
        Random rand = new Random();
        float knowledge_loc = knowledge[q.getOperator()] - tiredness[q.getOperator()];
        updateTiredness(q);
        if(rand.nextInt(101) < knowledge_loc) {
            return q.getResult();
        } else {
            return q.getResult()*knowledge_loc + (1-knowledge_loc)*(rand.nextInt((MAX - MIN) + 1) + MIN);
        }
    }

    void updateTiredness(Question q) {
        question_latest[q.getOperator()] = 0;
        question_latest[(q.getOperator() + 1)%4]++;
        question_latest[(q.getOperator() + 2)%4]++;
        question_latest[(q.getOperator() + 3)%4]++;

        for(int i = 0; i < question_latest.length; i++) {
            if(question_latest[i] > tiredness_rest_limit) {
                tiredness[i] = (tiredness[i] - tiredness_restored_per_rest) < 0 ? 0 : tiredness[i] - tiredness_restored_per_rest;
            }
        }

        tiredness[q.getOperator()] = tiredness[q.getOperator()] + tiredness_factor;
    }

	protected void setup() {
        Random rand = new Random();

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
        	if (args[0].getClass() == Float.class) {
        		knowledge = new float[] {Float.parseFloat((String)args[0]),Float.parseFloat((String) args[1]), Float.parseFloat((String) args[2]), Float.parseFloat((String) args[3])};
                tiredness_restored_per_rest = Float.parseFloat((String) args[4]);
                tiredness_factor = Float.parseFloat((String)args[5]);
                tiredness_rest_limit = (int) Math.floor(Float.parseFloat((String) args[6]));
        	}
        	else {
        		knowledge = new float[] {Float.parseFloat((String)args[0]), Float.parseFloat((String) args[1]), Float.parseFloat((String) args[2]), Float.parseFloat((String) args[3])};
                tiredness_restored_per_rest = Float.parseFloat((String) args[4]);
                tiredness_factor = Float.parseFloat((String) args[5]);
                tiredness_rest_limit = Integer.parseInt((String) args[6]);
        	}  
        } else {
            knowledge = new float[] {rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), rand.nextFloat()};
            tiredness_restored_per_rest = (float) 0.02;
            tiredness_factor = (float) 0.01;
            tiredness_rest_limit = 5;
        }

        tiredness = new float[] {0,0,0,0};
        question_latest = new int[] {999,999,999,999};
		
        writeMsg("knowledge: " + knowledge[0] + " " + knowledge[1] + " " + knowledge[2] + " " + knowledge[3] + " " ); 
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
						String value = Float.toString(reply(x));
						writeMsg("result: " + value);
						agree.setContent(value);
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

