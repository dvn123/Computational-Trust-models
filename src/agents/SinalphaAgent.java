package agents;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import util.Question;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class SinalphaAgent extends BaseAnswerAgent {
	
	private static final Integer ALPHA_POS=0;
	private static final Integer SINALPHA_POS=1;
	
	private Map <AID, float[]> addition = new HashMap<AID, float[]>();
	private Map <AID, float[]> subtraction = new HashMap<AID, float[]>();
	private Map <AID, float[]> multiplication = new HashMap<AID, float[]>();
	private Map <AID, float[]> division = new HashMap<AID, float[]>();
	
	private Map <Integer, AID> questions = new HashMap<Integer, AID>();
	
	private float alpha0, lambda_pos, lambda_neg, omega, ro;
	
	/*public SinalphaAgent() {
		
		this.alpha0=(float)((3.0*Math.PI)/2.0);
		this.lambda_pos=(float)1.00;
		this.lambda_neg=(float)-1.50;
		this.omega=(float)(Math.PI/12.0);
		this.ro=(float)0.50;
		
		init();
	}
	
	public SinalphaAgent(float alpha0, float lambda_pos, float lambda_neg, float omega) {
		
		this.alpha0=alpha0;
		this.lambda_pos=lambda_pos;
		this.lambda_neg=lambda_neg;
		this.omega=omega;
		this.ro=(float)0.50;
		
		init();
	}*/
	
	protected void init() {
		
		this.alpha0=(float)((3.0*Math.PI)/2.0);
		this.lambda_pos=(float)1.00;
		this.lambda_neg=(float)-1.50;
		this.omega=(float)(Math.PI/5.0);
		this.ro=(float)0.50;
		
		float[] initial_values={this.alpha0, calculateSinalpha(this.alpha0)};
		
		for(int i=0; i<wiseAgents.size(); i++) {
			addition.put(wiseAgents.get(i), initial_values);
			subtraction.put(wiseAgents.get(i), initial_values);
			multiplication.put(wiseAgents.get(i), initial_values);
			division.put(wiseAgents.get(i), initial_values);
		}
	}
	
	private float calculateAlpha(float previous_alpha, ACLMessage message) {
		
		if(message.getPerformative()==ACLMessage.CONFIRM)
			return previous_alpha+this.lambda_pos*this.omega;
		else
			return previous_alpha+this.lambda_neg*this.omega;
	}
	
	private float calculateSinalpha(float alpha) {
		
		return (float)(this.ro*(Math.sin(alpha)+1));
	}
	
	private void calculateAlgorithmSteps(AID agent_id, int type, ACLMessage message) {
		
		float[] values = new float[2];
		float[] new_values;
		
		switch (type)
		{
			case Question.OPERATOR_PLUS:
				values = addition.get(agent_id);
				new_values=calculateAgentValues(values, message);
				
				if(new_values!=null)
					addition.put(agent_id, new_values);
				
				break;
			case Question.OPERATOR_MINUS:
				values = subtraction.get(agent_id);
				new_values=calculateAgentValues(values, message);
				
				if(new_values!=null)
					subtraction.put(agent_id, new_values);
				
				break;
			case Question.OPERATOR_MUL:
				values  = multiplication.get(agent_id);
				new_values=calculateAgentValues(values, message);
				
				if(new_values!=null)
					multiplication.put(agent_id, new_values);
				
				break;
			case Question.OPERATOR_DIV:
				values = division.get(agent_id);
				new_values=calculateAgentValues(values, message);
				
				if(new_values!=null)
					division.put(agent_id, new_values);
				
				break;
			default:
				System.out.println("Invalid type!");
				break;
		}
	}

	private float[] calculateAgentValues(float[] values, ACLMessage message) {
		
		float[] new_values = new float[2];
		
		if(values!=null) {
			
			new_values[ALPHA_POS]=calculateAlpha(values[ALPHA_POS], message);
			new_values[SINALPHA_POS]=calculateSinalpha(values[ALPHA_POS]);
			
			return new_values;
			
		} else {
			System.out.println("Invalid agent!");
			
			return null;
		}
	}
	
	private AID chooseBestAgent(int type) {
		
		switch (type)
		{
			case Question.OPERATOR_PLUS:
				return compareAndReturnBestAgent(addition);
			case Question.OPERATOR_MINUS:
				return compareAndReturnBestAgent(subtraction);
			case Question.OPERATOR_MUL:
				return compareAndReturnBestAgent(multiplication);
			case Question.OPERATOR_DIV:
				return compareAndReturnBestAgent(division);
			default:
				System.out.println("Invalid type!");
				break;
		}
		return null;
	}
	
	private AID compareAndReturnBestAgent(Map<AID, float[]> agents) {
		
		AID best=null;
		float bestValue = 0, currentValue;
		boolean firstValue=true;
		Vector<AID> equalBests = new Vector<AID>();
		
		for (AID key : agents.keySet()) {
			
			if(firstValue) {
				best=key;
				bestValue=agents.get(key)[SINALPHA_POS];
			} else {
				
				currentValue=agents.get(key)[SINALPHA_POS];
				
				if(currentValue>bestValue) {
					bestValue=agents.get(key)[SINALPHA_POS];
					best=key;
				} 
			}
		}
		
		for (AID key : agents.keySet()) {
			if(agents.get(key)[SINALPHA_POS]==bestValue)
				equalBests.addElement(key);
		}
		
		if(equalBests.size()>1) {
			
			Random rand = new Random();
			int agent = rand.nextInt(equalBests.size());
			best = equalBests.get(agent);
		}
		
		return best;
	}

	protected void handleSolution(ACLMessage message) {
		
		if (message.getPerformative() == ACLMessage.CONFIRM)
			writeMsg(message.getSender().getLocalName() + " - Answer is correct");
		else if (message.getPerformative() == ACLMessage.DISCONFIRM)
			writeMsg(message.getSender().getLocalName() + " - Answer is incorrect");
		else 
			writeMsg(message.getSender().getLocalName() + " - estas tolo");
		
		AID agentResponsable;
		try {
			agentResponsable = questions.get(((Question) message.getContentObject()).getId());
			
			calculateAlgorithmSteps(agentResponsable, 
					((Question) message.getContentObject()).getOperator(), message);
			
			questions.remove(((Question) message.getContentObject()).getId());
					
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}
	
	protected AID getBestWiseAgent(Question question) {
		
		AID bestAgent = chooseBestAgent(question.getOperator());
		questions.put(question.getId(), bestAgent);
			
		return bestAgent;
	}
	
}
