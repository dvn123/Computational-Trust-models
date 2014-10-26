import jade.core.Runtime; 
import jade.core.Profile; 
import jade.core.ProfileImpl; 
import jade.wrapper.*; 

public class Main {

    public static void main(String[] args) {
    	// Get a hold on JADE runtime 
    	Runtime rt = Runtime.instance(); 
    	// Create a default profile 
    	Profile p = new ProfileImpl(); 
    	// Create a new non-main container, connecting to the default 
    	// main container (i.e. on this host, port 1099) 
    	ContainerController cc = rt.createMainContainer(p); 
    	// Create a new agent, a DummyAgent 
    	// and pass it a reference to an Object 
//    	Object reference = new Object(); 
//    	Object args2[] = new Object[1]; 
//    	args2[0]=reference; 
    	AgentController dummy;
		try {
			dummy = cc.createNewAgent("inProcess", 
			 "jade.tools.rma.rma", null);
			//acceptnewagent alternativa
			dummy.start(); 
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	// Fire up the agent 
		//How are services started?

        
    }
}
