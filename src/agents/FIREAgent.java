package agents;

import jade.core.AID;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.joda.time.Seconds;

import util.Constants;
import util.FIREDb;
import util.FIREInteraction;
import util.FIRERelation;
import util.FIRERule;
import util.Question;

@SuppressWarnings("serial")
public class FIREAgent extends BaseAnswerAgent {
    
	MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

    public static final int RATING_NUMBER_THRESHOLD = 10;

    public int times_right = 0;
    public int times_wrong = 0;

    private float getDateWeight(Date t1) {
        Date now = new Date();
        int diff = Seconds.secondsBetween(new org.joda.time.DateTime(t1), new org.joda.time.DateTime(now)).getSeconds();
        if(diff <= 30) {
            return 1;
        } else {
            float n = (float) (130*0.01 - 0.01*diff);
            return n < (float) (0.01) ? (float) 0.01 : n;
        }
    }

    private ArrayList<Float> getAllDateWeights(ArrayList<FIREInteraction> db) {
        ArrayList<Float> DateWeightsTemp = new ArrayList<Float>();

        float sum = 0;

        //writeMsg("[1][DateWeights] Array Length: " + db.size());

        for(FIREInteraction f: db) {
            float res = getDateWeight(f.date);
            sum += res;
            DateWeightsTemp.add(res);
            //writeMsg("[1][DateWeights] DateWeights - " + res);
        }

        //writeMsg("[1][DateWeights] Sum: " + sum);

        if(Math.abs(sum) == (0 + 0.001)) {
            DateWeightsTemp.set(0, (float) 1);
            return DateWeightsTemp;
        }

        //normalize.
        for(int i = 0; i < DateWeightsTemp.size(); i++) {
            DateWeightsTemp.set(i, DateWeightsTemp.get(i)/sum);
            //writeMsg("[1][DateWeights] DateWeights - " + DateWeightsTemp.get(i));
        }

        return DateWeightsTemp;
    }

    private float getRatingNumberReliability(int n, int m) {
        //writeMsg("[1][RatingReliability] n - " + n + " m - " + m);
        //writeMsg("[1][RatingReliability] n/m = " + ((float) n) / m);
        return n > m ? 1 : ((float) n)/m;
    }

    private float getDeviationReliability(float ti, ArrayList<Float> DateWeights, ArrayList<FIREInteraction> db) {
        //writeMsg("[1][DeviationReliability] ti - " + ti);
        float res = 0;
        for(int i = 0; i < DateWeights.size(); i++) {
            res += (DateWeights.get(i)*Math.abs(db.get(i).rating - ti))/2;
            writeMsg("[1][DeviationReliability] resTEMP - " + res);
        }
        writeMsg("[1][DeviationReliability] FINAL - " + (1-res));

        return 1 - res;
    }

    private float getScorePastInteractions(ArrayList<FIREInteraction> db, ArrayList<Float> DateWeights) {
        float res = 0;

        float deviationRating = 0;
        for(int i = 0; i < db.size(); i++) {
            res += db.get(i).rating * DateWeights.get(i);
            //writeMsg("[1][Score] res: " + res + " | Added =" + db.get(i).rating +"*" +DateWeights.get(i)) ;
            deviationRating += (DateWeights.get(i)*Math.abs(db.get(i).rating - db.get(i).rating * DateWeights.get(i)))/2;
        }
        deviationRating = 1 - deviationRating;
        return res;
    }

    private float getPastInteractionsReliability(float ti, ArrayList<Float> DateWeights, ArrayList<FIREInteraction> db) {
        writeMsg("[1][Reliability] 1 - " + getRatingNumberReliability(db.size(), RATING_NUMBER_THRESHOLD) + " 2 - " + getDeviationReliability(ti, DateWeights, db));
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
            //writeMsg("[Roles] Testing 1 - " + fr.name1 + " == " + );
            if(fr.name1.equals(name1) && (fr.name2.equals(name2) || fr.name2.equals(""))) {
                rules.add(fr.fireRule);
                //writeMsg("[Roles] Found role " + fr.fireRule.role + " on agent " + fr.name1);
            }
        }
        return rules;
    }

    private float getScore(ArrayList<FIREInteraction> db, AID w) {
        ArrayList<Float> DateWeights = getAllDateWeights(db);
        //writeMsg("[FIREAgent] Calculated DateWeights - " + String.valueOf(DateWeights.get(0)));
        float res1 = 0;
        float rel1 = 0;
        if(db.size() > 0) {
            res1 = getScorePastInteractions(db, DateWeights);
            writeMsg("[1] Calculted PastScore - " + String.valueOf(res1));
            rel1 = getPastInteractionsReliability(res1, DateWeights, db);
            writeMsg("[1] Calculated PastReliability - " + String.valueOf((rel1)));
        }

        float res2 = getScoreRoles(getRoles(w.getLocalName(), this.getLocalName()));
        //writeMsg("Calculted RoleScore - " + String.valueOf(res2));
        float rel2 = getReliabilityRoles(getRoles(w.getLocalName(), this.getLocalName()));
        //writeMsg("Calculated RoleReliability - " + String.valueOf((rel2)));
        //float component1 = getScore()
        //writeMsg("Final - " + (Constants.Component1Weight * rel1 * res1 + Constants.Component2Weight*rel2*res2)/(Constants.Component1Weight * rel1 + Constants.Component2Weight*rel2));
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
        ArrayList<AID> best = new ArrayList<AID>();
        float maxScore = -999;
        float scoreTemp = -1;
        for (AID w: wiseAgents) {
            scoreTemp = getScore(FIREDb.find(w.getLocalName(), String.valueOf(question.getOperator()), this.getLocalName()), w);
            //writeMsg("Length db - " + String.valueOf(FIREDb.find(w.getLocalName(), String.valueOf(question.getOperator()), this.getLocalName()).size()));
            writeMsg("ScoreTemp - " + String.valueOf(scoreTemp) + " - Agent name = " + w.getLocalName() + "\n");
            if (scoreTemp > maxScore) {
                best.clear();
                best.add(w);
                maxScore = scoreTemp;
            } else if(scoreTemp == maxScore) {
                best.add(w);
            }
        }
        Random x = new Random();
        int index = x.nextInt(best.size());
        AID bestFinal =  best.get(index);

        writeMsg("Returned best agent - " + bestFinal.getLocalName());
        FIREDb.addInteraction(new FIREInteraction(this.getLocalName(), bestFinal.getLocalName(), question.getId(), String.valueOf(question.getOperator()), (float) 0));
        return bestFinal;
    }

    protected void handleSolution(ACLMessage ok) {
        Question question = null;
        try {
            question = (Question) ok.getContentObject();
        } catch (UnreadableException e) {
            writeMsg("Error");
        }

        if (ok.getPerformative() == ACLMessage.CONFIRM) {
            times_right++;
            //FIREDb.addInteraction(new FIREInteraction(this, ok.getSender(), question.getId(), String.valueOf(question.getOperator()), (float) 1));
            FIREDb.addRating(question.getId(), (float) 1);
        } else if (ok.getPerformative() == ACLMessage.DISCONFIRM) {
            times_wrong++;
            //FIREDb.addInteraction(new FIREInteraction(this, ok.getSender(), question.getId(), String.valueOf(question.getOperator()), (float) -1));
            FIREDb.addRating(question.getId(), (float) -1);
        } else {
            writeMsg(ok.getSender().getLocalName() + " - estás tolo");
        }
        writeMsg("###################");
        writeMsg("Times Right - " + times_right + ", Times Wrong - " + times_wrong + ", Ratio: " + times_right/((float) times_right + times_wrong));
        writeMsg("###################");
        super.handleSolution(ok);
    }
    
    protected void writeMsg(String msg) {
		if (Constants.logFire)
			System.out.println("Agent "+ getLocalName() + ": " + msg);
	}
}
