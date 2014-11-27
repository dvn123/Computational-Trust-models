package agents;

import FIPA.DateTime;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import util.*;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.Property;
import org.joda.time.Days;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class FIREAgent extends BaseAnswerAgent {
    MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );
    public static final int RATING_NUMBER_THRESHOLD = 10;

    protected void setup() {
        super.setup();
        System.out.println("Agent "+getLocalName()+" waiting for requests...");
    }

    private ArrayList<Float> getAllDateWeights(ArrayList<FIREInteraction> db) {
        ArrayList<Float> DateWeightsTemp = new ArrayList<Float>();
        float sum = 0;
        for(FIREInteraction f: db) {
            float res = getDateWeight(f.date);
            sum += res;
            DateWeightsTemp.add(res);
        }

        //normalize
        for(Float f: DateWeightsTemp) {
            f = f/sum;
        }

        return DateWeightsTemp;
    }

    private float getRatingNumberReliability(int n, int m) {
        return n > m ? 1 : n/m;
    }

    private float getDeviationReliability(float ti, ArrayList<Float> DateWeights, ArrayList<FIREInteraction> db) {
        float res = 0;
        for(int i = 0; i < DateWeights.size(); i++) {
            res += (DateWeights.get(i)*Math.abs(db.get(i).rating - db.get(i).rating * DateWeights.get(i)))/2;
        }
        return 1 - res;
    }

    private float getDateWeight(Date t1) {
        Date now = new Date();
        int diff = Days.daysBetween(new org.joda.time.DateTime(t1), new org.joda.time.DateTime(now)).getDays(); // => 34
        if(diff < 30) {
            return 1;
        } else if(diff < 365) {
            return 1/(-30 + diff);
        } else {
            return 1/(diff*diff);
        }
    }

    private float getScorePastInteractions(ArrayList<FIREInteraction> db, ArrayList<Float> DateWeights) {
        float res = 0;

        float deviationRating = 0;
        for(int i = 0; i < db.size(); i++) {
            res += db.get(i).rating * DateWeights.get(i);
            deviationRating += (DateWeights.get(i)*Math.abs(db.get(i).rating - db.get(i).rating * DateWeights.get(i)))/2;
        }
        deviationRating = 1 - deviationRating;
        return res;
    }

    private float getPastInteractionsReliability(float ti, ArrayList<Float> DateWeights, ArrayList<FIREInteraction> db) {
        return getRatingNumberReliability(db.size(), RATING_NUMBER_THRESHOLD) * getDeviationReliability(ti, DateWeights, db);
    }

    private float getScoreRole(ArrayList<FIRERule> roles) {
        float sum = 0;
        float res = 0;
        for(FIRERule fr: roles) {
            res += fr.rating*fr.reliability;
            sum += fr.reliability;
        }
        return res/sum;
    }

    private float getScore(ArrayList<FIREInteraction> db) {
        ArrayList<Float> DateWeights = getAllDateWeights(db);
        float res1 = getScorePastInteractions(db, DateWeights);
        float rel1 = getPastInteractionsReliability(res1, DateWeights, db);

        //float res2 = getScoreRole()
        //float component1 = getScore()
        return (Constants.Component1Weight * rel1 * res1)/(Constants.Component1Weight * rel1);
    }

    protected AID getBestWiseAgent(Question question) {
        AID best = null;
        float maxScore = 0;
        for (AID w: wiseAgents) {
            best = getScore(FIREDb.find(w, String.valueOf(question.getOperator()), this)) > maxScore ? w : best;
        }
        FIREDb.addInteraction(new FIREInteraction(this, best, question.getId(), String.valueOf(question.getOperator()), (float) -2));

        return best;
    }
}
