package agents;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import util.Question;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

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
	
	public SinalphaAgent() {
		
		this.alpha0=(float)((3.0*Math.PI)/2.0);
		this.lambda_pos=(float)1.00;
		this.lambda_neg=(float)-1.50;
		this.omega=(float)(Math.PI/12.0);
		this.ro=(float)0.50;
		
		initialize();
	}
	
	public SinalphaAgent(float alpha0, float lambda_pos, float lambda_neg, float omega) {
		
		this.alpha0=alpha0;
		this.lambda_pos=lambda_pos;
		this.lambda_neg=lambda_neg;
		this.omega=omega;
		this.ro=(float)0.50;
		
		initialize();
	}
	
	private void initialize() {
		
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
		
		if(values!=null) {
			
			values[ALPHA_POS]=calculateAlpha(values[ALPHA_POS], message);
			values[SINALPHA_POS]=calculateSinalpha(values[ALPHA_POS]);
			
			return values;
			
		} else {
			System.out.println("Invalid agent!");
			
			return null;
		}
	}
	
	private AID chooseBestAgent(int type) {
		
		//TODO: escolher o melhor agente e retornar
		
		switch (type)
		{
			case Question.OPERATOR_PLUS:
				
				break;
			case Question.OPERATOR_MINUS:
				
				break;
			case Question.OPERATOR_MUL:
				
				break;
			case Question.OPERATOR_DIV:
				
				
				break;
			default:
				System.out.println("Invalid type!");
				break;
		}
		
		return wiseAgents.get(0);
	}
	
	protected void handleSolution(ACLMessage ok) {
		if (ok.getPerformative() == ACLMessage.CONFIRM)
			writeMsg(ok.getSender().getLocalName() + " - Answer is correct");
		else if (ok.getPerformative() == ACLMessage.DISCONFIRM)
			writeMsg(ok.getSender().getLocalName() + " - Answer is incorrect");
		else 
			writeMsg(ok.getSender().getLocalName() + " - estás tolo");
		
		//TODO: atualizar agente
	}
	
	protected AID getBestWiseAgent(Question question) {
		
		AID result = chooseBestAgent(question.getOperator());
		
		//TODO: adicionar result e idQuestion ao map
		
		return result;
	}
	
}
