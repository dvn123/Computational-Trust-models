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
            System.out.println("DateWeights - " + f);

        }

        return DateWeightsTemp;
    }

    private float getRatingNumberReliability(int n, int m) {
        System.out.println("[RatingReliability] n - " + n + " m - " + m);
        System.out.println("[RatingReliability] n/m = " + ((float) n) / m);
        return n > m ? 1 : ((float) n)/m;
    }

    private float getDeviationReliability(float ti, ArrayList<Float> DateWeights, ArrayList<FIREInteraction> db) {
        //System.out.println("[DeviationReliability] ti - " + ti);
        float res = 0;
        for(int i = 0; i < DateWeights.size(); i++) {
            res += (DateWeights.get(i)*Math.abs(db.get(i).rating - db.get(i).rating * DateWeights.get(i)))/2;
            //System.out.println("[DeviationReliability] resTEMP - " + res);
        }
        //System.out.println("[DeviationReliability] FINAL - " + (1-res));

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
        System.out.println("[Reliability] 1 - " + getRatingNumberReliability(db.size(), RATING_NUMBER_THRESHOLD) + "\n 2 - " + getDeviationReliability(ti, DateWeights, db));
        return getRatingNumberReliability(db.size(), RATING_NUMBER_THRESHOLD) * getDeviationReliability(ti, DateWeights, db);
    }

    private float getScoreRoles(ArrayList<FIRERule> roles) {
        float sum = 0;
        float res = 0;
        for(FIRERule fr: roles) {
            res += fr.rating*fr.reliability;
            sum += fr.reliability;
        }
        return sum == 0 ? res: res/sum;
    }

    private ArrayList<FIRERule> getRoles(String name1, String name2) {
        ArrayList<FIRERule> rules = new ArrayList<FIRERule>();
        for(FIRERelation fr: Constants.relations) {
            //System.out.println("[Roles] Testing 1 - " + fr.name1 + " == " + );
            if(fr.name1.equals(name1) && (fr.name2.equals(name2) || fr.name2.equals(""))) {
                rules.add(fr.fireRule);
                System.out.println("[Roles] Found role " + fr.fireRule.role + " on agent " + fr.name1);
            }
        }
        return rules;
    }

    private float getScore(ArrayList<FIREInteraction> db, AID w) {
        ArrayList<Float> DateWeights = getAllDateWeights(db);
        //System.out.println("[FIREAgent] Calculated DateWeights - " + String.valueOf(DateWeights.get(0)));
        float res1 = 0;
        float rel1 = 0;
        if(db.size() > 0) {
            res1 = getScorePastInteractions(db, DateWeights);
            System.out.println("[FIREAgent] Calculted PastScore - " + String.valueOf(res1));
            rel1 = getPastInteractionsReliability(res1, DateWeights, db);
            System.out.println("[FIREAgent] Calculated PastReliability - " + String.valueOf((rel1)));
        }

        float res2 = getScoreRoles(getRoles(w.getLocalName(), this.getLocalName()));
        System.out.println("[FIREAgent] Calculted RoleScore - " + String.valueOf(res2));
        float rel2 = getReliabilityRoles(getRoles(w.getLocalName(), this.getLocalName()));
        System.out.println("[FIREAgent] Calculated RoleReliability - " + String.valueOf((rel2)));
        //float component1 = getScore()
        System.out.println("FINAL - " + (Constants.Component1Weight * rel1 * res1 + Constants.Component2Weight*rel2*res2)/(Constants.Component1Weight * rel1 + Constants.Component2Weight*rel2));
        return (Constants.Component1Weight * rel1 * res1 + Constants.Component2Weight*rel2*res2)/(Constants.Component1Weight * rel1 + Constants.Component2Weight*rel2);
    }

    private float getReliabilityRoles(ArrayList<FIRERule> roles) {
        float res = 1;
        for(FIRERule fr: roles ) {
            res = res*fr.reliability;
        }
        return res;
    }

    protected AID getBestWiseAgent(Question question) {
        System.out.println("[FIREAgent] Received question");
        AID best = null;
        float maxScore = -999;
        float scoreTemp = -1;
        for (AID w: wiseAgents) {
            scoreTemp = getScore(FIREDb.find(w.getLocalName(), String.valueOf(question.getOperator()), this.getLocalName()), w);
            System.out.println("Length db - " + String.valueOf(FIREDb.find(w.getLocalName(), String.valueOf(question.getOperator()), this.getLocalName()).size()));
            System.out.println("ScoreTemp - " + String.valueOf(scoreTemp));
            System.out.println("MaxScore - " + String.valueOf(maxScore));
            if (scoreTemp > maxScore) {
                best =  w;
                maxScore = scoreTemp;
            }
        }
        if(best == null)
            System.out.println("NULL");
        //System.out.println("Returned best agent - ");
        FIREDb.addInteraction(new FIREInteraction(this.getLocalName(), best.getLocalName(), question.getId(), String.valueOf(question.getOperator()), (float) 0));
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
            System.out.println(ok.getSender().getLocalName() + " - estás tolo");
        }
        super.handleSolution(ok);
    }
}
