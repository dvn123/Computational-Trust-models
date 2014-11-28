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
            writeMsg("DateWeights - " + f);

        }

        return DateWeightsTemp;
    }

    private float getRatingNumberReliability(int n, int m) {
        writeMsg("[RatingReliability] n - " + n + " m - " + m);
        writeMsg("[RatingReliability] n/m = " +  ((float) n)/m);
        return n > m ? 1 : ((float) n)/m;
    }

    private float getDeviationReliability(float ti, ArrayList<Float> DateWeights, ArrayList<FIREInteraction> db) {
        writeMsg("[DeviationReliability] ti - " + ti);
        float res = 0;
        for(int i = 0; i < DateWeights.size(); i++) {
            res += (DateWeights.get(i)*Math.abs(db.get(i).rating - db.get(i).rating * DateWeights.get(i)))/2;
            writeMsg("[DeviationReliability] resTEMP - " + res);
        }
        writeMsg("[DeviationReliability] FINAL - " + (1-res));

        return 1 - res;
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
        writeMsg("[Reliability] 1 - " +  getRatingNumberReliability(db.size(), RATING_NUMBER_THRESHOLD) + "\n 2 - " + getDeviationReliability(ti, DateWeights, db));
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
        writeMsg("[FIREAgent] Calculating Score");
        ArrayList<Float> DateWeights = getAllDateWeights(db);
        //writeMsg("[FIREAgent] Calculated DateWeights - " + String.valueOf(DateWeights.get(0)));
        float res1 = getScorePastInteractions(db, DateWeights);
        writeMsg("[FIREAgent] Calculted PastScore - " + String.valueOf(res1));
        float rel1 = getPastInteractionsReliability(res1, DateWeights, db);
        writeMsg("[FIREAgent] Calculated PastReliability - " + String.valueOf((rel1)));

        //float res2 = getScoreRole()
        //float component1 = getScore()
        writeMsg("FINAL - " + String.valueOf((Constants.Component1Weight * rel1 * res1)/(Constants.Component1Weight * rel1)));
        return (Constants.Component1Weight * rel1 * res1)/(Constants.Component1Weight * rel1);
    }

    protected AID getBestWiseAgent(Question question) {
        writeMsg("[FIREAgent] Received question");
        AID best = null;
        float maxScore = -999;
        float scoreTemp = -1;
        for (AID w: wiseAgents) {
            if(FIREDb.find(w.getName(), String.valueOf(question.getOperator()), this.getName()).size() > 0) {
                scoreTemp = getScore(FIREDb.find(w.getName(), String.valueOf(question.getOperator()), this.getName()));
            } else {
                scoreTemp = 0;
            }
            writeMsg("Length db - " + String.valueOf(FIREDb.find(w.getName(), String.valueOf(question.getOperator()), this.getName()).size()));
            writeMsg("ScoreTemp - " + String.valueOf(scoreTemp));
            writeMsg("MaxScore - " + String.valueOf(maxScore));
            if (scoreTemp > maxScore) {
                best =  w;
                maxScore = scoreTemp;
            }
        }
        if(best == null)
            writeMsg("NULL");
        //writeMsg("Returned best agent - ");
        FIREDb.addInteraction(new FIREInteraction(this.getName(), best.getName(), question.getId(), String.valueOf(question.getOperator()), (float) 0));
        return best;
    }

    protected void handleSolution(ACLMessage ok) {
        Question question = null;
        try {
            question = (Question) ok.getContentObject();
        } catch (UnreadableException e) {
            System.out.println("Error");
        }
        if (ok.getPerformative() == ACLMessage.CONFIRM) {
            //FIREDb.addInteraction(new FIREInteraction(this, ok.getSender(), question.getId(), String.valueOf(question.getOperator()), (float) 1));
            FIREDb.addRating(question.getId(), (float) 1);
        } else if (ok.getPerformative() == ACLMessage.DISCONFIRM) {
            //FIREDb.addInteraction(new FIREInteraction(this, ok.getSender(), question.getId(), String.valueOf(question.getOperator()), (float) -1));
            FIREDb.addRating(question.getId(), (float) -1);
        } else {
            writeMsg(ok.getSender().getLocalName() + " - estás tolo");
        }
        super.handleSolution(ok);
    }
}
