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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

import util.Constants;
import util.FIRERelation;
import util.FIRERule;
import util.WiseData;
import agents.QuestionAgent;
import java.awt.Font;
import java.awt.Toolkit;

public class MainWindow {

	private JFrame frmModelosDeConfianca;
	private DefaultListModel wiseAgentsList;
	private Map wiseAgents = new HashMap<String, WiseData>();
	private WiseData lastWiseAdded = new WiseData();
	private JList list;
	private PrintWriter writer = null;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmModelosDeConfianca.setVisible(true);
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
		frmModelosDeConfianca = new JFrame();
		frmModelosDeConfianca.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
		frmModelosDeConfianca.setTitle("Jogo modelos de confianca");
		frmModelosDeConfianca.setBounds(100, 100, 450, 300);
		frmModelosDeConfianca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmModelosDeConfianca.getContentPane().setLayout(null);

		wiseAgentsList = new DefaultListModel();

		list = new JList(wiseAgentsList);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				int index = list.locationToIndex(evt.getPoint());
			}
		});
		list.setBorder(UIManager.getBorder("FormattedTextField.border"));

		list.setBounds(137, 49, 133, 149);
		frmModelosDeConfianca.getContentPane().add(list);

		JLabel lblWiseAgents = new JLabel("Wise Agents");
		lblWiseAgents.setBounds(137, 12, 110, 23);
		frmModelosDeConfianca.getContentPane().add(lblWiseAgents);

		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String name;
				WiseConfigDialog dialog = new WiseConfigDialog(wiseAgents, lastWiseAdded);
				dialog.setModal(true);
				dialog.setVisible(true);
		
				wiseAgentsList.addElement(lastWiseAdded.getName());
			}
		});
		btnAdd.setBounds(292, 53, 89, 23);
		frmModelosDeConfianca.getContentPane().add(btnAdd);

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
		frmModelosDeConfianca.getContentPane().add(btnRemove);

		JButton btnStartSimulation = new JButton("Start simulation");
		btnStartSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
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

				if (wiseAgents.size() < 1) {
					JOptionPane.showMessageDialog(new JFrame(), "You must add at least one agent!", "ERROR",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				//creating log folder if doesn't exist
				File theDir = new File("log");

				// if the directory does not exist, create it
				if (!theDir.exists()) {
					try{
						theDir.mkdir();
					} catch(SecurityException se){
						se.printStackTrace();
						return;
					}        
				}
				//initializing log
				Calendar cal = Calendar.getInstance();
				Date date = cal.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
				SimpleDateFormat sdf2 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
				try {
					writer = new PrintWriter("log/log_" + sdf.format(date) + ".html", "UTF-8");
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}


				writer.println("<html><head></head><body><h2>" + sdf2.format(date) +"</h2><br> ");
				writer.println("<span><a href=\"#wise-agents\">Wise Agents</a> | <a href=\"#players-agents\">Players Agents</a> | <a href=\"#questions\">Questions</a></span>");
				writer.println("<h1 id=\"wise-agents\">Wise agents</h1><div>");

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
						writer.println(((WiseData)pairs.getValue()).getAgentHtml());
					}
					writer.println("</div>");

					writer.println("<h1 id=\"players-agents\">Players agents</h1><div>");

					//start agent 1
					writer.print("<div>");
					writer.print("<h3><strong>Agent name: </strong>fire</h3>");
					writer.print("<div><p><strong>Algorithm: </strong> FIRE</p>");
					writer.print("</div><br>");
					//end agent 1

					//start agent 2
					writer.print("<div>");
					writer.print("<h3><strong>Agent name: </strong>sinalpha</h3>");
					writer.print("<div><p><strong>Algorithm: </strong> SinAlpha</p>");
					writer.print("</div><br>");
					//end agent 2

					//start agent 3
					writer.print("<div>");
					writer.print("<h3><strong>Agent name: </strong>random</h3>");
					writer.print("<div><p><strong>Algorithm: </strong> Random</p>");
					writer.print("</div><br>");
					//end agent 3

					writer.println("</div>");

					writer.println("<h1 id=\"questions\">Questions</h1><div><ul>");
					writer.flush();
					QuestionAgent.setWriter(writer);

					cc.createNewAgent("fire","agents.FIREAgent" ,null).start();

					cc.createNewAgent("sinalpha","agents.SinalphaAgent" ,null).start();
					cc.createNewAgent("random","agents.BaseAnswerAgent" ,null).start();
					Thread.sleep(100);
					cc.createNewAgent("manela","agents.QuestionAgent" ,null).start();



				} catch (StaleProxyException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 


				//Start chart
				new Thread(new Runnable() {
					@Override
					public void run() {
						javafx.application.Application.launch(AccuracyLineChart.class);
					}
				}).start();
			}
		});
		btnStartSimulation.setBounds(266, 226, 158, 23);
		frmModelosDeConfianca.getContentPane().add(btnStartSimulation);

		JLabel lblWiseData = new JLabel("Players Agents");
		lblWiseData.setBounds(11, 65, 117, 23);
		frmModelosDeConfianca.getContentPane().add(lblWiseData);
		
		JLabel lblLaver = new JLabel(" - FIRE");
		lblLaver.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblLaver.setBounds(30, 84, 98, 28);
		frmModelosDeConfianca.getContentPane().add(lblLaver);
		
		JLabel lblSinalpha = new JLabel(" - Sinalpha");
		lblSinalpha.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblSinalpha.setBounds(30, 109, 98, 28);
		frmModelosDeConfianca.getContentPane().add(lblSinalpha);
		
		JLabel lblRandom = new JLabel(" - Random");
		lblRandom.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblRandom.setBounds(30, 134, 98, 28);
		frmModelosDeConfianca.getContentPane().add(lblRandom);

	}
}
