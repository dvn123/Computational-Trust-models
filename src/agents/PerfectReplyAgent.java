package agents;

import util.Question;

/**
 * Created by diogo on 06-11-2014.
 */
public class PerfectReplyAgent extends ReplyAgent {
    @Override
    float reply(Question q) {
        return q.getResult();
    }
}
