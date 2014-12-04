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


	//Roles
	static public HashMap<String, FIRERule> roleValues = new HashMap<String, FIRERule>();
	static public ArrayList<FIRERelation> relations = new ArrayList<FIRERelation>();

	//Component Weights
	public static final int Component1Weight = 1;
	public static final int Component2Weight = 1;
	public static final int Component3Weight = 1;
	
	
	public static boolean logManela = false;
	public static boolean logFire = false;
	public static boolean logWise = false;
	public static boolean logWiseTiredness = false;
	public static boolean logBaseAnswer = false;
	
	public static boolean printQuestions = true;
	
	
	public static boolean printRatio = true;
	
	
	
	

}
