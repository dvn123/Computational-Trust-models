import jade.core.Runtime; 
import jade.core.Profile; 
import jade.core.ProfileImpl; 
import jade.wrapper.*;
import util.Constants;
import util.FIRERelation;
import util.FIRERule;

import java.sql.*;


public class Main {
    public static void main(String[] args) {
		Constants.roleValues.put("mathematician", new FIRERule("cs_student", (float) 1, (float) 1));
		Constants.roleValues.put("high_school_dropout", new FIRERule("cs_student", (float) -0.5, (float) 0.2));
		Constants.roleValues.put("neanderthal", new FIRERule("cs_student", (float) -1, (float) 1));
		Constants.roleValues.put("cs_student", new FIRERule("cs_student", (float) 0.8, (float) 0.7));
		Constants.roleValues.put("kind", new FIRERule("kind", (float) 0.2, (float) 0.4));
		Constants.roleValues.put("evil", new FIRERule("evil", (float) -0.6, (float) 0.4));

		Constants.relations.add(new FIRERelation("socrates", "", Constants.roleValues.get("evil")));
		Constants.relations.add(new FIRERelation("diogo", "", Constants.roleValues.get("cs_student")));
		Constants.relations.add(new FIRERelation("diogo", "", Constants.roleValues.get("kind")));
		Constants.relations.add(new FIRERelation("villate", "", Constants.roleValues.get("mathematician")));






		// Get a hold on JADE runtime
    	Runtime rt = Runtime.instance(); 
    	// Create a default profile 
    	Profile p = new ProfileImpl(); 
    	// Create a new non-main container, connecting to the default 
    	// main container (i.e. on this host, port 1099) 
    	ContainerController cc = rt.createMainContainer(p); 
    	// Create a new agent, a DummyAgent 
    	// and pass it a reference to an Object 
		//Object reference = new Object();
		//Object args2[] = new Object[1];
		//args2[0]=reference;
    	AgentController dummy;
		try {
			dummy = cc.createNewAgent("inProcess", 
			 "jade.tools.rma.rma", null);
			
			
			//acceptnewagent alternativa
			dummy.start(); 
			//cc.createNewAgent("nabo","agents.WiseAgent" ,null).start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	// Fire up the agent 
		//How are services started?

        
    }

		//  Database credentials
}
