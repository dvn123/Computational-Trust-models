package util;

import java.lang.reflect.Array;

public class WiseData {
	private static final int Float = 0;
	String name;
	float knowledge[];
	float tiredness[];
	
	public WiseData(String name, float additions, float subtractions, float multiplications, float divisions, float tiredness) {
		this.name = name;
		knowledge = new float[4];
		this.tiredness = new float[3];
		
		knowledge[Question.OPERATOR_PLUS] = additions;
		knowledge[Question.OPERATOR_MINUS] = subtractions;
		knowledge[Question.OPERATOR_MUL] = multiplications;
		knowledge[Question.OPERATOR_DIV] = divisions;
		
		this.tiredness[0] = tiredness;
		this.tiredness[1] = 0;
		this.tiredness[2] = 0;
		
	}
	
	public WiseData() {
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public <T> T[] concatenate (T[] a, T[] b) {
	    int aLen = a.length;
	    int bLen = b.length;

	    @SuppressWarnings("unchecked")
	    T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
	    System.arraycopy(a, 0, b, 0, aLen);
	    System.arraycopy(b, 0, b, aLen, bLen);

	    return c;
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
	
	
}
