package util;

import java.util.ArrayList;
import java.util.HashMap;

public class Constants {

	//Players' Agents
	static public String SERVICE_NAME_PLAYER = "QuemQuerSerMilionario-player";
	static public String SERVICE_DESCRIPTION_TYPE_PLAYER = "QuemQuerSerMilionario-player-type";
	static public String SERVICE_DESCRIPTION_ONTOLOGY_PLAYER = "QuemQuerSerMilionario-player-ontology";

	static public String SERVICE_DESCRIPTION_TYPE_INFORMATION_SOURCE_FIRE = "QuemQuerSerMilionario-information-source-fire";
	static public String SERVICE_DESCRIPTION_ONTOLOGY_INFORMATION_SOURCE_FIRE = "QuemQuerSerMilionario-information-source-fire-ontology";

	//Wise Agents
	static public String SERVICE_NAME_WISE = "QuemQuerSerMilionario-wise";
	static public String SERVICE_DESCRIPTION_TYPE_WISE = "QuemQuerSerMilionario-wise-type";
	static public String SERVICE_DESCRIPTION_ONTOLOGY_WISE = "QuemQuerSerMilionario-wise-ontology";

	static public String SOLUTION_ONTOLOGY = "solution";
	static public String WISE_CONVERSATION_ONTOLOGY = "wise-conversation-ontology";

	static public String SPLIT_STRING = "@at@";

	//Roles
	static public HashMap<String, FIRERule> roleValues = new HashMap<String, FIRERule>();
	static public ArrayList<FIRERelation> relations = new ArrayList<FIRERelation>();

	//Component Weights
	public static final float Component1Weight = (float)0.7;
	public static final float Component2Weight = (float)0.3;


	public static boolean logManela = false;
	public static boolean logFire = false;
	public static boolean logWise = false;
	public static boolean logWiseTiredness = false;
	public static boolean logBaseAnswer = false;

	public static boolean printQuestions = false;

	public static boolean printRatio = false;

	public static final double ALPHA0_INIT=(double)((3.0*Math.PI)/2.0);
	public static final double LAMBDA_POS_INIT=(double)1.00;
	public static final double LAMBDA_NEG_INIT=(double)-1.50;
	public static final double OMEGA_INIT=(double)(Math.PI/12.0);
	public static final double RO_INIT=(double)0.50;


	public static final int NUMBER_OF_QUESTIONS = 100;
	public static final int TIME_BETWEEN_QUESTIONS = 900;



}
