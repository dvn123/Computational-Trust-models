package agents;

import util.Question;

import java.util.Random;

public class RandomReplyAgent extends ReplyAgent {
    public static final int MAX = 100;
    public static final int MIN = 0;

    @Override
    float reply(Question q) {
        Random rand = new Random();
        return rand.nextInt((MAX - MIN) + 1) + MIN;
    }
}
