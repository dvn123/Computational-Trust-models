import jade.core.Runtime; 
import jade.core.Profile; 
import jade.core.ProfileImpl; 
import jade.wrapper.*;
import java.sql.*;


public class Main {
	static final String JDBC_DRIVER = "org.postgresql.Driver";
	static final String DB_URL = "jdbc:postgresql://localhost/aiad";
	static final String USER = "postgres";
	static final String PASS = "postgres";

    public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch(SQLException se) {
			se.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(stmt!=null)
					stmt.close();
			} catch(SQLException se2){
				// nothing we can do
			}
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}
		}


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
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	// Fire up the agent 
		//How are services started?

        
    }

		//  Database credentials
}
