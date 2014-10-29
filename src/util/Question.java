package util;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by diogo on 22-10-2014.
 */
public class Question implements Serializable {
    public static final int MAX_OPERATOR_NUMBER = 4;
    public static final double MAX_OPERAND = 100;
    public static final int OPERATOR_PLUS = 0;
    public static final int OPERATOR_MINUS = 1;
    public static final int OPERATOR_MUL = 2;
    public static final int OPERATOR_DIV = 3;

    public int operatorArray[]= {0,0,0,0};
    public int operator;
    public double op1, op2;

    Question(int operator, double op1, double op2) {
        this.operator = operator;
        this.operatorArray[operator] = 1;
        this.op1 = op1;
        this.op2 = op2;
    }

    public static Question generateQuestion() {
        Random rand = new Random();

        int operator = rand.nextInt(MAX_OPERATOR_NUMBER);
        double op1 = rand.nextDouble() * MAX_OPERAND;
        double op2 = rand.nextDouble() * MAX_OPERAND;

        return new Question(operator, op1, op2);
    }

    public double getResult() {
        double plus = operatorArray[OPERATOR_PLUS] * (op1 + op2);
        double minus = operatorArray[OPERATOR_MINUS] * (op1 - op2);
        double mul = operatorArray[OPERATOR_MUL] * (op1 * op2);
        double div = operatorArray[OPERATOR_DIV] * (op1 / op2);
        return  plus + minus + mul + div;
    }
}
