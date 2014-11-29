package gui;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import util.WiseData;

public class MainWindow {

	private JFrame frame;
	private DefaultListModel wiseAgentsList;
	private Map wiseAgents = new HashMap<String, WiseData>();
	private WiseData lastWiseAdded = new WiseData();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		wiseAgentsList = new DefaultListModel();
		wiseAgentsList.addElement("Jane Doe");
		wiseAgentsList.addElement("John Smith");
		wiseAgentsList.addElement("Kathy Green");
		
		final JList list = new JList(wiseAgentsList);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				 int index = list.locationToIndex(evt.getPoint());
			}
		});
		list.setBorder(UIManager.getBorder("InternalFrame.border"));
		
		list.setBounds(137, 49, 133, 149);
		frame.getContentPane().add(list);
		
		JLabel lblWiseAgents = new JLabel("Wise Agents");
		lblWiseAgents.setBounds(137, 27, 110, 14);
		frame.getContentPane().add(lblWiseAgents);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String name;
				System.out.println(wiseAgents);
				WiseConfigDialog dialog = new WiseConfigDialog(wiseAgents, lastWiseAdded);
				dialog.setModal(true);
				dialog.setVisible(true);
				System.out.println(wiseAgents);
				wiseAgentsList.addElement(lastWiseAdded.getName());
			}
		});
		btnAdd.setBounds(292, 53, 89, 23);
		frame.getContentPane().add(btnAdd);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Object selectedValue = list.getSelectedValue();
				if(selectedValue != null) {
					System.out.println(selectedValue);
					wiseAgents.remove(selectedValue);
					wiseAgentsList.removeElement(selectedValue);
				}
				else{
					 JOptionPane.showMessageDialog(new JFrame(), "Nothing Selected", "ERROR",
						        JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnRemove.setBounds(292, 87, 89, 23);
		frame.getContentPane().add(btnRemove);
		
		JButton btnStartSimulation = new JButton("Start simulation");
		btnStartSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
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
		    	AgentController rma;
				try {
					rma = cc.createNewAgent("inProcess", 
					 "jade.tools.rma.rma", null);
					
					
					//acceptnewagent alternativa
					rma.start(); 
					//cc.createNewAgent("nabo","agents.WiseAgent" ,null).start();

					Iterator it = wiseAgents.entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry pairs = (Map.Entry)it.next();
				        
				        cc.createNewAgent((String)pairs.getKey(),"agents.WiseAgent" ,((WiseData)pairs.getValue()).getData()).start();
				        System.out.println(pairs.getKey() + " = " + pairs.getValue());
				        it.remove(); // avoids a ConcurrentModificationException
				    }
				    
					
				} catch (StaleProxyException e) {
					e.printStackTrace();
				} 
			}
		});
		btnStartSimulation.setBounds(282, 227, 142, 23);
		frame.getContentPane().add(btnStartSimulation);
		
		JLabel lblWiseData = new JLabel("Wise Agent Data:");
		lblWiseData.setBounds(10, 67, 117, 14);
		frame.getContentPane().add(lblWiseData);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setBounds(10, 103, 46, 14);
		frame.getContentPane().add(lblNewLabel);
		
	}
}
