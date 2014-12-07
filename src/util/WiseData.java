package util;

import java.lang.reflect.Array;

public class WiseData {
	private static final int Float = 0;
	String name;
	float knowledge[];
	float tiredness[];
	
	public WiseData(String name, float additions, float subtractions, float multiplications, float divisions, float restore, float factor, float round) {
		this.name = name;
		knowledge = new float[4];
		this.tiredness = new float[3];
		
		knowledge[Question.OPERATOR_PLUS] = additions;
		knowledge[Question.OPERATOR_MINUS] = subtractions;
		knowledge[Question.OPERATOR_MUL] = multiplications;
		knowledge[Question.OPERATOR_DIV] = divisions;
		
		this.tiredness[0] = restore;
		this.tiredness[1] = factor;
		this.tiredness[2] = round;
		
	}
	
	public WiseData() {
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Object[] getData() {
		Object[] data = new Object[knowledge.length + tiredness.length];
		int index = 0;
		
		for (int i = 0; i < knowledge.length; i++,index++) 
			data[index] = knowledge[i];
		for (int i = 0; i < tiredness.length; i++,index++) 
			data[index] = tiredness[i];
		
		return data;
	}
	
	public String getAgentHtml() {
		StringBuilder html = new StringBuilder();
		html.append("<div>");
		html.append("<h3><strong>Agent name: </strong>");
		html.append(name);
		//start knowledge
		html.append("</h3><div><p><strong>Knowledge</strong></p>");
		html.append("<ul>");
		
		html.append("<li>");
		html.append("Addition: ");
		html.append(knowledge[Question.OPERATOR_PLUS]);
		html.append("%</li>");
		
		html.append("<li>");
		html.append("Subtraction: ");
		html.append(knowledge[Question.OPERATOR_MINUS]);
		html.append("%</li>");
		
		html.append("<li>");
		html.append("Multiplication: ");
		html.append(knowledge[Question.OPERATOR_MUL]);
		html.append("%</li>");
		
		html.append("<li>");
		html.append("Division: ");
		html.append(knowledge[Question.OPERATOR_DIV]);
		html.append("%</li>");
		
		
		html.append("</ul></div>");
		//end knowledge
		
		//start tiredness
		html.append("<div><p><strong>Tiredness</strong></p>");
		html.append("<ul>");
		
		html.append("<li>");
		html.append("Tiredeness rate: ");
		html.append(this.tiredness[1]);
		html.append("%</li>");
		
		html.append("<li>");
		html.append("Restore rate: ");
		html.append(this.tiredness[0]);
		html.append("%</li>");
		
		html.append("<li>");
		html.append("Rounds to restore: ");
		html.append((int)Math.round(this.tiredness[2]));
		html.append("</li>");

		html.append("</ul></div>");
		
		//end tiredness
		
		
		html.append("</div><br>");
		return html.toString();
	}
	
}
