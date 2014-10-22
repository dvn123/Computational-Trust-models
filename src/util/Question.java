package util;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by diogo on 22-10-2014.
 */
public class Question implements Serializable {
    public static final int MAX_OPERATOR_NUMBER = 3;
    public static final double MAX_OPERAND = 100;

    public int operator;
    public double op1, op2;

    Question(int operator, double op1, double op2) {
        this.operator = operator;
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
}
