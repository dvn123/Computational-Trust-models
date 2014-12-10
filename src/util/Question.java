package util;

import java.io.Serializable;
import java.util.Random;


@SuppressWarnings("serial")
public class Question implements Serializable {
	public static final int MAX_OPERATOR_NUMBER = 4;
	public static final int MAX_OPERAND = 100;
	public static final int OPERATOR_PLUS = 0;
	public static final int OPERATOR_MINUS = 1;
	public static final int OPERATOR_MUL = 2;
	public static final int OPERATOR_DIV = 3;

	public static final int OPERATOR = 1;
	public static final int OPERAND1 = 0;
	public static final int OPERAND2 = 2;

	private int operatorArray[]= {0,0,0,0};
	private char operatorStringArray[]= {'+','-','*','/'};
	private int operator;


	private int op1, op2;

	private static int ID = 0;
	private int id; //each question has an id



	Question(int operator, int op1, int op2) {
		this.operator = operator;
		this.operatorArray[operator] = 1;
		this.op1 = op1;
		this.op2 = op2;
		setID();
	}



	public static Question generateQuestion() {
		Random rand = new Random();

		int operator = rand.nextInt(MAX_OPERATOR_NUMBER);
		int op1 = rand.nextInt(MAX_OPERAND);
		int op2 = rand.nextInt(MAX_OPERAND - 1) + 1;
		//int operator=1;

		return new Question(operator, op1, op2);
	}

	public int getResult() {
		float plus = operatorArray[OPERATOR_PLUS] * (op1 + op2);
		float minus = operatorArray[OPERATOR_MINUS] * (op1 - op2);
		float mul = operatorArray[OPERATOR_MUL] * (op1 * op2);
		float div = operatorArray[OPERATOR_DIV] * (op1 / op2);
		return  (int) Math.round(plus + minus + mul + div);
	}

	public void printQuestion() {
		if (Constants.printQuestions)
			System.out.println("Q" + id + ": " + op1 + " " + operatorStringArray[operator] + " " + op2);
	}
	
	public String getStringQuestion() {
		return "Q" + id + ": " + op1 + " " + operatorStringArray[operator] + " " + op2;
	}

	public int getOperator() {
		return operator;
	}

	private void setID() {
		this.id = ID;
		ID++;
	}

	public int getId() {
		return id;
	}
}
